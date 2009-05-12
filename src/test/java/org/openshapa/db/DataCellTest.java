package org.openshapa.db;

import org.openshapa.db.MatrixVocabElement.MatrixType;
import java.io.PrintStream;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class DataCellTest extends CellTest {

    Database db;
    DataCell cell;
    long colID;
    DataColumn col;

    private PrintStream outStream;
    private boolean verbose;

    public DataCellTest() {
    }

    @Before
    public void setUp() throws SystemErrorException, LogicErrorException {
        db = new ODBCDatabase();

        col = new DataColumn(db, "integer", MatrixVocabElement.MatrixType.INTEGER);
        colID = db.addColumn(col);
        col = db.getDataColumn(colID);

        Matrix val = Matrix.Construct(db,
                                      col.getItsMveID(),
                                      IntDataValue.Construct(db, 3));
        cell = DataCell.Construct(db, colID, col.getItsMveID(), 0, 0, val);

        outStream = System.out;
        verbose = true;
    }

    @After
    public void tearDown() {
    }

    @Override
    public DBElement getInstance() {
        return cell;
    }

    @Test
    public void testEquals() throws SystemErrorException {
        DataCell cell1 = (DataCell) cell.clone();
        DataCell cell2 = (DataCell) cell.clone();

        Matrix val = Matrix.Construct(db,
                                      col.getItsMveID(),
                                      IntDataValue.Construct(db, 4));
        DataCell cell3 = DataCell.Construct(db, colID, col.getItsMveID(),
                                            0, 0, val);

        super.testEquals(cell, cell1, cell2, cell3);
    }

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) Three argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs to the
     *         three argument constructor.  Verify that all fields of the
     *         DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the three argument constructor fails on invalid
     *         input.
     *
     * 2) Four argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs along with
     *         a comment to the four argument constructor.  Verify that all
     *         fields of the DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the constructor fails when passed invalid data.
     *
     * 3) Seven argument constructor:
     *
     *      As per four argument constructor, save that an onset, offset and
     *      a cell value value are supplied to the constructor.  Verify that
     *      the onset, offset, and value appear in the DataCell.
     *
     *      Verify that the constructor fails when passed invalid data.
     *
     * 4) Copy constructor:
     *
     *      a) Construct DataCells of all types using the 3, 4, and 7 argument
     *         constructors with a variety of values.
     *
     *         Now use the copy constructor to create a copies of the
     *         DataCells, and verify that the copies are correct.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that can be tested unless we go an manualy break some
     *         DataCells created with the other constructors.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsMveID(), getItsMveType() getOnset(),
     *      getOffset(), getVal(), setOnset(), setOffset(), and setVal()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the Cell.TestAccessorMethods()
     *      method.
     *
     *      Verify that the accessors fail on invalid data.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

