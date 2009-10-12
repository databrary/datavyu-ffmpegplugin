package org.openshapa.db;

import org.openshapa.util.Constants;

/**
 * Instances of TextStringFormalArg are used as the formal argument in the
 * single element matricies used to implement text column variables.
 */
public class TextStringFormalArg extends FormalArgument
{

    /**
     * Boolean flag indicating whether the formal argument can be replaced by
     * any valid text string, or only by some text string that matches some
     * criteron.  At present, this will never be the case, so this field will
     * always be false.
     */
    boolean subRange = false;


    // TextStringFormalArg()
    /**
     * Constructor.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param db The parent database for this TextStringFormalArgument.
     *
     * @throws SystemErrorException if Unable to create a TextStringFormalArg
     *
     * @date 2007/02/12
     */

    public TextStringFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.TEXT;

    } /* TextStringFormalArg() -- no parameters */

    /**
     * Copy Constructor.
     *
     * @param fArg TextStringFormalArg to copy.
     *
     * @throws SystemErrorException If unable to copy the supplied
     * TextStringFormalArg
     *
     * @date 2007/02/12
     */
    public TextStringFormalArg(TextStringFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "TextStringFormalArg::TextStringFormalArg(): ";

        this.fargType = FArgType.TEXT;

        // copy over fields.

        this.subRange = fArg.getSubRange();

    } /* TextStringFormalArg() -- make copy */


    // getSubRange()
    /**
     * @return The subrange used for this TextString formal argument.
     *
     * @date 2007/02/12
     */
    public boolean getSubRange()
    {
        return subRange;
    }


    // constructArgWithSalvage() - Override of abstract method in FormalArgument
    /**
     * Constructs a argument from salvage.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param salvage The data value to salvage when constructing a new
     * TextStringDataValue.
     *
     * @return An instance of TextStringDataValue initialized from salvage if
     * possible, otherwise a default instance of TextStringDataValue.
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


    // constructEmptyArg() - Override of abstract method in FormalArgument.
    /**
     * @return An empty value that can be used as an empty value for this formal
     * argument.
     *
     * @throws SystemErrorException If unable to create an empty value for
     * this formal argument.
     */
    public DataValue constructEmptyArg()
        throws SystemErrorException
    {
        return new TextStringDataValue(this.getDB(), this.getID());
    } /* TextStringFormalArg::constructEmptyArg() */


    // toDBString - Override of abstract method in DataValue.
    /**
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     */
    public String toDBString() {

        return ("(TextStringFormalArg " + getID() + " " + getFargName() + ")");

    } /* TextStringFormalArg::toDBString() */


    // isValidValue() - Override of abstract method in FormalArgument
    /**
     * @return true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     * @date 2007/02/05
     */
    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {

        return Database.IsValidTextString(obj);

    } /* TextStringFormalArg::isValidValue() */


    // hashCode()
    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += (new Boolean(subRange)).hashCode() * Constants.SEED2;

        return hash;
    }


    // equals()
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
