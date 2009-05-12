/*
 * TextStringFormalArg.java
 *
 * Created on February 12, 2007, 9:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;

/**
 * Class TextStringFormalArg
 *
 * Instances of TextStringFormalArg are used as the formal argument in the
 * single element matricies used to implement text column variables
 */
public class TextStringFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any valid text string, or only by some text string
     *      that matches some criteron.  At present, this will never be the
     *      case, so this field will always be false.
     */

    boolean subRange = false;

    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * TextStringFormalArg()
     *
     * Constructors for integer typed formal arguments.
     *
     * Two versions of this constructor -- one takes only a database reference
     * as its argument.  Since the names of instances of TextStringFormalArg
     * are never displayed, there is no need to set a formal argument name.
     * Similarly, there is no subrange to be defined.
     *
     * The second version takes an instance of TextStringFormalArg and uses it
     * to make a copy.
     *
     *                                          -- 2/12/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public TextStringFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.TEXT;

    } /* TextStringFormalArg() -- no parameters */

    public TextStringFormalArg(TextStringFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "TextStringFormalArg::TextStringFormalArg(): ";

        this.fargType = FArgType.TEXT;

        // copy over fields.

        this.subRange = fArg.getSubRange();

    } /* TextStringFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getSubRange()
     *
     * Accessor routine used to obtain the current values of the subRange field.
     *
     *                                          -- 2/12/07
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
     * Return an instance of TextStringDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * TextStringDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        TextStringDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new TextStringDataValue(this.getDB(), this.getID());
        }
        else if ( ( salvage instanceof QuoteStringDataValue ) &&
                  ( ((QuoteStringDataValue)salvage).getItsValue() != null ) &&
                  ( Database.IsValidTextString
                        (((QuoteStringDataValue)salvage).getItsValue()) ) )

        {
            retVal = new TextStringDataValue(this.getDB(), this.getID(),
                    ((QuoteStringDataValue)salvage).getItsValue());
        }
        else if ( ( salvage instanceof NominalDataValue ) &&
                  ( ((NominalDataValue)salvage).getItsValue() != null ) &&
                  ( Database.IsValidTextString
                        (((NominalDataValue)salvage).getItsValue())))
        {
            retVal = new TextStringDataValue(this.getDB(), this.getID(),
                    ((NominalDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new TextStringDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* TextStringDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of TextStringDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new TextStringDataValue(this.getDB(), this.getID());

     } /* TextStringFormalArg::constructEmptyArg() */


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
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString() {

        return ("(TextStringFormalArg " + getID() + " " + getFargName() + ")");

    } /* TextStringFormalArg::toDBString() */


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

        return Database.IsValidTextString(obj);

    } /* TextStringFormalArg::isValidValue() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += (new Boolean(subRange)).hashCode() * Constants.SEED2;

        return hash;
    }

    /**
     * Compares this text string formal argument against a object.
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

        TextStringFormalArg tsfa = (TextStringFormalArg) obj;
        return super.equals(obj) && subRange == tsfa.subRange;
    }

} /* class TextStringFormalArg */
