/*
 * NominalFormalArg.java
 *
 * Created on March 14, 2007, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Class NominalFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to nominals.
 */

public class NominalFormalArg extends FormalArgument
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
     * approvedSet: Set of nominals that may be used to replace this
     *      formal argument.  The field is ignored and should be null if
     *      subRange is false,
     *
     *      At present, the approvedSet is implemented with TreeSet, so as
     *      to quickly provide a sorted list of approved nominals.  If this
     *      turns out to be unnecessary, we should use HashSet instead.
     */

    /** Whether values are restricted to members of the approvedList */
    boolean subRange = false;

    /** If subRange is true, set of nominal that may replace the formal arg. */
    java.util.SortedSet<String> approvedSet = null;

    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * NominalFormalArg()
     *
     * Constructors for nominal typed formal arguments.
     *
     * Three versions of this constructor -- one that takes only a database
     * referenece, one that takes a database reference and the formal argument
     * name as a parameters, and one that takes a reference to an instance of
     * NominalFormalArg and uses it to create a copy.
     *
     *                                          -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public NominalFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.NOMINAL;

    } /* NominalFormalArg() -- no parameters */

    public NominalFormalArg(Database db,
                            String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.NOMINAL;

    } /* NominalFormalArg() -- one parameter */

    public NominalFormalArg(NominalFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "NominalFormalArg::NominalFormalArg(): ";

        this.fargType = FArgType.NOMINAL;

        // copy over fields.

        this.subRange = fArg.getSubRange();

        if ( this.subRange )
        {
            /* copy over the approved list from fArg. */
            java.util.Vector<String> approvedVector = fArg.getApprovedVector();

            this.approvedSet = new java.util.TreeSet<String>();

            for ( String s : approvedVector )
            {
                this.addApproved(s);
            }
        }

    } /* NominalFormalArg() -- make copy */



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
     *                                          -- 3/15/07
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
        final String mName = "NominalFormalArg::setSubRange(): ";

        if ( this.subRange != subRange )
        {
            /* we have work to do. */
            if ( subRange )
            {
                this.subRange = true;
                approvedSet = new java.util.TreeSet<String>();
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
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of NominalDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * NominalDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        NominalDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new NominalDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof NominalDataValue )
        {
            retVal = new NominalDataValue(this.getDB(), this.getID(),
                    ((NominalDataValue)salvage).getItsValue());
        }
        else if ( ( salvage instanceof QuoteStringDataValue ) &&
                  ( ((QuoteStringDataValue)salvage).getItsValue() != null ) &&
                  ( Database.IsValidNominal
                        (((QuoteStringDataValue)salvage).getItsValue()) ))
        {
            retVal = new NominalDataValue(this.getDB(), this.getID(),
                    ((QuoteStringDataValue)salvage).getItsValue());
        }
        else if ( ( salvage instanceof TextStringDataValue ) &&
                  ( ((TextStringDataValue)salvage).getItsValue() != null ) &&
                  ( Database.IsValidNominal
                        (((TextStringDataValue)salvage).getItsValue())))
        {
            retVal = new NominalDataValue(this.getDB(), this.getID(),
                    ((TextStringDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new NominalDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* NominalDataValue::constructArgWithSalvage(salvage) */


    /**
     * addApproved()
     *
     * Add the supplied nominal to the approved set.
     *
     * The method throws a system error if subRange is false, if passed a null,
     * if passed an invalid nominal, or if the approved list already contains
     * the supplied nominal.
     *                                          -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public void addApproved(String s)
        throws SystemErrorException
    {
        final String mName = "NominalFormalArg::addApproved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! Database.IsValidNominal(s) )
        {
            throw new SystemErrorException(mName + "s is not a nominal.");
        }
        else if ( ! this.approvedSet.add(new String(s)) )
        {
            throw new SystemErrorException(mName + "s already in approved set.");
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
     *                                          -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean approved(String test)
        throws SystemErrorException
    {
        final String mName = "NominalFormalArg::approved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( this.approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! Database.IsValidNominal(test) )
        {
            throw new SystemErrorException(mName + "test is not a nominal.");
        }

        return approvedSet.contains(test);

    } /* NominalFormalArg::approved() */


    /**
     * approvedSetToString()
     *
     * Construct and return a string representation of the approved set.
     *
     *                                          -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     */

    private String approvedSetToString()
    {
        final String mName = "NominalFormalArg::approvedSetToString(): ";
        String s = null;
        int i;
        java.util.Iterator<String> iterator = null;

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
                s += iterator.next();
            }

            while ( iterator.hasNext() )
            {
                s += ", " + iterator.next();
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
     * Delete the supplied nominal from the approved set.
     *
     * The method throws a system error if subRange is false, if passed a null,
     * if passed an invalid nominal, or if the approved list does not contain
     * the supplied nominal.
     *                                          -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public void deleteApproved(String s)
        throws SystemErrorException
    {
        final String mName = "NominalFormalArg::deleteApproved(): ";

        if ( ! this.subRange )
        {
            throw new SystemErrorException(mName + "subRange is false.");
        }
        else if ( approvedSet == null )
        {
            throw new SystemErrorException(mName + "approvedSet is null?!?!");
        }
        else if ( ! Database.IsValidNominal(s) )
        {
            throw new SystemErrorException(mName + "s is not a nominal.");
        }
        else if ( ! this.approvedSet.remove(s) )
        {
            throw new SystemErrorException(mName + "s not in approved set.");
        }

        return;

    } /* NominalFormalArg::deleteApproved() */


    /**
     * getApprovedVector()
     *
     * Return an vector of String containing an alphabetical list of all
     * entries in the approved set, or null if the approved list is empty.
     *
     * The method throws a system error if subRange is false.
     *
     *                                              -- 3/15/07
     *
     * Changes:
     *
     *    - None.
     */

    java.util.Vector<String> getApprovedVector()
        throws SystemErrorException
    {
        final String mName = "NominalFormalArg::getApprovedList(): ";
        java.util.Vector<String> approvedVector = null;
        int i;

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
            /* make copies of all strings in the approved set and then insert
             * them in the approvedVector.  We make copies so that
             * they can't be changed out from under the approved set.
             *
             * From reading the documentation, it is not completely clear that
             * this is necessary, but better safe than sorry.
             */
            approvedVector = new java.util.Vector<String>();

            for ( String s : this.approvedSet )
            {
                approvedVector.add(new String(s));
            }
        }

        return approvedVector;

    } /* NominalFormalArg::getApprovedVector() */

    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of NominalDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new NominalDataValue(this.getDB(), this.getID());

     } /* NominalFormalArg::constructEmptyArg() */


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
     *                                      -- 2/13/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString() {

        return ("(NominalFormalArg " + getID() + " " + getFargName() + " " +
                getSubRange() + " " + approvedSetToString() + ")");

    } /* NominalFormalArg::toDBString() */


    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean metho that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                             -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        if ( ! Database.IsValidNominal(obj) )
        {
            return false;
        }

        return true;

    } /*  NominalFormalArg::isValidValue() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += (new Boolean(subRange)).hashCode() * Constants.SEED2;
        hash += HashUtils.Obj2H(approvedSet) * Constants.SEED3;

        return hash;
    }

    /**
     * Compares this predicate formal argument against a object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this, false
     * otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        NominalFormalArg nfa = (NominalFormalArg) obj;
        return super.equals(nfa) && subRange == nfa.subRange
               && (approvedSet == null ? nfa.approvedSet == null
                                       : approvedSet.equals(nfa.approvedSet));
    }

} /* class NominalFormalArg */