// TODO - Refactor to JUnit style
// Currently just a copy of John's tests with small refactors to handle
// some private access to methods and fields.
// Asserts at the end if anything fails then you need to review the output
// to locate the cause of the fail.

    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing 3 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

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

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
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

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
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
                f_cell = new DataCell(db, f_colID, f_mveID);
                i_cell = new DataCell(db, i_colID, i_mveID);
                m_cell = new DataCell(db, m_colID, m_mveID);
                n_cell = new DataCell(db, n_colID, n_mveID);
                p_cell = new DataCell(db, p_colID, p_mveID);
                t_cell = new DataCell(db, t_colID, t_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }

                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }

                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }

                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }

                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }

                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                     f_cell,
                                     "f_cell",
                                     null,
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     i_cell,
                                     "i_cell",
                                     null,
                                     i_colID,
                                     i_mveID,
                                     MatrixVocabElement.MatrixType.INTEGER,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, i_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     m_cell,
                                     "m_cell",
                                     null,
                                     m_colID,
                                     m_mveID,
                                     MatrixVocabElement.MatrixType.MATRIX,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, m_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     n_cell,
                                     "n_cell",
                                     null,
                                     n_colID,
                                     n_mveID,
                                     MatrixVocabElement.MatrixType.NOMINAL,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, n_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     p_cell,
                                     "p_cell",
                                     null,
                                     p_colID,
                                     p_mveID,
                                     MatrixVocabElement.MatrixType.PREDICATE,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, p_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     t_cell,
                                     "t_cell",
                                     null,
                                     t_colID,
                                     t_mveID,
                                     MatrixVocabElement.MatrixType.TEXT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, t_mveID),
                                     outStream,
                                     verbose);
            }
        }

        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null, f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a db that doesn't match the supplied IDs */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(new ODBCDatabase(), f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, DBIndex.INVALID_ID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "invalid) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "invalid) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "invalid) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an ID mismatch */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "i_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a bad colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_mveID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                         "i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                         "i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                "i_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a bad mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, i_colID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_colID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_colID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "i_colID) failed to throw a system error " +
                                "exception.\n");
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

    } /* DataCell::Test3ArgConstructor() */


    /**
     * Test4ArgConstructor()
     *
     * Run a battery of tests on the four argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test4ArgConstructor()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing 4 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

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

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
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

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
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
                c = new DataCell(db, null, f_colID, f_mveID);
                f_cell = new DataCell(db, "f_cell", f_colID, f_mveID);
                i_cell = new DataCell(db, "i_cell", i_colID, i_mveID);
                m_cell = new DataCell(db, "m_cell", m_colID, m_mveID);
                n_cell = new DataCell(db, "n_cell", n_colID, n_mveID);
                p_cell = new DataCell(db, "p_cell", p_colID, p_mveID);
                t_cell = new DataCell(db, "t_cell", t_colID, t_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c == null ) ||
                 ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c == null )
                    {
                        outStream.printf("c allocation failed.\n");
                    }

                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }

                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }

                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }

                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }

                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }

                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                     c,
                                     "c",
                                     null,
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     f_cell,
                                     "f_cell",
                                     "f_cell",
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     i_cell,
                                     "i_cell",
                                     "i_cell",
                                     i_colID,
                                     i_mveID,
                                     MatrixVocabElement.MatrixType.INTEGER,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, i_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     m_cell,
                                     "m_cell",
                                     "m_cell",
                                     m_colID,
                                     m_mveID,
                                     MatrixVocabElement.MatrixType.MATRIX,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, m_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     n_cell,
                                     "n_cell",
                                     "n_cell",
                                     n_colID,
                                     n_mveID,
                                     MatrixVocabElement.MatrixType.NOMINAL,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, n_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     p_cell,
                                     "p_cell",
                                     "p_cell",
                                     p_colID,
                                     p_mveID,
                                     MatrixVocabElement.MatrixType.PREDICATE,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, p_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     t_cell,
                                     "t_cell",
                                     "t_cell",
                                     t_colID,
                                     t_mveID,
                                     MatrixVocabElement.MatrixType.TEXT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, t_mveID),
                                     outStream,
                                     verbose);
            }
        }

        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null, "valid", f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a db that doesn't match the supplied IDs */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(new ODBCDatabase(), "valid", f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", DBIndex.INVALID_ID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an ID mismatch */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a bad colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_mveID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a bad mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, i_colID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) failed to throw a system " +
                                "error exception.\n");
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
    } /* DataCell::Test4ArgConstructor() */


    /**
     * Test7ArgConstructor()
     *
     * Run a battery of tests on the seven argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test7ArgConstructor()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing 7 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

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

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
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

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }

                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }

                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }

                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }

                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }

                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }

                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
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
                // The tests here may seem a bit cursory.  However, we have
                // tested the TimeStamp and Matrix classes extensively
                // elsewhere.  Thus all that we need to do here is verify that
                // the desirecd initializations are being passed through
                // correctly.

                f_cell = new DataCell(db, "f_cell", f_colID, f_mveID,
                                      f_onset, f_offset, f_matrix);
                i_cell = new DataCell(db, "i_cell", i_colID, i_mveID,
                                      i_onset, i_offset, i_matrix);
                m_cell = new DataCell(db, "m_cell", m_colID, m_mveID,
                                      m_onset, m_offset, m_matrix);
                n_cell = new DataCell(db, "n_cell", n_colID, n_mveID,
                                      n_onset, n_offset, n_matrix);
                p_cell = new DataCell(db, "p_cell", p_colID, p_mveID,
                                      p_onset, p_offset, p_matrix);
                t_cell = new DataCell(db, "t_cell", t_colID, t_mveID,
                                      t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }

                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }

                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }

                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }

                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }

                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell,
                                       "f_cell",
                                       "f_cell",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell,
                                       "i_cell",
                                       "i_cell",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell,
                                       "m_cell",
                                       "m_cell",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell,
                                       "n_cell",
                                       "n_cell",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                      p_cell,
                                      "p_cell",
                                      "p_cell",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell,
                                       "t_cell",
                                       "t_cell",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }

        /* Now verify that the constructor fails on invalid input */

        if ( failures == 0 )
        {
            /* verify that it fails on a null db */
            failures += Verify7ArgConstructorFailure(null, "null",
                                                     "valid", "\"valid\"",
                                                     f_colID, "f_colID",
                                                     f_mveID, "f_mveID",
                                                     f_onset, "f_onset",
                                                     f_offset, "f_offset",
                                                     f_matrix, "f_matrix",
                                                     outStream, verbose);

            /* verify that it fails on a db that doesn't match the supplied IDs */
            failures += Verify7ArgConstructorFailure(new ODBCDatabase(), "alt_db",
                                                     "valid", "\"valid\"",
                                                     f_colID, "f_colID",
                                                     f_mveID, "f_mveID",
                                                     f_onset, "f_onset",
                                                     f_offset, "f_offset",
                                                     f_matrix, "f_matrix",
                                                     outStream, verbose);

            /* verify that it fails on an invalid colID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             DBIndex.INVALID_ID, "invalid_id",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on an invalid mveID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             DBIndex.INVALID_ID, "invalid_id",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on an ID mismatch */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             i_mveID, "i_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a bad colID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_mveID, "f_mveID",
                                             i_mveID, "i_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a bad mveID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             i_colID, "i_colID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null onset */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             null, "null",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null offset */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             null, "null",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null value */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             null, "null",
                                             outStream, verbose);

            /* finally, verify failure on a fargID mismatch */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             i_matrix, "i_matrix",
                                             outStream, verbose);

            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             m_colID, "m_colID",
                                             m_mveID, "m_mveID",
                                             m_onset, "m_onset",
                                             m_offset, "m_offset",
                                             p_matrix, "p_matrix",
                                             outStream, verbose);
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

        //return pass;
        assertTrue(pass);

    } /* DataCell::Test7ArgConstructor() */


    /**
     * TestAccessorMethods()
     *
     * Verify that the accessors supported by the DataCell class function
     * correctly when run on the supplied instance of DataCell.
     *
     *                                               -- 12/4/07
     *
     * Changes:
     *
     *    - None
     */
    public static int TestAccessorMethods(DataCell testCell,
                                  Database initDB,
                                  String initComment,
                                  long initItsColID,
                                  long initItsMveID,
                                  MatrixVocabElement.MatrixType initItsMveType,
                                  int initOrd,
                                  TimeStamp initOnset,
                                  TimeStamp initOffset,
                                  Matrix initVal,
                                  java.io.PrintStream outStream,
                                  boolean verbose,
                                  String desc)
        throws SystemErrorException
    {
        int failures = 0;
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        TimeStamp newOnset1 = null;
        TimeStamp newOnset2 = null;
        TimeStamp newOffset1 = null;
        TimeStamp newOffset2 = null;
        FloatDataValue fdv = null;
        IntDataValue idv = null;
        NominalDataValue ndv = null;
        PredDataValue pdv = null;
        QuoteStringDataValue qdv = null;
        TextStringDataValue tdv = null;
        TimeStampDataValue tsdv = null;
        UndefinedDataValue udv = null;
        Matrix newVal1 = null;
        Matrix newVal2 = null;
        Matrix newVal3 = null;
        Matrix newVal4 = null;
        Matrix newVal5 = null;
        Matrix newVal6 = null;
        Matrix newVal7 = null;
        FormalArgument farg = null;
        long pve10ID = DBIndex.INVALID_ID;
        long pve11ID = DBIndex.INVALID_ID;
        long pve12ID = DBIndex.INVALID_ID;
        PredicateVocabElement pve10 = null;
        PredicateVocabElement pve11 = null;
        PredicateVocabElement pve12 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;

        if ( testCell == null )
        {
            failures++;

            outStream.printf(
                    "DataCell::TestAccessors(): testCell null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;

            outStream.printf("DataCell::TestAccessors(): desc null on entry.\n");
        }

        failures += CellTest.TestAccessorMethods(testCell, initDB, initComment,
                                             initItsColID, initOrd,
                                             outStream, verbose, desc);


        /* test getItsMveID() */

        if ( testCell.getItsMveID() != initItsMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getItsMveID() = %d != %d = initItsMveID.\n",
                    desc, testCell.getItsMveID(), initItsMveID);
            }
        }


        /* test getItsMveType() */

        if ( testCell.getItsMveType() != initItsMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.getItsMveType() = %s != %s = initItsMveType.\n",
                        desc, testCell.getItsMveType().toString(),
                        initItsMveType.toString());
            }
        }


        /* test getOnset() / setOnset() */

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOnset(),
                                                  initOnset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "initOnset");

        newOnset1 = new TimeStamp(testCell.getDB().getTicks(),
                                  60 * testCell.getDB().getTicks());

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOnset(newOnset1);

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
                    outStream.printf(
                            "testCell.setOnset(newOnset1) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset1,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset1");

        /* here we try to set the onset, using a time stamp with a different
         * tps setting.  We should do the conversion automatically.
         */
        newOnset2 = new TimeStamp(testCell.getDB().getTicks() * 2,
                                  120 * testCell.getDB().getTicks() * 2);

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOnset(newOnset2);

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
                    outStream.printf(
                            "testCell.setOnset(newOnset2) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset2) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* do the conversion on our test time stamp */
        newOnset2.setTPS(testCell.getDB().getTicks());

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset2(1)");

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOnset(null);

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
                if ( ! completed )
                {
                    outStream.printf("testCell.setOnset(null) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(null) failed to " +
                                      "throw a system error exception.\n");
                }
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset2(2)");



        /* test getOffset() / setOffset() */

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOffset(),
                                                  initOffset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "initOffset");

        newOffset1 = new TimeStamp(testCell.getDB().getTicks(),
                                   60 * 60 * testCell.getDB().getTicks());

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOffset(newOffset1);

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
                    outStream.printf("testCell.setOffset(newOffset1) " +
                                     "failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(newOffset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset1,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset1");

        /* here we try to set the offset, using a time stamp with a different
         * tps setting.  We should do the conversion automatically.
         */
        newOffset2 = new TimeStamp(testCell.getDB().getTicks() / 2,
                                   240 * testCell.getDB().getTicks() / 2);

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOffset(newOffset2);

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
                    outStream.printf("testCell.setOffset(newOffset2) " +
                                     "failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(newOffset2) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* do the conversion on our test time stamp */
        newOffset2.setTPS(testCell.getDB().getTicks());

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset2(1)");

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOffset(null);

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
                if ( ! completed )
                {
                    outStream.printf("testCell.setOffset(null) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(null) failed to " +
                                      "throw a system error exception.\n");
                }
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset2(2)");

       /* Now test getVal() / setVal().
        *
        * At first glance, the tests here are not as exhaustive as one might
        * think they should be.  However, the value passed to the setVal()
        * routine must be a matrix, and the Matrix class has extensive error
        * checking on the replaceArg() routine, which should prevent
        * construction of an invalid value to pass to the setVal() method.
        * This code is tested extensively in the Matrix test code.
        *
        * However, it is also possible that a change has been made in the
        * target MVE, and that this change has not been propagated to this
        * particular DataCell.  To detect this, setVal() calls
        * Matrix.validateMatrix() before assigning the value.
        *
        * Assuming that Matrix.validateMatrix() works correctly, it will throw
        * a system error if an invalid value is received.
        *
        * Given the difficulty of constructing invalid value matricies, we don't
        * bother with such tests here -- the real testing is done in Matrix.
        */

        /* start by setting up some test predicates that we may need */

        completed = false;
        threwSystemErrorException = false;

        try
        {
            if ( testCell.getDB().predVEExists("pve10") )
            {
                pve10 = testCell.getDB().getPredVE("pve10");
                pve10ID = pve10.getID();
            }
            else
            {
                pve10 = new PredicateVocabElement(testCell.getDB(), "pve10");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg1>");
                pve10.appendFormalArg(farg);
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg2>");
                pve10.appendFormalArg(farg);
                pve10ID = testCell.getDB().addPredVE(pve10);
                // get a copy of the databases version of pve10 with ids assigned
                pve10 = testCell.getDB().getPredVE(pve10ID);
            }

            if ( testCell.getDB().predVEExists("pve11") )
            {
                pve11 = testCell.getDB().getPredVE("pve11");
                pve11ID = pve11.getID();
            }
            else
            {
                pve11 = new PredicateVocabElement(testCell.getDB(), "pve11");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg>");
                pve11.appendFormalArg(farg);
                pve11ID = testCell.getDB().addPredVE(pve11);
                // get a copy of the databases version of pve11 with ids assigned
                pve11 = testCell.getDB().getPredVE(pve11ID);
            }

            if ( testCell.getDB().predVEExists("pve12") )
            {
                pve12 = testCell.getDB().getPredVE("pve12");
                pve12ID = pve12.getID();
            }
            else
            {
                pve12 = new PredicateVocabElement(testCell.getDB(), "pve12");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg>");
                pve12.appendFormalArg(farg);
                pve12ID = testCell.getDB().addPredVE(pve12);
                // get a copy of the databases version of pve12 with ids assigned
                pve12 = testCell.getDB().getPredVE(pve12ID);
            }

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.getMessage();
        }

        if ( ( pve10 == null ) ||
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
                if ( ( pve10 == null ) ||
                     ( pve10ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve10.\n");
                }

                if ( ( pve11 == null ) ||
                     ( pve11ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve11.\n");
                }

                if ( ( pve12 == null ) ||
                     ( pve12ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve12.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "testCell.setOnset(newOnset1) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), initVal,
                                            outStream, verbose,
                                            desc + ".getVal()", "initVal");

        if ( testCell.getVal() == testCell.getVal() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.getVal() isn't creating unique copies of val.\n",
                        desc);
            }
        }

        switch(testCell.getItsMveType())
        {
            case FLOAT:
                newVal1 = testCell.getVal();
                fdv = (FloatDataValue)(newVal1.getArg(0));
                fdv.setItsValue(3.14159);
                newVal1.replaceArg(0, fdv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(f)");
                break;

            case INTEGER:
                newVal1 = testCell.getVal();
                idv = (IntDataValue)newVal1.getArg(0);
                idv.setItsValue(28);
                newVal1.replaceArg(0, idv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(i)");
                break;

            case MATRIX:
                newVal1 = testCell.getVal();
                fdv = new FloatDataValue(testCell.getDB(),
                                         newVal1.getArg(0).getItsFargID(),
                                         22.2);
                newVal1.replaceArg(0, fdv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(m-f)");

                newVal2 = testCell.getVal();
                idv = new IntDataValue(testCell.getDB(),
                                       newVal2.getArg(0).getItsFargID(),
                                       33);
                newVal2.replaceArg(0, idv);
                testCell.setVal(newVal2);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal2,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal2(m-i)");

                newVal3 = testCell.getVal();
                ndv = new NominalDataValue(testCell.getDB(),
                                           newVal3.getArg(0).getItsFargID(),
                                           "another_nominal");
                newVal3.replaceArg(0, ndv);
                testCell.setVal(newVal3);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal3,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal3(m-n)");

                // construct the predicate:
                //
                //      pve10(pve11(pve12(j_q_nominal)), "just a quote string")
                //
                p2 = new Predicate(testCell.getDB(), pve12ID);
                ndv = new NominalDataValue(testCell.getDB(),
                                           p2.getArg(0).getItsFargID(),
                                           "j_q_nominal");
                p2.replaceArg(0, ndv);

                p1 = new Predicate(testCell.getDB(), pve11ID);
                pdv = new PredDataValue(testCell.getDB(),
                                        p1.getArg(0).getItsFargID(),
                                        p2);
                p1.replaceArg(0, pdv);

                p0 = new Predicate(testCell.getDB(), pve10ID);
                pdv = new PredDataValue(testCell.getDB(),
                                        p0.getArg(0).getItsFargID(),
                                        p1);
                p0.replaceArg(0, pdv);
                qdv = new QuoteStringDataValue(testCell.getDB(),
                                               p0.getArg(1).getItsFargID(),
                                               "just a quote string");
                p0.replaceArg(1, qdv);

                newVal4 = testCell.getVal();
                pdv = new PredDataValue(testCell.getDB(),
                                        newVal4.getArg(0).getItsFargID(),
                                        p0);
                newVal4.replaceArg(0, pdv);
                testCell.setVal(newVal4);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal4,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal4(m-p)");

                newVal5 = testCell.getVal();
                tsdv = new TimeStampDataValue(testCell.getDB(),
                          newVal5.getArg(0).getItsFargID(),
                          new TimeStamp(testCell.getDB().getTicks(), 60 * 60));
                newVal5.replaceArg(0, tsdv);
                testCell.setVal(newVal5);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal5,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal5(m-ts)");

                newVal6 = testCell.getVal();
                qdv = new QuoteStringDataValue(testCell.getDB(),
                                               newVal6.getArg(0).getItsFargID(),
                                               "another q-string");
                newVal6.replaceArg(0, qdv);
                testCell.setVal(newVal6);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal6,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal6(m-qs)");

                newVal7 = testCell.getVal();
                farg = (FormalArgument)(testCell.getDB().idx.getElement(
                                             newVal7.getArg(0).getItsFargID()));
                udv = new UndefinedDataValue(testCell.getDB(),
                                             newVal7.getArg(0).getItsFargID(),
                                             farg.getFargName());
                newVal7.replaceArg(0, udv);
                testCell.setVal(newVal7);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal7,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal7(m-u)");
                break;

            case NOMINAL:
                newVal1 = testCell.getVal();
                ndv = (NominalDataValue)newVal1.getArg(0);
                ndv.setItsValue("an_unlikely_nominal");
                newVal1.replaceArg(0, ndv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(n)");
                break;

            case PREDICATE:
                // start by constructing the predicate:
                //
                //      pve10(pve11(pve12(33)), 11.1)
                //
                p2 = new Predicate(testCell.getDB(), pve12ID);
                idv = new IntDataValue(testCell.getDB(),
                                       p2.getArg(0).getItsFargID(),
                                       33);
                p2.replaceArg(0, idv);

                p1 = new Predicate(testCell.getDB(), pve11ID);
                pdv = new PredDataValue(testCell.getDB(),
                                        p1.getArg(0).getItsFargID(),
                                        p2);
                p1.replaceArg(0, pdv);

                p0 = new Predicate(testCell.getDB(), pve10ID);
                pdv = new PredDataValue(testCell.getDB(),
                                        p0.getArg(0).getItsFargID(),
                                        p1);
                p0.replaceArg(0, pdv);
                fdv = new FloatDataValue(testCell.getDB(),
                                         p0.getArg(1).getItsFargID(),
                                         11.1);
                p0.replaceArg(1, fdv);

                newVal1 = testCell.getVal();
                pdv = new PredDataValue(testCell.getDB(),
                                        newVal1.getArg(0).getItsFargID(),
                                        p0);
                newVal1.replaceArg(0, pdv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(p)");
                break;

            case TEXT:
                newVal1 = testCell.getVal();
                tdv = (TextStringDataValue)newVal1.getArg(0);
                tdv.setItsValue("a random text string.");
                newVal1.replaceArg(0, tdv);
                testCell.setVal(newVal1);
                failures =+ MatrixTest.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose,
                                            desc + ".getVal()", "newVal1(t15)");
                break;

        }

        return failures;

    } /* DataCell::TestAccessorMethods() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessor methods for this class.
     *
     *                                               -- 12/4/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing class DataCell accessors                                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell c = null;

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

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
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

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }

                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }

                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }

                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }

                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }

                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }

                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* now allocate the base cells for the accessor tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID,
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID,
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) ||
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }

                    if ( ( i_cell0 == null ) ||
                         ( i_cell1 == null ) ||
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }

                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) ||
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }

                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) ||
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }

                    if ( ( p_cell0 == null ) ||
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }

                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }

        if ( failures == 0 )
        {
            /* run accessor tests on accessors */

            failures += TestAccessorMethods(f_cell0, db, null, f_colID,
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, f_mveID),
                    outStream, verbose, "f_cell0");

            failures += TestAccessorMethods(f_cell1, db, "f_cell1", f_colID,
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, f_mveID),
                    outStream, verbose, "f_cell1");

            failures += TestAccessorMethods(f_cell2, db, "f_cell2", f_colID,
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    f_onset, f_offset, f_matrix,
                    outStream, verbose, "f_cell2");


            failures += TestAccessorMethods(i_cell0, db, null, i_colID,
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, i_mveID),
                    outStream, verbose, "i_cell0");

            failures += TestAccessorMethods(i_cell1, db, "i_cell1", i_colID,
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, i_mveID),
                    outStream, verbose, "i_cell1");

            failures += TestAccessorMethods(i_cell2, db, "i_cell2", i_colID,
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    i_onset, i_offset, i_matrix,
                    outStream, verbose, "i_cell2");


            failures += TestAccessorMethods(m_cell0, db, null, m_colID,
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, m_mveID),
                    outStream, verbose, "m_cell0");

            failures += TestAccessorMethods(m_cell1, db, "m_cell1", m_colID,
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, m_mveID),
                    outStream, verbose, "m_cell1");

            failures += TestAccessorMethods(m_cell2, db, "m_cell2", m_colID,
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    m_onset, m_offset, m_matrix,
                    outStream, verbose, "m_cell2");


            failures += TestAccessorMethods(n_cell0, db, null, n_colID,
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, n_mveID),
                    outStream, verbose, "n_cell0");

            failures += TestAccessorMethods(n_cell1, db, "n_cell1", n_colID,
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, n_mveID),
                    outStream, verbose, "n_cell1");

            failures += TestAccessorMethods(n_cell2, db, "n_cell2", n_colID,
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    n_onset, n_offset, n_matrix,
                    outStream, verbose, "n_cell2");


            failures += TestAccessorMethods(p_cell0, db, null, p_colID,
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, p_mveID),
                    outStream, verbose, "p_cell0");

            failures += TestAccessorMethods(p_cell1, db, "p_cell1", p_colID,
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, p_mveID),
                    outStream, verbose, "p_cell1");

            failures += TestAccessorMethods(p_cell2, db, "p_cell2", p_colID,
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    p_onset, p_offset, p_matrix,
                    outStream, verbose, "p_cell2");


            failures += TestAccessorMethods(t_cell0, db, null, t_colID,
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, t_mveID),
                    outStream, verbose, "t_cell0");

            failures += TestAccessorMethods(t_cell1, db, "t_cell1", t_colID,
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    new TimeStamp(db.getTicks(), 0),
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, t_mveID),
                    outStream, verbose, "t_cell1");

            failures += TestAccessorMethods(t_cell2, db, "t_cell2", t_colID,
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    t_onset, t_offset, t_matrix,
                    outStream, verbose, "t_cell2");
        }


        /* verify that setVal() fails on an mveID mismatch. */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                m_cell0.setVal(m_matrix);
                m_cell0.setVal(f_matrix);

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
                        outStream.printf(
                                "m_cell0.setVal(f_matrix) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("m_cell0.setVal(f_matrix) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else
            {
                failures =+ MatrixTest.VerifyMatrixCopy(m_cell0.getVal(), m_matrix,
                                            outStream, verbose,
                                            "m_cell0.getVal()", "m_matrix");
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
    } /* DataCell::TestAccessors() */


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
    public void TestCopyConstructor()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing copy constructor for class DataCell                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell0_copy = null;
        DataCell f_cell1_copy = null;
        DataCell f_cell2_copy = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell i_cell0_copy = null;
        DataCell i_cell1_copy = null;
        DataCell i_cell2_copy = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell m_cell0_copy = null;
        DataCell m_cell1_copy = null;
        DataCell m_cell2_copy = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell n_cell0_copy = null;
        DataCell n_cell1_copy = null;
        DataCell n_cell2_copy = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell p_cell0_copy = null;
        DataCell p_cell1_copy = null;
        DataCell p_cell2_copy = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell t_cell0_copy = null;
        DataCell t_cell1_copy = null;
        DataCell t_cell2_copy = null;
        DataCell c = null;

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

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
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

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }

                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }

                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }

                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }

                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }

                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }

                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* now allocate the base cells for the copy tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell0.setOrd(10);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID,
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID,
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) ||
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }

                    if ( ( i_cell0 == null ) ||
                         ( i_cell1 == null ) ||
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }

                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) ||
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }

                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) ||
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }

                    if ( ( p_cell0 == null ) ||
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }

                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       10,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }

        /* now create the copies */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0_copy = new DataCell(f_cell0);
                f_cell1_copy = new DataCell(f_cell1);
                f_cell2_copy = new DataCell(f_cell2);

                i_cell0_copy = new DataCell(i_cell0);
                i_cell1_copy = new DataCell(i_cell1);
                i_cell2_copy = new DataCell(i_cell2);

                m_cell0_copy = new DataCell(m_cell0);
                m_cell1_copy = new DataCell(m_cell1);
                m_cell2_copy = new DataCell(m_cell2);

                n_cell0_copy = new DataCell(n_cell0);
                n_cell1_copy = new DataCell(n_cell1);
                n_cell2_copy = new DataCell(n_cell2);

                p_cell0_copy = new DataCell(p_cell0);
                p_cell1_copy = new DataCell(p_cell1);
                p_cell2_copy = new DataCell(p_cell2);

                t_cell0_copy = new DataCell(t_cell0);
                t_cell1_copy = new DataCell(t_cell1);
                t_cell2_copy = new DataCell(t_cell2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell0_copy == null ) ||
                 ( f_cell1_copy == null ) ||
                 ( f_cell2_copy == null ) ||
                 ( i_cell0_copy == null ) ||
                 ( i_cell1_copy == null ) ||
                 ( i_cell2_copy == null ) ||
                 ( m_cell0_copy == null ) ||
                 ( m_cell1_copy == null ) ||
                 ( m_cell2_copy == null ) ||
                 ( n_cell0_copy == null ) ||
                 ( n_cell1_copy == null ) ||
                 ( n_cell2_copy == null ) ||
                 ( p_cell0_copy == null ) ||
                 ( p_cell1_copy == null ) ||
                 ( p_cell2_copy == null ) ||
                 ( t_cell0_copy == null ) ||
                 ( t_cell1_copy == null ) ||
                 ( t_cell2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0_copy == null ) ||
                         ( f_cell1_copy == null ) ||
                         ( f_cell2_copy == null ) )
                    {
                        outStream.printf("f_cell copy allocation(s) failed.\n");
                    }

                    if ( ( i_cell0_copy == null ) ||
                         ( i_cell1_copy == null ) ||
                         ( i_cell2_copy == null ) )
                    {
                        outStream.printf("i_cell copy allocation(s) failed.\n");
                    }

                    if ( ( m_cell0_copy == null ) ||
                         ( m_cell1_copy == null ) ||
                         ( m_cell2_copy == null ) )
                    {
                        outStream.printf("m_cell copy allocation(s) failed.\n");
                    }

                    if ( ( n_cell0_copy == null ) ||
                         ( n_cell1_copy == null ) ||
                         ( n_cell2_copy == null ) )
                    {
                        outStream.printf("n_cell copy allocation(s) failed.\n");
                    }

                    if ( ( p_cell0_copy == null ) ||
                         ( p_cell1_copy == null ) ||
                         ( p_cell2_copy == null ) )
                    {
                        outStream.printf("p_cell copy allocation(s) failed.\n");
                    }

                    if ( ( t_cell0_copy == null ) ||
                         ( t_cell1_copy == null ) ||
                         ( t_cell2_copy == null ) )
                    {
                        outStream.printf("t_cell copy allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyDataCellCopy(f_cell0,
                                               f_cell0_copy,
                                               outStream,
                                               verbose,
                                               "f_cell0",
                                               "f_cell0_copy");

                failures += VerifyDataCellCopy(f_cell1,
                                               f_cell1_copy,
                                               outStream,
                                               verbose,
                                               "f_cell1",
                                               "f_cell1_copy");

                failures += VerifyDataCellCopy(f_cell2,
                                               f_cell2_copy,
                                               outStream,
                                               verbose,
                                               "f_cell2",
                                               "f_cell2_copy");

                failures += VerifyDataCellCopy(i_cell0,
                                               i_cell0_copy,
                                               outStream,
                                               verbose,
                                               "i_cell0",
                                               "i_cell0_copy");

                failures += VerifyDataCellCopy(i_cell1,
                                               i_cell1_copy,
                                               outStream,
                                               verbose,
                                               "i_cell1",
                                               "i_cell1_copy");

                failures += VerifyDataCellCopy(i_cell2,
                                               i_cell2_copy,
                                               outStream,
                                               verbose,
                                               "i_cell2",
                                               "i_cell2_copy");

                failures += VerifyDataCellCopy(m_cell0,
                                               m_cell0_copy,
                                               outStream,
                                               verbose,
                                               "m_cell0",
                                               "m_cell0_copy");

                failures += VerifyDataCellCopy(m_cell1,
                                               m_cell1_copy,
                                               outStream,
                                               verbose,
                                               "m_cell1",
                                               "m_cell1_copy");

                failures += VerifyDataCellCopy(m_cell2,
                                               m_cell2_copy,
                                               outStream,
                                               verbose,
                                               "m_cell2",
                                               "m_cell2_copy");

                failures += VerifyDataCellCopy(n_cell0,
                                               n_cell0_copy,
                                               outStream,
                                               verbose,
                                               "n_cell0",
                                               "n_cell0_copy");

                failures += VerifyDataCellCopy(n_cell1,
                                               n_cell1_copy,
                                               outStream,
                                               verbose,
                                               "n_cell1",
                                               "n_cell1_copy");

                failures += VerifyDataCellCopy(n_cell2,
                                               n_cell2_copy,
                                               outStream,
                                               verbose,
                                               "n_cell2",
                                               "n_cell2_copy");

                failures += VerifyDataCellCopy(p_cell0,
                                               p_cell0_copy,
                                               outStream,
                                               verbose,
                                               "p_cell0",
                                               "p_cell0_copy");

                failures += VerifyDataCellCopy(p_cell1,
                                               p_cell1_copy,
                                               outStream,
                                               verbose,
                                               "p_cell1",
                                               "p_cell1_copy");

                failures += VerifyDataCellCopy(p_cell2,
                                               p_cell2_copy,
                                               outStream,
                                               verbose,
                                               "p_cell2",
                                               "p_cell2_copy");

                failures += VerifyDataCellCopy(t_cell0,
                                               t_cell0_copy,
                                               outStream,
                                               verbose,
                                               "t_cell0",
                                               "t_cell0_copy");

                failures += VerifyDataCellCopy(t_cell1,
                                               t_cell1_copy,
                                               outStream,
                                               verbose,
                                               "t_cell1",
                                               "t_cell1_copy");

                failures += VerifyDataCellCopy(t_cell2,
                                               t_cell2_copy,
                                               outStream,
                                               verbose,
                                               "t_cell2",
                                               "t_cell2_copy");
            }
        }


        /* verify that copy constructor fails on null input */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf(
                                "new DataCell(null) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null) failed to throw " +
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

    } /* DataCell::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the accessor methods for this class.
     *
     *                                               -- 12/4/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods()
    throws SystemErrorException, LogicErrorException {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell c = null;
        NominalDataValue ndv = null;
        PredDataValue pdv = null;
        QuoteStringDataValue qdv = null;

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

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);

            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<val>");
            pve2.appendFormalArg(farg);
            pve2ID = db.addPredVE(pve2);
            // get a copy of the databases version of pve2 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<val>");
            pve3.appendFormalArg(farg);
            pve3ID = db.addPredVE(pve3);
            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            f_col = new DataColumn(db, "f_col",
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);


            i_col = new DataColumn(db, "i_col",
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);


            m_col = new DataColumn(db, "m_col",
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            // construct the predicate:
            //
            //      pve1(pve2(pve3(j_q_nominal)), "just a quote string")
            //
            p2 = new Predicate(db, pve3ID);
            ndv = new NominalDataValue(db,  p2.getArg(0).getItsFargID(),
                                      "j_q_nominal");
            p2.replaceArg(0, ndv);

            p1 = new Predicate(db, pve2ID);
            pdv = new PredDataValue(db, p1.getArg(0).getItsFargID(), p2);
            p1.replaceArg(0, pdv);

            p0 = new Predicate(db, pve1ID);
            pdv = new PredDataValue(db, p0.getArg(0).getItsFargID(), p1);
            p0.replaceArg(0, pdv);
            qdv = new QuoteStringDataValue(db, p0.getArg(1).getItsFargID(),
                                          "just a quote string");
            p0.replaceArg(1, qdv);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            pdv = new PredDataValue(db, fargID, p0);
            m_arg_list.add(pdv);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);


            n_col = new DataColumn(db, "n_col",
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);


            p_col = new DataColumn(db, "p_col",
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);


            t_col = new DataColumn(db, "t_col",
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
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

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }

                if ( ( pve1 == null ) ||
                     ( pve1ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve1.  pve1ID = %d.\n",
                                     pve1ID);
                }

                if ( ( pve2 == null ) ||
                     ( pve2ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve2.  pve2ID = %d.\n",
                                     pve2ID);
                }

                if ( ( pve3 == null ) ||
                     ( pve3ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve3.  pve3ID = %d.\n",
                                     pve3ID);
                }

                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }

                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }

                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }

                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }

                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }

                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }

                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }

                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }

                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }

                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }

                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }

                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }

                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }

                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }

                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }

                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }

                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }

                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* now allocate the base cells for the to string tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID,
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID,
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) ||
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }

                    if ( ( i_cell0 == null ) ||
                         ( i_cell1 == null ) ||
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }

                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) ||
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }

                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) ||
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }

                    if ( ( p_cell0 == null ) ||
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }

                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }

        if ( failures == 0 )
        {
            /* Run the to string tests:
             *
             * The tests here are both rigorous and lax.
             *
             * The are rigorous in  the sense that we running tests on every
             * type of cell, produced by all forms of the DataCell constructors.
             *
             * They are lax in that we don't concern outselves with creating
             * cells whose values are representative of the entire range of
             * values that can be represented.  This should be OK, as we
             * test this extensively in the DataValue classes.
             */
            String f_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (0.000000))";
            String f_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (0.000000))";
            String f_cell2_string = "(-1, 00:00:01:000, 00:00:02:000, (11.000000))";

            String i_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (0))";
            String i_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (0))";
            String i_cell2_string = "(-1, 00:00:03:000, 00:00:04:000, (22))";

            String m_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (<val>))";
            String m_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (<val>))";
            String m_cell2_string = "(-1, 00:00:05:000, 00:00:06:000, " +
                    "(pve1(pve2(pve3(j_q_nominal)), \"just a quote string\")))";

            String n_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String n_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String n_cell2_string = "(-1, 00:00:07:000, 00:00:08:000, " +
                                    "(a_nominal))";

            String p_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (()))";
            String p_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (()))";
            String p_cell2_string = "(-1, 00:00:09:000, 00:00:10:000, " +
                                    "(pve0(<arg1>, <arg2>)))";

            String t_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String t_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String t_cell2_string = "(-1, 00:00:11:000, 00:00:12:000, " +
                                    "(a text string))";


            String f_cell0_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";
            String f_cell1_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";
            String f_cell2_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:01:000)) " +
                        "(offset (60,00:00:02:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 11.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";

            String i_cell0_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";
            String i_cell1_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";
            String i_cell2_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:03:000)) " +
                        "(offset (60,00:00:04:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 22) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";

            String m_cell0_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 31) " +
                        "(itsMveID 25) " +
                        "(itsMveType MATRIX) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 25) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 26) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <val>) " +
                                            "(subRange false))))))))";
            String m_cell1_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 31) " +
                        "(itsMveID 25) " +
                        "(itsMveType MATRIX) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 25) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 26) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <val>) " +
                                            "(subRange false))))))))";
            String m_cell2_DBstring =
                "(DataCell (id 0) " +
                  "(itsColID 31) " +
                  "(itsMveID 25) " +
                  "(itsMveType MATRIX) " +
                  "(ord -1) " +
                  "(onset (60,00:00:05:000)) " +
                  "(offset (60,00:00:06:000)) " +
                  "(val " +
                    "(Matrix " +
                      "(mveID 25) " +
                      "(varLen false) " +
                      "(argList " +
                        "((PredDataValue (id 0) " +
                          "(itsFargID 26) " +
                          "(itsFargType UNTYPED) " +
                          "(itsCellID 0) " +
                          "(itsValue " +
                            "(predicate (id 0) " +
                              "(predID 4) " +
                              "(predName pve1) " +
                              "(varLen false) " +
                              "(argList " +
                                "((PredDataValue (id 0) " +
                                  "(itsFargID 5) " +
                                  "(itsFargType UNTYPED) " +
                                  "(itsCellID 0) " +
                                  "(itsValue " +
                                    "(predicate (id 0) " +
                                      "(predID 7) " +
                                      "(predName pve2) " +
                                      "(varLen false) " +
                                      "(argList " +
                                        "((PredDataValue (id 0) " +
                                          "(itsFargID 8) " +
                                          "(itsFargType UNTYPED) " +
                                          "(itsCellID 0) " +
                                          "(itsValue " +
                                            "(predicate (id 0) " +
                                              "(predID 9) " +
                                              "(predName pve3) " +
                                              "(varLen false) " +
                                              "(argList " +
                                                "((NominalDataValue (id 0) " +
                                                  "(itsFargID 10) " +
                                                  "(itsFargType UNTYPED) " +
                                                  "(itsCellID 0) " +
                                                  "(itsValue j_q_nominal) " +
                                                  "(subRange false))))))) " +
                                          "(subRange false))))))) " +
                                  "(subRange false)), " +
                        "(QuoteStringDataValue (id 0) " +
                          "(itsFargID 6) " +
                          "(itsFargType UNTYPED) " +
                          "(itsCellID 0) " +
                          "(itsValue just a quote string) " +
                          "(subRange false))))))) " +
                "(subRange false))))))))";

            String n_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String n_cell1_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String n_cell2_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:07:000)) " +
                    "(offset (60,00:00:08:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false))))))))";

            String p_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))))";
            String p_cell1_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))))";
            String p_cell2_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:09:000)) " +
                    "(offset (60,00:00:10:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
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
                                    "(subRange false))))))))";

            String t_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String t_cell1_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String t_cell2_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:11:000)) " +
                    "(offset (60,00:00:12:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))))";

            if ( f_cell0.toString().compareTo(f_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell0.toString(): \"%s\".\n",
                                     f_cell0.toString());
                }
            }

            if ( f_cell0.toDBString().compareTo(f_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell0.toDBString(): \"%s\".\n",
                                     f_cell0.toDBString());
                }
            }

            if ( f_cell1.toString().compareTo(f_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell1.toString(): \"%s\".\n",
                                     f_cell1.toString());
                }
            }

            if ( f_cell1.toDBString().compareTo(f_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell1.toDBString(): \"%s\".\n",
                                     f_cell1.toDBString());
                }
            }

            if ( f_cell2.toString().compareTo(f_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell2.toString(): \"%s\".\n",
                                     f_cell2.toString());
                }
            }

            if ( f_cell2.toDBString().compareTo(f_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell2.toDBString(): \"%s\".\n",
                                     f_cell2.toDBString());
                }
            }

            /*******************************************************/

            if ( i_cell0.toString().compareTo(i_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell0.toString(): \"%s\".\n",
                                     i_cell0.toString());
                }
            }

            if ( i_cell0.toDBString().compareTo(i_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell0.toDBString(): \"%s\".\n",
                                     i_cell0.toDBString());
                }
            }

            if ( i_cell1.toString().compareTo(i_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell1.toString(): \"%s\".\n",
                                     i_cell1.toString());
                }
            }

            if ( i_cell1.toDBString().compareTo(i_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell1.toDBString(): \"%s\".\n",
                                     i_cell1.toDBString());
                }
            }

            if ( i_cell2.toString().compareTo(i_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell2.toString(): \"%s\".\n",
                                     i_cell2.toString());
                }
            }

            if ( i_cell2.toDBString().compareTo(i_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell2.toDBString(): \"%s\".\n",
                                     i_cell2.toDBString());
                }
            }

            /*******************************************************/

            if ( m_cell0.toString().compareTo(m_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell0.toString(): \"%s\".\n",
                                     m_cell0.toString());
                }
            }

            if ( m_cell0.toDBString().compareTo(m_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell0.toDBString(): \"%s\".\n",
                                     m_cell0.toDBString());
                }
            }

            if ( m_cell1.toString().compareTo(m_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell1.toString(): \"%s\".\n",
                                     m_cell1.toString());
                }
            }

            if ( m_cell1.toDBString().compareTo(m_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell1.toDBString(): \"%s\".\n",
                                     m_cell1.toDBString());
                }
            }

            if ( m_cell2.toString().compareTo(m_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell2.toString(): \"%s\".\n",
                                     m_cell2.toString());
                }
            }

            if ( m_cell2.toDBString().compareTo(m_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell2.toDBString(): \"%s\".\n",
                                     m_cell2.toDBString());
                }
            }

            /*******************************************************/

            if ( n_cell0.toString().compareTo(n_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell0.toString(): \"%s\".\n",
                                     n_cell0.toString());
                }
            }

            if ( n_cell0.toDBString().compareTo(n_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell0.toDBString(): \"%s\".\n",
                                     n_cell0.toDBString());
                }
            }

            if ( n_cell1.toString().compareTo(n_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell1.toString(): \"%s\".\n",
                                     n_cell1.toString());
                }
            }

            if ( n_cell1.toDBString().compareTo(n_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell1.toDBString(): \"%s\".\n",
                                     n_cell1.toDBString());
                }
            }

            if ( n_cell2.toString().compareTo(n_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell2.toString(): \"%s\".\n",
                                     n_cell2.toString());
                }
            }

            if ( n_cell2.toDBString().compareTo(n_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell2.toDBString(): \"%s\".\n",
                                     n_cell2.toDBString());
                }
            }

            /*******************************************************/

            if ( p_cell0.toString().compareTo(p_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell0.toString(): \"%s\".\n",
                                     p_cell0.toString());
                }
            }

            if ( p_cell0.toDBString().compareTo(p_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell0.toDBString(): \"%s\".\n",
                                     p_cell0.toDBString());
                }
            }

            if ( p_cell1.toString().compareTo(p_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell1.toString(): \"%s\".\n",
                                     p_cell1.toString());
                }
            }

            if ( p_cell1.toDBString().compareTo(p_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell1.toDBString(): \"%s\".\n",
                                     p_cell1.toDBString());
                }
            }

            if ( p_cell2.toString().compareTo(p_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell2.toString(): \"%s\".\n",
                                     p_cell2.toString());
                }
            }

            if ( p_cell2.toDBString().compareTo(p_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell2.toDBString(): \"%s\".\n",
                                     p_cell2.toDBString());
                }
            }

            /*******************************************************/

            if ( t_cell0.toString().compareTo(t_cell0_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell0.toString(): \"%s\".\n",
                                     t_cell0.toString());
                }
            }

            if ( t_cell0.toDBString().compareTo(t_cell0_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell0.toDBString(): \"%s\".\n",
                                     t_cell0.toDBString());
                }
            }

            if ( t_cell1.toString().compareTo(t_cell1_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell1.toString(): \"%s\".\n",
                                     t_cell1.toString());
                }
            }

            if ( t_cell1.toDBString().compareTo(t_cell1_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell1.toDBString(): \"%s\".\n",
                                     t_cell1.toDBString());
                }
            }

            if ( t_cell2.toString().compareTo(t_cell2_string) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell2.toString(): \"%s\".\n",
                                     t_cell2.toString());
                }
            }

            if ( t_cell2.toDBString().compareTo(t_cell2_DBstring) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell2.toDBString(): \"%s\".\n",
                                     t_cell2.toDBString());
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
    } /* DataCell::TestToStringMethods() */


    /**
     * Verify7ArgConstructorFailure()
     *
     * Verify that the 7 argument constructor fails with the supplied
     * argument.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */

    public static int Verify7ArgConstructorFailure(Database db,
                                   String db_desc,
                                   String comment,
                                   String comment_desc,
                                   long colID,
                                   String colID_desc,
                                   long mveID,
                                   String mveID_desc,
                                   TimeStamp onset,
                                   String onset_desc,
                                   TimeStamp offset,
                                   String offset_desc,
                                   Matrix val,
                                   String val_desc,
                                   java.io.PrintStream outStream,
                                   boolean verbose)
    {
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String systemErrorExceptionString = null;
        DataCell c = null;


        try
        {
            c = new DataCell(db, comment, colID, mveID, onset, offset, val);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( c != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( c != null )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) returned non-null.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }

                if ( completed )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) completed.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) failed to throw a system error exception.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }
            }
        }

        return failures;

    } /* DataCell::Verify7ArgConstructorFailure() */


    /**
     * VerifyDataCellCopy()
     *
     * Verify that the supplied instances of DataCell are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     *                                               -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyDataCellCopy(DataCell base,
                                         DataCell copy,
                                         java.io.PrintStream outStream,
                                         boolean verbose,
                                         String baseDesc,
                                         String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDataCellCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDataCellCopy: %s null on entry.\n",
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

        failures += CellTest.VerifyCellCopy((Cell)base, (Cell)copy, outStream,
                                        verbose, baseDesc, copyDesc);

        if ( base.itsMveID != copy.itsMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsMveID == %d != %s.itsMveID == %d.\n",
                                 baseDesc, base.itsMveID,
                                 copyDesc, copy.itsMveID);
            }
        }

        if ( base.itsMveType != copy.itsMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsMveType == %s != %s.itsMveType == %s.\n",
                                 baseDesc, base.itsMveType.toString(),
                                 copyDesc, copy.itsMveType.toString());
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(base.onset,
                                                  copy.onset,
                                                  outStream,
                                                  verbose,
                                                  baseDesc + ".onset",
                                                  copyDesc + ".onset");

        failures += TimeStampTest.VerifyTimeStampCopy(base.offset,
                                                  copy.offset,
                                                  outStream,
                                                  verbose,
                                                  baseDesc + ".offset",
                                                  copyDesc + ".offset");

        failures += MatrixTest.VerifyMatrixCopy(base.val,
                                            copy.val,
                                            outStream,
                                            verbose,
                                            baseDesc + ".val",
                                            copyDesc + ".val");

        return failures;

    } /* DataCell::VerifyDataCellCopy() */


    /**
     * VerifyInitialization()
     *
     * Verify that the supplied instance of Cell has been correctly
     * initialized by a constructor.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyInitialization(Database db,
                                   DataCell c,
                                   String desc,
                                   String expectedComment,
                                   long expectedColID,
                                   long expectedMveID,
                                   MatrixVocabElement.MatrixType expectedMveType,
                                   int expectedOrd,
                                   TimeStamp expectedOnset,
                                   TimeStamp expectedOffset,
                                   Matrix expectedVal,
                                   java.io.PrintStream outStream,
                                   boolean verbose)
    {
        int failures = 0;

        if ( db == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: db null on entry.\n");
        }

        if ( c == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: c null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: c null on entry.\n");
        }

        if ( c.getDB() != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("c.db not initialized correctly.\n");
            }
        }

        failures += CellTest.VerifyInitialization(db, c, desc, expectedColID,
                                         expectedComment, outStream, verbose);

        if ( c.itsMveID != expectedMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsMveID not initialized correctly: %d (%d).\n",
                        desc, c.itsMveID, expectedMveID);
            }
        }

        if ( c.itsMveType != expectedMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsMveType not initialized correctly: %s (%s).\n",
                        desc, c.itsMveType.toString(), expectedMveType.toString());
            }
        }

        if ( c.ord != expectedOrd )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.ord not initialized correctly: %d (%d).\n",
                        desc, c.ord, expectedOrd);
            }
        }

        failures += TimeStampTest.VerifyTimeStampCopy(c.onset,
                                                  expectedOnset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".onset",
                                                  "expectedOnset");

        failures += TimeStampTest.VerifyTimeStampCopy(c.offset,
                                                  expectedOffset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".offset",
                                                  "expectedOffset");

        failures += MatrixTest.VerifyMatrixCopy(c.val,
                                            expectedVal,
                                            outStream,
                                            verbose,
                                            desc + ".val",
                                            "expectedVal");

        return failures;

    } /* DataCell::VerifyInitialization() */

}