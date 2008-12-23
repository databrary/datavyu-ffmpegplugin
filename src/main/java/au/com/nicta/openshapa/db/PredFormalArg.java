/*
 * PredFormalArg.java
 *
 * Created on June 16, 2007, 12:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class PredFormalArg
 *
 * Instances of this class are usef for formal arguments that have been
 * strongly typed to predicates.
 *
 * @author mainzer
 */
public class PredFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any valid nominal, or only by some nominal that
     *      appears in the approvedSet (see below).
     *
     * approvedSet: Set of representing the predicates that may be used to
     *      replace this formal argument.  The elements of the set are the IDs
     *      of the approved predicates, which must all be listed in the
     *      associated database's vocab list.
     *
     *      The field is ignored and should be null if subRange is false,
     *
     *      At present, the approvedSet is implemented with TreeSet, so as
     *      to quickly provide a sorted list of approved predicate IDs.  If
     *      this turns out to be unnecessary, we should use HashSet instead.
     */

    /** Whether values are restricted to members of the approvedList */
    boolean subRange = false;

    /** If subRange is true, set of IDs of predicates that may replace the
     *  formal arg.
     */
    java.util.TreeSet<java.lang.Long> approvedSet = null;




    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * PredFormalArg()
     *
     * Constructors for predicate typed formal arguments.
     *
     * Three versions of this constructor -- one that takes only a database
     * referenece, one that takes a database reference and the formal argument
     * name as a parameters, and one that takes a reference to an instance of
     * PredFormalArg and uses it to create a copy.
     *
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public PredFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = fArgType.PREDICATE;

    } /* NominalFormalArg() -- no parameters */

    public PredFormalArg(Database db,
                         String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = fArgType.PREDICATE;

    } /* NominalFormalArg() -- one parameter */

    public PredFormalArg(PredFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "PredFormalArg::PredFormalArg(): ";

        this.fargType = fArgType.PREDICATE;

        if ( ! ( fArg instanceof PredFormalArg ) )
        {
            throw new SystemErrorException(mName + "fArg not PredFormalArg");
        }

        // copy over fields.

        this.subRange = fArg.getSubRange();

        if ( this.subRange )
        {
            /* copy over the approved predicates IDs list from fArg. */
            java.util.Vector<java.lang.Long> approvedVector = fArg.getApprovedVector();

            this.approvedSet = new java.util.TreeSet<java.lang.Long>();

            for ( long i : approvedVector )
            {
                this.addApproved(i);
            }
        }

    } /* PredFormalArg() -- make copy */



    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getSubRange() & setSubRange()
     *
     * Accessor routine used to get and set the subRange field.
     *
     * In addition, if subRange is changed from false to true, we must allocate
     * the approvedSet.  Similarly, if subrange is changed from true to false,
     * we discard the approved list by setting the approvedList field to null.
     *
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean getSubRange()
    {
        return subRange;
    }

    public void setSubRange(boolean subRange)
    {
        final String mName = "PredFormalArg::setSubRange(): ";

        if ( this.subRange != subRange )
        {
            /* we have work to do. */
            if ( subRange )
            {
                this.subRange = true;
                approvedSet = new java.util.TreeSet<java.lang.Long>();
            }
            else
            {
                this.subRange = false;

                /* discard the approved set */
                approvedSet = null;
            }
        }

        return;

    } /* NominalFormalArg::setSubRange() */


    /*************************************************************************/
    /************************ Approved Set Management: ***********************/
    /*************************************************************************/

    /**
     * addApproved()
     *
     * Add the supplied nominal to the approved set.
     *
     * The method throws a system error if subRange is false, if passed a null,
     * if passed an invalid nominal, or if the approved list already contains
     * the supplied nominal.
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public void addApproved(long predID)
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::addApproved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! this.db.vl.predInVocabList(predID) )
        {
            throw new SystemErrorException(mName + "predID not in vocab list.");
        }
        else if ( ! this.approvedSet.add(predID) )
        {
            throw new SystemErrorException(mName +
                                           "predID already in approved set.");
        }

        return;

    } /* NominalFormalArg::addApproved() */


    /**
     * approved()
     *
     * Return true if the supplied String contains a nominal that is a member
     * of the approved set.
     *
     * The method throws a system error if passed a null, if subRange is false,
     * or if the test string does not contain a valid nominal.
     *
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean approved(long predID )
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::approved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( this.approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! this.db.vl.predInVocabList(predID) )
        {
            throw new SystemErrorException(mName +
                                           "id not associated with a pred.");
        }

        return approvedSet.contains(predID);

    } /* NominalFormalArg::approved() */


    /**
     * approvedSetToString()
     *
     * Construct and return a string representation of the approved set.
     *
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    private String approvedSetToString()
    {
        final String mName = "PredFormalArg::approvedSetToString(): ";
        String s = null;
        java.lang.Long predID;
        java.util.Iterator<java.lang.Long> iterator = null;

        if ( subRange )
        {
            if ( this.approvedSet == null )
            {
                s = "(" + mName +
                    " (subRange && (approvedSet == null)) syserr?? )";
            }

            iterator = this.approvedSet.iterator();

            s = "(";

            if ( iterator.hasNext() )
            {
                predID = iterator.next();
                s += predID.toString();
            }

            while ( iterator.hasNext() )
            {
                predID = iterator.next();
                s += ", " + predID.toString();
            }

            s += ")";
        }
        else
        {
            s = "()";
        }

        return s;
    }

    /**
     * deleteApproved()
     *
     * Delete the supplied predicate ID from the approved set.
     *
     * The method throws a system error if subRange is false, if passed the
     * invalid ID, or if the approved list does not contain the supplied
     * predicate ID.
     *                                          JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public void deleteApproved(long predID)
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::deleteApproved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! this.db.vl.predInVocabList(predID) )
        {
            throw new SystemErrorException(mName +
                    "predID not associated with a predicate.");
        }
        else if ( ! this.approvedSet.remove(predID) )
        {
            throw new SystemErrorException(mName + "predID not in approved set.");
        }

        return;

    } /* PredFormalArg::deleteApproved() */


    /**
     * getApprovedVector()
     *
     * Return an vector of long containing an increasing order list of all
     * entries in the approved set, or null if the approved list is empty.
     *
     * The method throws a system error if subRange is false.
     *
     *                                              JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    java.util.Vector<java.lang.Long> getApprovedVector()
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::getApprovedList(): ";
        java.util.Vector<java.lang.Long> approvedVector = null;

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }

        if ( this.approvedSet.size() > 0 )
        {
            approvedVector = new java.util.Vector<java.lang.Long>();

            for ( long predID : this.approvedSet )
            {
                approvedVector.add(predID);
            }
        }

        return approvedVector;

    } /* PredFormalArg::getApprovedVector() */

    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of PredDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * PredDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        PredDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new PredDataValue(this.db, this.id);
        }
        else if ( salvage instanceof PredDataValue )
        {
            retVal = new PredDataValue(this.db, this.id,
                    ((PredDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new PredDataValue(this.db, this.id);
        }

        return retVal;

    } /* PredDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of PredDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new PredDataValue(this.db, this.id);

     } /* PredFormalArg::constructEmptyArg() */


    /**
     * toDBString() -- Override of abstract method in DataValue
     *
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     *                                      JRM -- 6/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString() {

        return ("(PredFormalArg " + getID() + " " + getFargName() + " " +
                getSubRange() + " " + approvedSetToString() + ")");

    } /* PredFormalArg::toDBString() */


    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean method that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                             JRM -- 6/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::isValidValue(): ";
        Predicate pred = null;

        if ( obj instanceof Predicate )
        {
            pred = (Predicate)obj;

            if ( pred.getDB() != this.db )
            {
                return false;
            }

            if ( this.subRange )
            {
                long pveID;

                pveID = pred.getPveID();

                if ( ( pveID != DBIndex.INVALID_ID ) &&
                     ( this.approved(pveID) ) )
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }

        return false;

    } /*  PredFormalArg::isValidValue() */


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*** TODO: Review test code. ***/

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
    {
        String testBanner =
            "Testing class PredFormalArg accessors                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean approved0 = false;
        boolean approved1 = false;
        boolean approved2 = false;
        boolean approved3 = false;
        boolean approved4 = false;
        boolean approved5 = false;
        boolean approved6 = false;
        boolean testFinished = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredFormalArg arg = null;
        Database db = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        UnTypedFormalArg alpha   = null;
        UnTypedFormalArg bravo   = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta   = null;
        UnTypedFormalArg echo    = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf    = null;
        UnTypedFormalArg hotel   = null;
        UnTypedFormalArg india   = null;
        UnTypedFormalArg juno    = null;
        UnTypedFormalArg kilo    = null;
        UnTypedFormalArg lima    = null;
        UnTypedFormalArg mike    = null;
        UnTypedFormalArg nero    = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            arg = new PredFormalArg(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new ODBCDatabase() returned null.\n");
                }

                if ( arg == null )
                {
                    outStream.print("new PredFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new PredFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures +=
                        FormalArgument.TestAccessors(arg, outStream, verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("FormalArgument.TestAccessors." +
                            " threw a SystemErrorException.\n");
                }
            }
        }

        /* PredFormalArg adds subRange and approvedSet.  We must test
         * the routines supporting these fields as well.
         */

        /* First verify correct initialization */
        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("\"arg.getSubRange()\" returned " +
                            "unexpected initial value(1): %b.\n",
                            arg.getSubRange());
                }
            }
            else if ( arg.approvedSet != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.approvedSet not initialized to null.\n");
                }
            }
        }

        /* now set subRange to true, and verify that approvedSet is allocated.
         *
         * Start by allocating a bunch of predicate vocab elements for test
         * purposes.
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
            p0      = null;
            p1      = null;
            p2      = null;
            p3      = null;
            p4      = null;
            p5      = null;
            p6      = null;
            testFinished = false;
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
                mike    = new UnTypedFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");

                p0 = VocabList.ConstructTestPred(db, "p0",
                                                 alpha, null, null, null);
                p1 = VocabList.ConstructTestPred(db, "p1",
                                                 bravo, charlie, null, null);
                p2 = VocabList.ConstructTestPred(db, "p2",
                                                 delta, echo, foxtrot, null);
                p3 = VocabList.ConstructTestPred(db, "p3",
                                                 hotel, india, juno, kilo);
                p4 = VocabList.ConstructTestPred(db, "p4",
                                                 lima, null, null, null);
                p5 = VocabList.ConstructTestPred(db, "p5",
                                                 mike, null, null, null);
                p6 = VocabList.ConstructTestPred(db, "p6",
                                                 nero, null, null, null);

                db.vl.addElement(p0);
                db.vl.addElement(p1);
                db.vl.addElement(p2);
                db.vl.addElement(p3);
                db.vl.addElement(p4);
                db.vl.addElement(p5);
                db.vl.addElement(p6);

                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! testFinished ) ||
                 ( alpha == null ) || ( bravo == null ) ||
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( hotel == null ) || ( india == null ) ||
                 ( juno == null ) || ( kilo == null ) ||
                 ( lima == null ) || ( mike == null ) ||
                 ( nero == null ) ||
                 ( p0 == null ) || ( p1 == null ) ||
                 ( p2 == null ) || ( p3 == null ) ||
                 ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! testFinished )
                    {
                        outStream.print("Setup for approved set test " +
                                        "failed to complete.\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) ||
                         ( juno == null ) || ( kilo == null ) ||
                         ( lima == null ) || ( mike == null ) ||
                         ( nero == null ) )
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
                                "one or more pred allocations failed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error exception " +
                                "in approved set test setup: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* now verify that setSubRange works as expected */
        if ( failures == 0 )
        {
            arg.setSubRange(true);

            if ( ( arg.subRange != true ) ||
                 ( arg.getSubRange() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(true)\" failed " +
                            "to set arg.subRange to true.\n");
                }
            }
            else if ( ( arg.approvedSet == null ) ||
                      ( arg.approvedSet.size() != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(true)\" failed " +
                            "to initialize arg.approvedSet correctly.\n");
                }
            }
        }

        /* now test the approvedSet management functions.  Start with just a
         * simple smoke check that verifies that the methods do more or
         * less as they should, and then verify that they fail when they
         * should.
         */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(p0.getID());
                arg.addApproved(p2.getID());
                arg.addApproved(p4.getID());
                arg.deleteApproved(p0.getID());
                arg.addApproved(p6.getID());
                approved0 = arg.approved(p0.getID());
                approved1 = arg.approved(p1.getID());
                approved2 = arg.approved(p2.getID());
                approved3 = arg.approved(p3.getID());
                approved4 = arg.approved(p4.getID());
                approved5 = arg.approved(p5.getID());
                approved6 = arg.approved(p6.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( approved0 != false ) || ( approved1 != false ) ||
                 ( approved2 != true )  || ( approved3 != false ) ||
                 ( approved4 != true )  || ( approved5 != false ) ||
                 ( approved6 != true )  ||
                 ( ! testFinished ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( approved0 != false ) || ( approved1 != false ) ||
                         ( approved2 != true )  || ( approved3 != false ) ||
                         ( approved4 != true )  || ( approved5 != false ) ||
                         ( approved6 != true ) )
                    {
                        outStream.print("unexpected approved results(1).\n");
                    }

                    if ( ! testFinished )
                    {
                        outStream.print("approved set test incomplete(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("SystemErrorException thrown in " +
                                         "approved set test(1): %s.\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.approvedSetToString().
                    compareTo("(6, 15, 19)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected arg.approvedSetToString() " +
                            "results(1): \"%s\".\n", arg.approvedSetToString());
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<java.lang.Long> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "SystemErrorException thrown in call to " +
                            "arg.getApprovedVector()(1).\n");
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(2).\n");
                }
            }
            else if ( ( approvedVector.size() != 3 ) ||
                      ( approvedVector.get(0) != 6 ) ||
                      ( approvedVector.get(1) != 15 ) ||
                      ( approvedVector.get(2) != 19 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected approvedVector(1).\n");
                }
            }
        }

        /* try several invalid additions to the approved set */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(DBIndex.INVALID_ID);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.addApproved(INVALID_ID) failed to throw a" +
                            "SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.addApproved(INVALID_ID) returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(mike.getID()); /* not a predicate */
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.addApproved(mike.getID()) failed " +
                                    "to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.addApproved(mike.getID()) returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(p2.getID()); /* already in approved set */
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.addApproved(p2.getID()) failed " +
                                    "to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.addApproved(p2.getID()) returned.\n");
                }
            }
        }

        /* try invalid calls to arg.approved() */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.approved(DBIndex.INVALID_ID);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.approved(DBIndex.INVALID_ID) failed " +
                                    "to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.approved(DBIndex.INVALID_ID) returned.\n");
                }
            }
        }

        /* try invalid deletions from the approved set */
         if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(p1.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.deleteApproved(p1.getID()) failed " +
                                    "to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.deleteApproved(p1.getID()) returned.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(DBIndex.INVALID_ID);
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ! threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("arg.deleteApproved(INVALID_ID) failed " +
                                    "to throw a SystemErrorException).\n");
                }
            }
            else if ( testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "arg.deleteApproved(INVALID_ID) returned.\n");
                }
            }
        }

        /* now reduce the size of the approved set to 1 */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(p2.getID());
                arg.deleteApproved(p6.getID());
                approved0 = arg.approved(p0.getID());
                approved1 = arg.approved(p1.getID());
                approved2 = arg.approved(p2.getID());
                approved3 = arg.approved(p3.getID());
                approved4 = arg.approved(p4.getID());
                approved5 = arg.approved(p5.getID());
                approved6 = arg.approved(p6.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( approved0 != false ) || ( approved1 != false ) ||
                 ( approved2 != false ) || ( approved3 != false ) ||
                 ( approved4 != true )  || ( approved5 != false ) ||
                 ( approved6 != false ) ||
                 ( ! testFinished ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( approved0 != false ) || ( approved1 != false ) ||
                         ( approved2 != false ) || ( approved3 != false ) ||
                         ( approved4 != true )  || ( approved5 != false ) ||
                         ( approved6 != false ) )
                    {
                        outStream.print("unexpected approved results(2).\n");
                    }

                    if ( ! testFinished )
                    {
                        outStream.print("approved set test incomplete(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("SystemErrorException thrown in " +
                                         "approved set test(2): %s.\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* verify that getApprovedVector() and getApprovedString() work as they
         * should with one and zero entries.
         */

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<java.lang.Long> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("SystemErrorException thrown in call to " +
                                     "arg.getApprovedVector()(2): %s\n",
                                     systemErrorExceptionString);
                }
            }
            else if ( ! testFinished )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("test incomplete(4).\n");
                }
            }
            else if ( ( approvedVector.size() != 1 ) ||
                      ( approvedVector.get(0) != 15 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected approvedVector(2).\n");
                }
            }
        }


        if ( failures == 0 )
        {
            if ( arg.approvedSetToString().compareTo("(15)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "Unexpected arg.approvedSetToString() results(2).\n");
                }
            }
        }

        /* now reduce the size of the approved set to 0 */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(p4.getID());
                approved0 = arg.approved(p0.getID());
                approved1 = arg.approved(p1.getID());
                approved2 = arg.approved(p2.getID());
                approved3 = arg.approved(p3.getID());
                approved4 = arg.approved(p4.getID());
                approved5 = arg.approved(p5.getID());
                approved6 = arg.approved(p6.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( approved0 != false ) || ( approved1 != false ) ||
                 ( approved2 != false ) || ( approved3 != false ) ||
                 ( approved4 != false ) || ( approved5 != false ) ||
                 ( approved6 != false ) ||
                 ( ! testFinished ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( approved0 != false ) || ( approved1 != false ) ||
                         ( approved2 != false ) || ( approved3 != false ) ||
                         ( approved4 != false ) || ( approved5 != false ) ||
                         ( approved6 != false ) )
                    {
                        outStream.print("unexpected approved results(3).\n");
                    }

                    if ( ! testFinished )
                    {
                        outStream.print("approved set test incomplete(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("SystemErrorException thrown in " +
                                         "approved set test(3): %s.\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }

        /* verify that getApprovedVector() and getApprovedString() work as they
         * should with no entries.
         */

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;
            java.util.Vector<java.lang.Long> approvedVector = null;

            try
            {
                approvedVector = arg.getApprovedVector();
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( approvedVector != null ) ||
                 ( ! testFinished ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( approvedVector != null )
                    {
                        outStream.print("Unexpected approvedVector(3).\n");
                    }

                    if ( ! testFinished )
                    {
                        outStream.print("test incomplete(5).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("SystemErrorException thrown in " +
                                "call to arg.getApprovedVector()(3): %s\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.approvedSetToString().compareTo("()") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "Unexpected arg.approvedSetToString() results(3).\n");
                }
            }
        }

        /* now set subRange back to false.  Verify that approvedSet is set to
         * null, and that all approved set manipulation methods thow a system
         * error if invoked.
         */

        if ( failures == 0 )
        {
            arg.setSubRange(false);

            if ( ( arg.subRange != false ) ||
                 ( arg.getSubRange() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(false)\" failed " +
                            "to set arg.subRange to false.\n");
                }
            }
            else if ( arg.approvedSet != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "\"arg.setSubRange(false)\" failed " +
                            "to set arg.approvedSet to null.\n");
                }
            }
        }

        /* finally, verify that all the approved list management routines
         * flag a system error if invoked when subRange is false.
         */
        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.approved(p0.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testFinished ) || ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testFinished )
                    {
                        outStream.print(
                                "test completed with subrange == false (1).\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "arg.approved() failed to throw a " +
                                "SystemErrorException when subRange is false.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.addApproved(p1.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testFinished ) || ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testFinished )
                    {
                        outStream.print(
                            "test completed with subrange == false (2).\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "arg.addApproved() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.deleteApproved(p1.getID());
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testFinished ) || ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testFinished )
                    {
                        outStream.print(
                            "test completed with subrange == false (3).\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "arg.deleteApproved() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            testFinished = false;
            threwSystemErrorException = false;

            try
            {
                arg.getApprovedVector();
                testFinished = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testFinished ) || ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( testFinished )
                    {
                        outStream.print(
                            "test completed with subrange == false (4).\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "arg.getApprovedVector() failed to throw a " +
                            "SystemErrorException when subRange is false.\n");
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

    } /* PredFormalArg::TestAccessors() */


    /**
     * TestVEAccessors()
     *
     * Run a battery of tests on the itsVocabElement and itsVocabElementID
     * accessor methods for this class.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestVEAccessors(java.io.PrintStream outStream,
                                          boolean verbose)
    {
        String testBanner =
            "Testing class PredFormalArg itsVocabElement accessors            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new PredFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new PredFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new PredFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the itsVocabElement & itsVocabElementID accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures += FormalArgument.TestVEAccessors(arg, outStream,
                                                           verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("FormalArgument.TestVEAccessors()" +
                            " threw a SystemErrorException.\n");
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

    } /* PredFormalArg::TestVEAccessors() */


    /**
     * TestClassPredFormalArg()
     *
     * Main routine for tests of class PredFormalArg.
     *
     *                                      JRM -- 6/16/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassPredFormalArg(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class PredFormalArg:\n");

        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestVEAccessors(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidValue(outStream, verbose) )
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
            outStream.printf("%d failures in tests for class PredFormalArg.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class PredFormalArg.\n\n");
        }

        return pass;

    } /* PredFormalArg::TestClassPredFormalArg() */

    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class PredFormalArg           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        PredFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new PredFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print("new PredFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new PredFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<val>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        /* Verify that the constructor fails if passed a bad db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new PredFormalArg((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( methodReturned ) ||
                 ( arg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new PredFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                                "new PredFormalArg(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredFormalArg(null) didn't " +
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

        return pass;

    } /* PredFormalArg::Test1ArgConstructor() */

    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 2 argument constructor for class PredFormalArg           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        PredFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new PredFormalArg(new ODBCDatabase(), "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                        "new PredFormalArg(db, \"<valid>\") returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new PredFormalArg(db, \"<valid>\") " +
                                     "threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<valid>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db. */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new PredFormalArg(null, "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new PredFormalArg(null, \"<alid>>\") != null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new PredFormalArg(null, \"<valid>\") "
                                    + "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new PredFormalArg(new ODBCDatabase(), "<<invalid>>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new PredFormalArg(db, \"<<valid>>\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new PredFormalArg(db, \"<<invalid>>\") "
                        + "didn't throw a SystemErrorException.\n");
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

    } /* PredFormalArg::Test2ArgConstructor() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing copy constructor for class PredFormalArg                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int progress;
        String s = null;
        String systemErrorExceptionString = null;
        PredFormalArg arg0 = null;
        PredFormalArg arg1 = null;
        PredFormalArg copyArg0 = null;
        PredFormalArg copyArg1 = null;
        PredFormalArg munged = null;
        Database db = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        UnTypedFormalArg alpha   = null;
        UnTypedFormalArg bravo   = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta   = null;
        UnTypedFormalArg echo    = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf    = null;
        UnTypedFormalArg hotel   = null;
        UnTypedFormalArg india   = null;
        UnTypedFormalArg juno    = null;
        UnTypedFormalArg kilo    = null;
        UnTypedFormalArg lima    = null;
        UnTypedFormalArg mike    = null;
        UnTypedFormalArg nero    = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of PredFormalArg to be copied: */
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
        p0      = null;
        p1      = null;
        p2      = null;
        p3      = null;
        p4      = null;
        p5      = null;
        p6      = null;
        progress = 0;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            db = new ODBCDatabase();

            progress++;

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
            mike    = new UnTypedFormalArg(db, "<mike>");
            nero    = new UnTypedFormalArg(db, "<nero>");

            progress++;

            p0 = VocabList.ConstructTestPred(db, "p0",
                                             alpha, null, null, null);
            p1 = VocabList.ConstructTestPred(db, "p1",
                                             bravo, charlie, null, null);
            p2 = VocabList.ConstructTestPred(db, "p2",
                                             delta, echo, foxtrot, null);
            p3 = VocabList.ConstructTestPred(db, "p3",
                                             hotel, india, juno, kilo);
            p4 = VocabList.ConstructTestPred(db, "p4",
                                             lima, null, null, null);
            p5 = VocabList.ConstructTestPred(db, "p5",
                                             mike, null, null, null);
            p6 = VocabList.ConstructTestPred(db, "p6",
                                             nero, null, null, null);

            progress++;

            db.vl.addElement(p0);
            db.vl.addElement(p1);
            db.vl.addElement(p2);
            db.vl.addElement(p3);
            db.vl.addElement(p4);
            db.vl.addElement(p5);
            db.vl.addElement(p6);

            progress++;

            arg0 = new PredFormalArg(db, "<copy_this_0>");
            arg0.setHidden(true);
            p0.appendFormalArg(arg0);

            progress++;

            arg1 = new PredFormalArg(db, "<copy_this_1>");
            arg1.setSubRange(true);
            arg1.addApproved(p5.getID());
            arg1.addApproved(p2.getID());
            p1.appendFormalArg(arg1);

            progress++;

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( db == null ) ||
             ( arg0 == null ) ||
             ( arg1 == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("progress = %s\n", progress);

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( db == null )
                {
                    outStream.print("new ODBCDatabase() returned null.\n");
                }

                if ( arg0 == null )
                {
                    outStream.print(
                        "new PredFormalArg(\"<copy_this_0>\") returned null.\n");
                }

                if ( arg1 == null )
                {
                    outStream.print(
                        "new PredFormalArg(\"<copy_this_1>\") returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw an unexpected system " +
                            "error exception: %s", systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ! arg0.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected value of arg0.hidden.\n");
                }
            }
        }


        /* Now, try to make a copy of arg0 */

        if ( failures == 0 )
        {
            copyArg0 = null;
            copyArg1 = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                copyArg0 = new PredFormalArg(arg0);
                copyArg1 = new PredFormalArg(arg1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( copyArg0 == null ) || ( copyArg1 == null ) ||
                 ( completed == false ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 == null )
                    {
                        outStream.print(
                            "new PredFormalArg(arg0)\" returned null.\n");
                    }

                    if ( copyArg1 == null )
                    {
                        outStream.print(
                            "new PredFormalArg(arg1)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("pred copy constructor threw an " +
                                "unexpected SystemErrorException: %s\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* verify that the copies are good */

        if ( failures == 0 )
        {
            if ( arg0 == copyArg0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("(arg0 == copyArg0) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg1 == copyArg1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("(arg1 == copyArg1) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg0.toDBString().compareTo(copyArg0.toDBString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg0.toDBString() = \"%s\" != \" " +
                            "copyArg0.toDBString() = \"%s\".\n",
                            arg0.toDBString(), copyArg0.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg1.toDBString().compareTo(copyArg1.toDBString()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg1.toDBString() = \"%s\" != \" " +
                            "copyArg1.toDBString() = \"%s\".\n",
                            arg0.toDBString(), copyArg0.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg0.getHidden() != copyArg0.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg0.hidden = %b != " +
                            "copyArg0.hidden = %b.\n", arg0.hidden,
                            copyArg0.hidden);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg0.getHidden() != copyArg0.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg0.hidden = %b != " +
                            "copyArg0.hidden = %b.\n", arg0.hidden,
                            copyArg0.hidden);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg0.getItsVocabElement() != copyArg0.getItsVocabElement() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg0.getItsVocabElement() != \" " +
                            "copyArg0.getItsVocabElement().\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg1.getItsVocabElement() != copyArg1.getItsVocabElement() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg1.getItsVocabElement() != \" " +
                            "copyArg1.getItsVocabElement().\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg0.getItsVocabElementID() != copyArg0.getItsVocabElementID() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg0.getItsVocabElementID() != \" " +
                            "copyArg0.getItsVocabElementID().\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg1.getItsVocabElementID() != copyArg1.getItsVocabElementID() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg1.getItsVocabElementID() != \" " +
                            "copyArg1.getItsVocabElementID().\n");
                }
            }
        }

        /* now verify that we fail when we should */

        /* first ensure that the copy constructor failes when passed null */
        if ( failures == 0 )
        {
            munged = copyArg0; /* save the copy for later */
            copyArg0 = null;
            threwSystemErrorException = false;

            try
            {
                copyArg0 = null;
                copyArg0 = new PredFormalArg(copyArg0);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 != null )
                    {
                        outStream.print(
                            "new PredFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of PredFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg0 = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg0 = new PredFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 != null )
                    {
                        outStream.print(
                            "new PredFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredFormalArg(munged)\" " +
                                "didn't throw a SystemErrorException.\n");
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

    } /* PredFormalArg::TestCopyConstructor() */


    /**
     * TestIsValidValue()
     *
     * Verify that isValidValue() does the right thing.
     *
     * This test didn't get written for quite a while, as it depends on
     * other classes that didn't exit yet when the PredicateFormalArg
     * class was created.
     *
     *                                          JRM -- 11/22/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidValue(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing isValidValue()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean p0_is_valid;
        boolean p1_is_valid;
        boolean p2_is_valid;
        boolean p3_is_valid;
        boolean p4_is_valid;
        boolean p5_is_valid;
        boolean p6_is_valid;
        boolean p7_is_valid;
        boolean alt_p_is_valid;
        int failures = 0;
        int testNum = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        long alt_pveID = DBIndex.INVALID_ID;
        Database db = null;
        Database alt_db = null;
        FormalArgument farg = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        PredicateVocabElement alt_pve = null;
        PredFormalArg pfa = null;
        PredFormalArg pfa_sr = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;
        Predicate alt_p = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, pve's, and preds
        threwSystemErrorException = false;
        completed = false;

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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve2.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve2.appendFormalArg(farg);

            pve2ID = db.addPredVE(pve2);

            // get a copy of the databases version of pve1 with ids assigned
            pve2 = db.getPredVE(pve2ID);

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


            pve4 = new PredicateVocabElement(db, "pve4");

            farg = new FloatFormalArg(db, "<float>");
            pve4.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve4.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve4.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve4.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve4.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve4.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve4.appendFormalArg(farg);

            pve4ID = db.addPredVE(pve4);

            // get a copy of the databases version of pve4 with ids assigned
            pve4 = db.getPredVE(pve4ID);

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);



            alt_db = new ODBCDatabase();


            alt_pve = new PredicateVocabElement(alt_db, "alt_pve");
            farg = new UnTypedFormalArg(alt_db, "<alt_pve>");
            alt_pve.appendFormalArg(farg);

            alt_pveID = alt_db.addPredVE(alt_pve);

            // get a copy of the alt_db's version of alt_pve with ids assigned
            alt_pve = db.getPredVE(alt_pveID);

            alt_p = new Predicate(alt_db, alt_pveID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
             ( alt_pve == null ) ||
             ( alt_pveID == DBIndex.INVALID_ID ) ||
             ( alt_p == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( alt_pve == null )
                {
                    outStream.print("creation of alt_pve failed.\n");
                }

                if ( alt_pveID == DBIndex.INVALID_ID )
                {
                    outStream.print("alt_pveID not initialized.\n");
                }

                if ( alt_p == null )
                {
                    outStream.print("creation of alt_p failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* Now set up the test formal arguments */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                pfa = new PredFormalArg(db, "<pfa>");

                pfa_sr = new PredFormalArg(db, "<pfa_sr>");
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( pfa == null ) ||
                 ( pfa_sr == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pfa == null )
                    {
                        outStream.print("creation of pfa failed.\n");
                    }

                    if ( pfa_sr == null  )
                    {
                        outStream.print("creation of pfa_sr failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete (2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("pfa allocations threw a " +
                                "SystemErrorException: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else
            {
                p0_is_valid = pfa.isValidValue(p0);
                p1_is_valid = pfa.isValidValue(p1);
                p2_is_valid = pfa.isValidValue(p2);
                p3_is_valid = pfa.isValidValue(p3);
                p4_is_valid = pfa.isValidValue(p4);
                p5_is_valid = pfa.isValidValue(p5);
                p6_is_valid = pfa.isValidValue(p6);
                p7_is_valid = pfa.isValidValue(p7);
                alt_p_is_valid = pfa.isValidValue(alt_p);

                if ( ( ! p0_is_valid ) ||
                     ( ! p1_is_valid ) ||
                     ( ! p2_is_valid ) ||
                     ( ! p3_is_valid ) ||
                     ( ! p4_is_valid ) ||
                     ( ! p5_is_valid ) ||
                     ( ! p6_is_valid ) ||
                     ( ! p7_is_valid ) ||
                     ( alt_p_is_valid ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected results from " +
                            "pfa.isValidValue: %b %b %b %b %b %b %b %b %b\n",
                            p0_is_valid,
                            p1_is_valid,
                            p2_is_valid,
                            p3_is_valid,
                            p4_is_valid,
                            p5_is_valid,
                            p6_is_valid,
                            p7_is_valid,
                            alt_p_is_valid);
                    }
                }

                p0_is_valid = pfa_sr.isValidValue(p0);
                p1_is_valid = pfa_sr.isValidValue(p1);
                p2_is_valid = pfa_sr.isValidValue(p2);
                p3_is_valid = pfa_sr.isValidValue(p3);
                p4_is_valid = pfa_sr.isValidValue(p4);
                p5_is_valid = pfa_sr.isValidValue(p5);
                p6_is_valid = pfa_sr.isValidValue(p6);
                p7_is_valid = pfa_sr.isValidValue(p7);
                alt_p_is_valid = pfa_sr.isValidValue(alt_p);

                if ( ( ! p0_is_valid ) ||
                     ( p1_is_valid ) ||
                     ( ! p2_is_valid ) ||
                     ( p3_is_valid ) ||
                     ( ! p4_is_valid ) ||
                     ( p5_is_valid ) ||
                     ( ! p6_is_valid ) ||
                     ( p7_is_valid ) ||
                     ( alt_p_is_valid ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected results from " +
                            "pfa_sr.isValidValue: %b %b %b %b %b %b %b %b %b\n",
                            p0_is_valid,
                            p1_is_valid,
                            p2_is_valid,
                            p3_is_valid,
                            p4_is_valid,
                            p5_is_valid,
                            p6_is_valid,
                            p7_is_valid,
                            alt_p_is_valid);
                    }
                }

                if ( ( pfa.isValidValue(1.0) ) ||
                     ( pfa.isValidValue(1) ) ||
                     ( pfa.isValidValue("a string") ) ||
                     ( pfa.isValidValue(new TimeStamp(db.getTicks(), 0)) ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("pfa.isValidValue() accepted one or " +
                                        "more non-Predicates.\n");
                    }
                }

                if ( ( pfa_sr.isValidValue(1.0) ) ||
                     ( pfa_sr.isValidValue(1) ) ||
                     ( pfa_sr.isValidValue("a string") ) ||
                     ( pfa_sr.isValidValue(new TimeStamp(db.getTicks(), 0)) ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("pfa_sr.isValidValue() accepted one " +
                                        "or more non-Predicates.\n");
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

    } /* PredFormalArg::TestIsValidValue() */


    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 3/11/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        PredFormalArg arg = null;
        Database db = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        UnTypedFormalArg alpha   = null;
        UnTypedFormalArg bravo   = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta   = null;
        UnTypedFormalArg echo    = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf    = null;
        UnTypedFormalArg hotel   = null;
        UnTypedFormalArg india   = null;
        UnTypedFormalArg juno    = null;
        UnTypedFormalArg kilo    = null;
        UnTypedFormalArg lima    = null;
        UnTypedFormalArg mike    = null;
        UnTypedFormalArg nero    = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();
                arg = new PredFormalArg(db, "<test>");
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( db == null ) ||
                 ( arg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( arg == null )
                    {
                        outStream.print("new PredFormalArg(db, \"<test>\")" +
                                         "returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new PredFormalArg(db, \"<test>\") " +
                                         "threw a SystemErrorException.\n");
                    }
                }

                arg = null;
            }
        }

        if ( failures == 0 )
        {
            if ( arg != null )
            {
                if ( arg.toString().compareTo("<test>") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toString() returned unexpected value(1): \"%s\".\n",
                        arg.toString());
                }
            }

            if ( arg != null )
            {
                if ( arg.toDBString().compareTo(
                        "(PredFormalArg 0 <test> false ())") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toDBString() returned unexpected value(1): \"%s\".\n",
                        arg.toDBString());
                }
            }
        }

        /* now set subRange, add some approved nominals, and verify that
         * this is reflected in the output from toDBString().
         *
         * Must do some setup first.
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
            p0      = null;
            p1      = null;
            p2      = null;
            p3      = null;
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
                mike    = new UnTypedFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");

                p0 = VocabList.ConstructTestPred(db, "p0",
                                                 alpha, null, null, null);
                p1 = VocabList.ConstructTestPred(db, "p1",
                                                 bravo, charlie, null, null);
                p2 = VocabList.ConstructTestPred(db, "p2",
                                                 delta, echo, foxtrot, null);
                p3 = VocabList.ConstructTestPred(db, "p3",
                                                 hotel, india, juno, kilo);
                p4 = VocabList.ConstructTestPred(db, "p4",
                                                 lima, null, null, null);
                p5 = VocabList.ConstructTestPred(db, "p5",
                                                 mike, null, null, null);
                p6 = VocabList.ConstructTestPred(db, "p6",
                                                 nero, null, null, null);

                db.vl.addElement(p0);
                db.vl.addElement(p1);
                db.vl.addElement(p2);
                db.vl.addElement(p3);
                db.vl.addElement(p4);
                db.vl.addElement(p5);
                db.vl.addElement(p6);

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
                 ( nero == null ) ||
                 ( p0 == null ) || ( p1 == null ) ||
                 ( p2 == null ) || ( p3 == null ) ||
                 ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Setup for approved set test " +
                                        "failed to complete.\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) ||
                         ( juno == null ) || ( kilo == null ) ||
                         ( lima == null ) || ( mike == null ) ||
                         ( nero == null ) )
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
                                "one or more pred allocations failed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error exception " +
                                "in approved set test setup: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg.setSubRange(true);
                arg.addApproved(p1.getID());
                arg.addApproved(p3.getID());
                arg.addApproved(p5.getID());
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "Approved set setup failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorExcetpion " +
                                "in approved set setup: %s",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg != null )
            {
                if ( arg.toString().compareTo("<test>") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toString() returned unexpected value(2): \"%s\".\n",
                        arg.toString());
                }
            }

            if ( arg != null )
            {
                if ( arg.toDBString().compareTo(
                        "(PredFormalArg 0 <test> true (3, 10, 17))") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toDBString() returned unexpected value(2): \"%s\".\n",
                        arg.toDBString());
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

    } /* PredFormalArg::TestToStringMethods() */

} /* class PredFormalArg */
