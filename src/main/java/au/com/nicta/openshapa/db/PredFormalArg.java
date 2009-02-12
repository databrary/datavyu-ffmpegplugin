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

        this.fargType = FArgType.PREDICATE;

    } /* NominalFormalArg() -- no parameters */

    public PredFormalArg(Database db,
                         String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.PREDICATE;

    } /* NominalFormalArg() -- one parameter */

    public PredFormalArg(PredFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "PredFormalArg::PredFormalArg(): ";

        this.fargType = FArgType.PREDICATE;

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
        else if ( ! this.getDB().vl.predInVocabList(predID) )
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
        else if ( ! this.getDB().vl.predInVocabList(predID) )
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
        else if ( ! this.getDB().vl.predInVocabList(predID) )
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
            retVal = new PredDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof PredDataValue )
        {
            retVal = new PredDataValue(this.getDB(), this.getID(),
                    ((PredDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new PredDataValue(this.getDB(), this.getID());
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

         return new PredDataValue(this.getDB(), this.getID());

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

            if ( pred.getDB() != this.getDB() )
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

} /* class PredFormalArg */
