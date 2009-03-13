package au.com.nicta.openshapa.db;

import java.io.PrintStream;
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
public class ColumnTest {

    public ColumnTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /** Dummy test. Class holds utility test methods. */
    @Test
    public void DummyTest() {

    }
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessors()
     *
     * Verify that the accessors defined in this abstract class perform
     * as expected in the supplied instance of some subclass.
     *
     *                                          JRM -- 1/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestAccessors(Database db,
                                    Column col,
                                    String expectedName,
                                    String invalidName,
                                    boolean initHidden,
                                    boolean initReadOnly,
                                    int initNumCells,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException, LogicErrorException
    {
        final String mName = "Column::TestAccessors()";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long cellID = DBIndex.INVALID_ID;
        DataCell dc = null;

        if ( db == null )
        {
            failures++;
            outStream.printf("%s: db null on entry.\n", mName);
        }

        if ( col == null )
        {
            failures++;
            outStream.printf("%s: col null on entry.\n", mName);
        }

        if ( expectedName == null )
        {
            failures++;
            outStream.printf("%s: expectedName null on entry.\n", mName);
        }

        if ( invalidName == null )
        {
            failures++;
            outStream.printf("%s: invalidName null on entry.\n", mName);
        }

        if ( col.getDB() != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getDB() != supplied db.\n", mName);
            }
        }


        /*** tests for getName() and setName() ***/

        /* We have already tested getName() and setName in passing -- thus
         * for this test we simply verify that getName() returns the expected
         * value, and that setName() fails on invalid input.
         */

        if ( expectedName.compareTo(col.getName()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getName() = \"%s\" != \"%s\"(1).\n",
                                 mName, col.getName(), expectedName);
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            col.setName(invalidName);

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
                            "col.setName(invalidName) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("col.setName(invalidName) failed to " +
                                     "throw a system error exception.\n");
                }
            }
        }
        else if ( expectedName.compareTo(col.getName()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getName() = \"%s\" != \"%s\"(2).\n",
                                 mName, col.getName(), expectedName);
            }
        }


        /*** Tests for getHidden() & setHidden() ***/

        if ( col.getHidden() != initHidden )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getHidden() = %b (%b expected)(1).\n",
                                 mName, col.getHidden(), initHidden);
            }
        }

        col.setHidden(! initHidden);

        if ( col.getHidden() != ! initHidden )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getHidden() = %b (%b expected)(2).\n",
                                 mName, col.getHidden(), ! initHidden);
            }
        }

        col.setHidden(initHidden);

        if ( col.getHidden() != initHidden )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getHidden() = %b (%b expected)(3).\n",
                                 mName, col.getHidden(), initHidden);
            }
        }


        /*** Tests for getReadOnly() & setReadOnly() ***/

        if ( col.getReadOnly() != initReadOnly )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getReadOnly() = %b (%b expected)(1).\n",
                                 mName, col.getReadOnly(), initReadOnly);
            }
        }

        col.setReadOnly(!initReadOnly);

        if ( col.getReadOnly() != ! initReadOnly )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getReadOnly() = %b (%b expected)(2).\n",
                                 mName, col.getReadOnly(), ! initReadOnly);
            }
        }


        col.setReadOnly(initReadOnly);

        if ( col.getReadOnly() != initReadOnly )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getReadOnly() = %b (%b expected)(3).\n",
                                 mName, col.getReadOnly(), initReadOnly);
            }
        }


        /*** Tests for getNumCells() & setNumCells() ***/

        if ( col.getNumCells() != initNumCells )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getNumCells() = %d (%d expected)(1).\n",
                                 mName, col.getNumCells(), initNumCells);
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            col.setNumCells(initNumCells + 1);

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
                            "col.setNumCells() failed to complete(1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("col.setNumCells() threw a system error " +
                                     "exception(1): \"%s\"",
                                     systemErrorExceptionString);
                }
            }
        }
        else if ( col.getNumCells() != initNumCells + 1 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getNumCells() = %d (%d expected)(2).\n",
                                 mName, col.getNumCells(), initNumCells + 1);
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            col.setNumCells(initNumCells);

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
                            "col.setNumCells() failed to completed(2).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("col.setNumCells() threw a system error " +
                                     "exception(2): \"%s\"",
                                     systemErrorExceptionString);
                }
            }
        }
        else if ( col.getNumCells() != initNumCells )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getNumCells() = %d (%d expected)(3).\n",
                                 mName, col.getNumCells(), initNumCells);
            }
        }

        /* verify that setNumCells fails when passed a negative number */

        completed = false;
        threwSystemErrorException = false;
        try
        {
            col.setNumCells(-1);

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
                            "col.setNumCells(-1) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("col.setNumCells(-1) failed to throw a " +
                                     "system error exception.\n");
                }
            }
        }
        else if ( col.getNumCells() != initNumCells )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: getNumCells() = %d (%d expected)(4).\n",
                                 mName, col.getNumCells(), initNumCells);
            }
        }

        return failures;

    } /* Column::TestAccessors() */


    /**
     * VerifyDataColumnCopy()
     *
     * Verify that the supplied instances of Column are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     *                                              JRM -- 12/30/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyColumnCopy(Column base,
                                       Column copy,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String baseDesc,
                                       String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyColumnCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyColumnCopy: %s null on entry.\n",
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

        if ( base.getDB() != copy.getDB() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }

        if ( base.getID() != copy.getID() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.id == %d != %s.id == %d.\n",
                                 baseDesc, base.getID(),
                                 copyDesc, copy.getID());
            }
        }

        if ( base.hidden != copy.hidden )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.hidden == %b != %s.hidden == %b.\n",
                                 baseDesc, base.hidden,
                                 copyDesc, copy.hidden);
            }
        }

        if ( base.name == null )
        {
            if ( copy.name != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.name == null != %s.name == \"%s\".\n",
                            baseDesc, copyDesc, copy.name);
                }
            }
        }
        else if ( copy.name == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.name == \"%s\" != %s.name == null.\n",
                        baseDesc, base.name, copyDesc);
            }
        }
        else if ( base.name == copy.name )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.name and %s.name refer to the same string.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( base.name.compareTo(copy.name) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.name == \"%s\" != %s.name == \"%s\".\n",
                        baseDesc, base.name, copyDesc, copy.name);
            }
        }

        if ( base.numCells != copy.numCells )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.numCells == %d != %s.numCells == %d.\n",
                                 baseDesc, base.numCells,
                                 copyDesc, copy.numCells);
            }
        }

        if ( base.readOnly != copy.readOnly )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.readOnly == %b != %s.readOnly == %b.\n",
                                 baseDesc, base.readOnly,
                                 copyDesc, copy.readOnly);
            }
        }

        return failures;

    } /* Column::VerifyColumnCopy() */


    /**
     * VerifyInitialization()
     *
     * Verify that the supplied instance of Column has been correctly
     * initialized by a constructor.
     *
     *                                              JRM -- 12/26/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyInitialization(Database db,
                                           Column c,
                                           String desc,
                                           String expectedName,
                                           boolean expectedHidden,
                                           boolean expectedReadOnly,
                                           int expectedNumCells,
                                           java.io.PrintStream outStream,
                                           boolean verbose)
    {
        int failures = 0;

        if ( db == null )
        {
            failures++;
            outStream.printf("Column::VerifyInitialization: db null on entry.\n");
        }

        if ( c == null )
        {
            failures++;
            outStream.printf("Column::VerifyInitialization: c null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;
            outStream.printf("Column::VerifyInitialization: desc null on entry.\n");
        }

        if ( c.hidden != expectedHidden )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.hidden = %b (%b expected).\n",
                                 desc, c.hidden, expectedHidden);
            }
        }

        if ( c.readOnly != expectedReadOnly )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.readOnly = %b (%b expected).\n",
                                 desc, c.readOnly, expectedReadOnly);
            }
        }

        if ( c.numCells != expectedNumCells )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.numCells = %d (%d expected).\n",
                                 desc, c.numCells, expectedNumCells);
            }
        }

        if ( expectedName == null )
        {
            if ( c.name != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s: c.name = \"%s\" (null expected).\n",
                                     desc, c.name);
                }
            }
        }
        else
        {
            if ( expectedName.compareTo(c.name) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s: c.name = \"%s\" (\"%s\" expected).\n",
                                     desc, c.name, expectedName);
                }
            }
            else if ( c.name == expectedName )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s: c.name == expectedName.\n", desc);
                }
            }
        }

        return failures;

    } /* Column::VerifyInitialization() */

}