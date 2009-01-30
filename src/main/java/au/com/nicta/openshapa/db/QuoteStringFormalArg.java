/*
 * QuoteStringFormalArg.java
 *
 * Created on February 13, 2007, 7:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class QuoteStringFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to quote string.  This class of formal argument will only appear in
 * matrix and predicate argument lists.
 *
 *                                                      JRM -- 2/13/07
 *
 *
 * @author mainzer
 */
public class QuoteStringFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any valid quote string, or only by some quote string
     *      that meets some criteria.
     *
     *      At present, subRange will always be false, as we have no immediate
     *      plans to support subrange types on quote strings.
     */

    boolean subRange = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * QuoteStringFormalArg()
     *
     * Constructors for quote string typed formal arguments.
     *
     * Three versions of this constructor -- one that takes no arguments, one
     * that takes the formal argument name as a parameter, and one that takes
     * a reference to an instance of QuoteStringFormalArg and uses it to create
     * a copy.
     *
     *                                          JRM -- 2/13/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public QuoteStringFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = fArgType.QUOTE_STRING;

    } /* QuoteStringFormalArg() -- no parameters */

    public QuoteStringFormalArg(Database db,
                                String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = fArgType.QUOTE_STRING;

    } /* QuoteStringFormalArg() -- one parameter */

    public QuoteStringFormalArg(QuoteStringFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = fArgType.QUOTE_STRING;

        // copy over fields.

        this.subRange = fArg.getSubRange();

    } /* QuoteStringFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getSubRange()
     *
     * Accessor routine used to obtain the current value of the subRange.
     *
     *                                          JRM -- 2/13/07
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


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of QuoteStringDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * QuoteStringDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        QuoteStringDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof QuoteStringDataValue )
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID(),
                    ((QuoteStringDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof NominalDataValue )
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID(),
                                    ((NominalDataValue)salvage).getItsValue());
        }
        else if ( ( salvage instanceof TextStringDataValue ) &&
                  ( ((TextStringDataValue)salvage).getItsValue() != null ) &&
                  ( Database.IsValidQuoteString
                     (((TextStringDataValue)salvage).getItsValue())))
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID(),
                                ((TextStringDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* QuoteStringDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of QuoteStringDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new QuoteStringDataValue(this.getDB(), this.getID());

     } /* QuoteStringFormalArg::constructEmptyArg() */


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
     *                                      JRM -- 2/13/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString() {

        return ("(QuoteStringFormalArg " + getID() + " " + getFargName() + ")");

    } /* QuoteStringFormalArg::toDBString() */


    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean metho that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                             JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        if ( ! Database.IsValidQuoteString(obj) )
        {
            return false;
        }

        return true;

    } /*  QuoteStringFormalArg::isValidValue() */

} /* class QuoteStringFormalArg */
