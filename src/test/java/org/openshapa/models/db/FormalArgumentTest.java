package org.openshapa.models.db;


import org.testng.annotations.Test;

/**
 *
 */
public class FormalArgumentTest {

    public FormalArgumentTest() {
    }

    @Test
    public void DummyTest() {

    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors defined in this class using
     * the instance of some subclass of AbStractFormalArgument supplied in
     * the argument list.
     *
     * This method is intended to be called in the test code of the classes
     * subclasses, and thus just returns the number of failures unless
     * the verbose parameter is true.
     *
     * Note that for now we don't test itsVocabElement here.  Will do that in
     * another method.
     *                                           -- 3/10/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestAccessors(FormalArgument arg,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
        final String mName = "AbstractFormalArgument::TestAccessors(): ";
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;

        if ( arg == null )
        {
            outStream.print(mName + "arg null on entry.\n");

            throw new SystemErrorException(mName + "arg null on entry.");
        }

        /*******************************************/
        /* Start by testing accessors for fArgName */
        /*******************************************/

        /* verify that we have the default formal argument name */

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<val>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fArgName(1) \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        /* now change it to another valid formal argument name */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg.setFargName("<a_valid_name>");
                methodReturned= true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("arg.setFargName(\"<a_valid_name>\")\""
                            + " failed to return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("arg.setFargName(\"<a_valid_name>\")\""
                            + " threw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change took */

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<a_valid_name>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fArgName(2) \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }


        /* now try to change it to an invalid farg name */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg.setFargName("<an invalid name>");
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("arg.setFargName(\"<an invalid name>\")\""
                            + " returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "arg.setFargName(\"<an invalid name>\")\"  " +
                                "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change didn't take */

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<a_valid_name>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fArgName(3) \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        /***********************************************/
        /* now test the accessors for the hidden field */
        /***********************************************/

        /* verify that hidden has its default value */

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of hidden(1): %b.\n",
                                       arg.getHidden());
                }
            }
        }


        /* now try to set hidden to true, and verify that the change took */

        if ( failures == 0 )
        {
            arg.setHidden(true);


            if ( arg.getHidden() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of hidden(2): %b.\n",
                                       arg.getHidden());
                }
            }
        }



        /* finally, set hidden back to false, and verify that the change took */

        if ( failures == 0 )
        {
            arg.setHidden(false);


            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of hidden(3): %b.\n",
                                       arg.getHidden());
                }
            }
        }

        return failures;

    } /* FormalArgument::TestAccessors() */

    /**
     * TestVEAccessors()
     *
     * Run a battery of tests on the accessors for the itsVocabElement and
     * itsVocabElementID fields.   This test requires allocation of a vocab
     * element and the database, and thus could not implemented until those
     * features were in place.
     *                                           -- 6/14/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestVEAccessors(FormalArgument arg,
                                      java.io.PrintStream outStream,
                                      boolean verbose)
        throws SystemErrorException
    {
        final String mName = "AbstractFormalArgument::TestVEAccessors(): ";
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long VEID0;
        long VEID1;
        long VEID2;
        String systemErrorExceptionString = null;
        Database db = null;
        VocabElement VE0;
        VocabElement VE1;
        VocabElement VE2;
        PredicateVocabElement p0 = null;

        if ( arg == null )
        {
            outStream.print(mName + "arg null on entry.\n");

            throw new SystemErrorException(mName + "arg null on entry.");
        }
        else if ( (db = arg.getDB()) == null )
        {
            outStream.print(mName + "arg.db is null.\n");

            throw new SystemErrorException(mName + "arg.db null on entry.");
        }
        else if ( (db = arg.getDB()) == null )
        {
            outStream.print(mName + "arg.db is null.\n");

            throw new SystemErrorException(mName + "arg.db null on entry.");
        }

        /* allocate a predicate vocab element for test purposes, and insert
         * it in the vocab list.  This gives it an id that we can use for
         * test purposes.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            p0 = null;
            systemErrorExceptionString = null;

            try
            {
                p0 = new PredicateVocabElement(db, "p0");
                db.vl.addElement(p0);
                methodReturned = true;
            } catch (SystemErrorException e) {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! methodReturned ) ||
                 ( p0 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( p0 == null )
                    {
                        outStream.print("p0 is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test setup threw an " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }


        /* verify that get & set its vocab element and its vocab element ID
         * methods perform correctly if given good data.
         */

        if ( failures == 0 )
        {
            VE0 = null;
            VE1 = null;
            VE2 = null;
            VEID0 = DBIndex.INVALID_ID;
            VEID1 = DBIndex.INVALID_ID;
            VEID2 = DBIndex.INVALID_ID;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                VE0 = arg.getItsVocabElement();
                VEID0 = arg.getItsVocabElementID();
                arg.setItsVocabElement(p0);
                arg.setItsVocabElementID(p0.getID());
                VE1 = arg.getItsVocabElement();
                VEID1 = arg.getItsVocabElementID();
                arg.setItsVocabElement(null);
                arg.setItsVocabElementID(DBIndex.INVALID_ID);
                VE2 = arg.getItsVocabElement();
                VEID2 = arg.getItsVocabElementID();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( VE0 != null ) || ( VEID0 != DBIndex.INVALID_ID ) ||
                 ( VE1 != p0 ) || ( VEID1 != p0.getID() ) ||
                 ( VEID1 == DBIndex.INVALID_ID ) ||
                 ( VE2 != null ) || ( VEID2 != DBIndex.INVALID_ID ) ||
                 ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( VE0 != null )
                    {
                        outStream.print("VE0 != null.\n");
                    }

                    if ( VE1 != p0 )
                    {
                        outStream.print("VE0 != p0.\n");
                    }

                    if ( VE0 != null )
                    {
                        outStream.print("VE0 != null.\n");
                    }

                    if ( VEID0 != DBIndex.INVALID_ID )
                    {
                        outStream.print("VEID0 != INVALID_ID.\n");
                    }

                    if ( VEID1 != p0.getID() )
                    {
                        outStream.print("VEID1 != p0.getID().\n");
                    }

                    if ( VEID1 == DBIndex.INVALID_ID )
                    {
                        outStream.print("VEID == INVALID_ID.\n");
                    }

                    if ( VEID2 != DBIndex.INVALID_ID )
                    {
                        outStream.print("VEID0 != INVALID_ID.\n");
                    }

                    if ( ! methodReturned )
                    {
                        outStream.print("valid test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("valid test threw an " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* verify that setItsVocabElementID() will fail if passed an ID
         * that is not associated with a vocab element
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg.setItsVocabElementID(100);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( methodReturned ) ||
                 ( arg.itsVocabElementID != DBIndex.INVALID_ID ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("invalid test completed.\n");
                    }

                    if ( arg.itsVocabElementID != DBIndex.INVALID_ID )
                    {
                        outStream.print("arg.itsVocabElementID != " +
                                "DBIndex.INVALID_ID after invalid test.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("invalid test failed to " +
                                        "throw a system error.\n");
                    }
                }
            }

        }

        return failures;

    } /* FormalArgument::TestVEAccessors() */

}