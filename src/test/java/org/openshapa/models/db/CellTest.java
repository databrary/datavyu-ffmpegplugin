package org.openshapa.models.db;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public abstract class CellTest extends DBElementTest {

    /** Constructor. */
    public CellTest() {
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessorMethods()
     *
     * Verify that the accessors supported by the Cell abstract class function
     * correctly when run on the supplied instance of some subclass of Cell.
     *
     *                                               -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */

    public static int TestAccessorMethods(Cell testCell,
                                          Database initDB,
                                          String initComment,
                                          long initItsColID,
                                          int initOrd,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String desc)
    {
        int failures = 0;
        String newComment = new String("a new comment");
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;

        if ( testCell == null )
        {
            failures++;

            outStream.printf("Cell::TestAccessors(): testCell null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;

            outStream.printf("Cell::TestAccessors(): desc null on entry.\n");
        }

        /* test getDB() */

        if ( testCell.getDB() != initDB )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getDB() != expected value.\n", desc);
            }
        }


        /* test getComment() / setComment() */

        if ( initComment == null )
        {
            if ( testCell.getComment() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.getComment() = \"%s\", not null as expected.\n",
                            desc, testCell.getComment());
                }
            }
        }
        else
        {
            if ( testCell.getComment() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.getComment() = null, not \"%s\" as expected.\n",
                            desc, initComment);
                }
            }
            else if ( testCell.getComment() == initComment )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.getComment() == initComment.\n", desc);
                }
            }
            else if ( initComment.compareTo(testCell.getComment()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "%s.getComment() = \"%s\" != \"%s\" = initComment.\n",
                        desc, testCell.getComment(), initComment);
                }
            }
        }

        testCell.setComment(newComment);

        if ( testCell.getComment() == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getComment() = null != \"%s\" = newComment.\n",
                    desc, testCell.getComment(), newComment);
            }
        }
        else if ( testCell.getComment() == newComment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getComment() == newComment.\n", desc);
            }
        }
        else if ( newComment.compareTo(testCell.getComment()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getComment() = \"%s\" != \"%s\" = newComment.\n",
                    desc, testCell.getComment(), newComment);
            }
        }


        /* test getItsColID() */

        if ( testCell.getItsColID() != initItsColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getColID() = %d != %d = initItsColID.\n",
                    desc, testCell.getItsColID(), initItsColID);
            }
        }


        /* test getOrd() / setOrd() */

        if ( testCell.getOrd() != initOrd )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() == %d != %d == expected value.\n",
                        desc, testCell.getOrd(), initOrd);
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOrd(1066);
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
                            "testCell.setOrd(1066) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOrd(1066) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( testCell.getOrd() != 1066 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() = %d, not 1066 as expected (1).\n",
                        desc, testCell.getOrd());
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOrd(0);
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
                    outStream.printf("testCell.setOrd(0) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOrd(0) failed to throw " +
                                      "system error exception.\n");
                }
            }
        }

        if ( testCell.getOrd() != 1066 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() = %d, not 1066 as expected (2).\n",
                        desc, testCell.getOrd());
            }
        }

        return failures;

    } /* Cell::TestAccessorMethods() */


    /**
     * VerifyCellCopy()
     *
     * Verify that the supplied instances of Cell are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     *                                               -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyCellCopy(Cell base,
                                     Cell copy,
                                     java.io.PrintStream outStream,
                                     boolean verbose,
                                     String baseDesc,
                                     String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyCellCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyCellCopy: %s null on entry.\n",
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

        if ( base.itsColID != copy.itsColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsColID == %d != %s.itsColID == %d.\n",
                                 baseDesc, base.itsColID,
                                 copyDesc, copy.itsColID);
            }
        }

        if ( base.ord != copy.ord )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.ord == %d != %s.ord == %d.\n",
                                 baseDesc, base.ord,
                                 copyDesc, copy.ord);
            }
        }

        if ( base.comment == null )
        {
            if ( copy.comment != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.comment == null != %s.comment == \"%s\".\n",
                            baseDesc, copyDesc, copy.comment);
                }
            }
        }
        else if ( copy.comment == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment == \"%s\" != %s.comment == null.\n",
                        baseDesc, base.comment, copyDesc);
            }
        }
        else if ( base.comment == copy.comment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment and %s.comment refer to the same string.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( base.comment.compareTo(copy.comment) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment == \"%s\" != %s.comment == \"%s\".\n",
                        baseDesc, base.comment, copyDesc, copy.comment);
            }
        }

        return failures;

    } /* Cell::VerifyCellCopy() */


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
                                           Cell c,
                                           String desc,
                                           long expectedColID,
                                           String expectedComment,
                                           java.io.PrintStream outStream,
                                           boolean verbose)
    {
        int failures = 0;

        if ( db == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: db null on entry.\n");
        }

        if ( c == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: c null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: desc null on entry.\n");
        }

        if ( c.getDB() != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.db not initialized correctly.\n",
                                 desc);
            }
        }

        if ( c.getID() != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.id not initialized corectly: %d.\n",
                                 desc, c.getID());
            }
        }

        if ( c.itsColID != expectedColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsColID not initialized correctly: %d (%d).\n",
                        desc, c.itsColID, expectedColID);
            }
        }

        if ( expectedComment == null )
        {
            if ( c.comment != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s: c.comment == \"%s\" != null.\n",
                                     desc, c.comment);
                }
            }
        }
        else if ( expectedComment == c.comment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.comment and expectedComment refer " +
                                 "to the same string.\n, desc");
            }
        }
        else if ( expectedComment.compareTo(c.comment) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.comment == \"%s\" != " +
                                 "expectedComment = \"%s\".\n",
                                 desc, c.comment, expectedComment);
            }
        }

        return failures;

    } /* Cell::VerifyInitialization() */

}