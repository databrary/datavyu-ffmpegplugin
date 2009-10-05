/*
 * TextStringDataValue.java
 *
 * Created on August 18, 2007, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;

/**
 * An instance of TextStringDataValue is used to store a quote string value
 * assigned to a formal argument.
 */

public final class TextStringDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      float data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * minVal & maxVal don't appear in TextStringDataValue as at present,
     *      we don't support subranging in quote strings
     */

    /** default value for text strings */
    final String ItsDefault = null;

    /** the value assigned to the associated formal argument in this case */
    String itsValue = ItsDefault;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * TextStringDataValue()
     *
     * Constructor for instances of TextStringDataValue.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID, and
     * attempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of TextStringDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public TextStringDataValue(Database db)
        throws SystemErrorException
    {

        super(db);

    } /* TextStringDataValue::TextStringDataValue(db) */

    public TextStringDataValue(Database db,
                               long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* TextStringDataValue::TextStringDataValue(db, fargID) */

    public TextStringDataValue(Database db,
                               long fargID,
                               String value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* TextStringDataValue::TextStringDataValue(db, fargID, value) */

    public TextStringDataValue(TextStringDataValue dv)
        throws SystemErrorException
    {

        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue = new String(dv.itsValue);
        }
        else
        {
            this.itsValue = null;
        }

    } /* TextStringDataValue::TextStringDataValue(dv) */

    /**
     * Creates a new copy of the object.
     *
     * @return A duplicate of this object.
     *
     * @throws java.lang.CloneNotSupportedException If the clone interface has
     * not been implemented.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        TextStringDataValue clone = (TextStringDataValue) super.clone();

        try {
            clone = new TextStringDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * If the data value is currently defined, return a string containing a
     * copy of the the current value of the data value.  Otherwise return null.
     *
     *                           -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public String getItsValue()
    {

        if ( this.itsValue == null )
        {
            return null;
        }
        else
        {
            return (new String(this.itsValue));
        }

    } /* TextStringDataValue::getItsValue() */


    /**
     * setItsValue()
     *
     * Set itsValue to the specified value if it is valid.  Otherwise
     * throw a system error.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::setItsValue(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! ( getDB().IsValidTextString(value) ) )
        {
            throw new SystemErrorException(mName +
                                           "value not valid quote string");
        }
        else
        {
            this.itsValue = new String(value);
        }

        this.valueSet();
        return;

    } /* TextStringDataValue::setItsValue() */

    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return itsValue == null ?
                           itsValue == ItsDefault : itsValue.equals(ItsDefault);
    }


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * toString()
     *
     * Returns a String representation of the DBValue for display.
     *
     *                                   -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *     - None.
     */

    public String toString()
    {
        if ( this.itsValue == null )
        {
            return "";
        }
        else
        {
            return new String(this.itsValue);
        }

    } /* TextStringDataValue::toString() */

    public String toEscapedString() {
        if (this.itsValue == null) {
            return "";
        } else {
            return StringUtils.escapeCSV(this.itsValue);
        }

    }

    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     *                                       -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     */

    public String toDBString()
    {
        if ( this.itsValue == null )
        {
            return ("(TextStringDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(TextStringDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* TextStringDataValue::toDBString() */


    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will an instantiation of <text_quote_string>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     *                                              1/18/09
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(java.io.PrintStream output)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "TextStringDataValue::toMODBFile()";
        char ch;
        StringBuilder tmp = new StringBuilder("");
        int i;

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( ( this.itsValue != null ) && ( this.itsValue.length() > 0 ) )
        {
            for ( i = 0; i < this.itsValue.length(); i++ )
            {
                ch = this.itsValue.charAt(i);

                if ( ( ch < 0 ) || ( ch > 0x7F ) || ( ch == '\b') )
                {
                    // string contains a character that can't appear in a
                    // text string.
                    throw new SystemErrorException(mName +
                            "itsValue contains an illegal character.");
                }
                else if ( ( ch == '\'' ) || ( ch == '\"' ) || ( ch == '\\' ) )
                {
                    // the next character must be escaped.
                    tmp.append('\\');
                }

                tmp.append(ch);
            }
        }

        output.printf("\"%s\" ", tmp.toString());

        return;

    } /* TextStringDataValue::toMODBFile() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                           -- 3/22/08
     *
     * Changes:
     *
     *    - None.
     */

    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::updateForFargChange(): ";

        if ( ( oldFA == null ) || ( newFA == null ) )
        {
            throw new SystemErrorException(mName +
                                           "null old and/or new FA on entry.");
        }

        if ( oldFA.getID() != newFA.getID() )
        {
            throw new SystemErrorException(mName + "old/new FA ID mismatch.");
        }

        if ( oldFA.getItsVocabElementID() != newFA.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName + "old/new FA veID mismatch.");
        }

        if ( oldFA.getFargType() != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "old/new FA type mismatch.");
        }

        if ( this.itsFargID != newFA.getID() )
        {
            throw new SystemErrorException(mName + "FA/DV faID mismatch.");
        }

        if ( this.itsFargType != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "FA/DV FA type mismatch.");
        }

        if ( ( fargSubRangeChanged ) || ( fargRangeChanged ) )
        {
            this.updateSubRange(newFA);
        }

        return;

    } /* TextStringDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Nominally, this method should determine if the formal argument
     * associated with the data value is subranged, and if it is, update
     * the data values representation of  the subrange (if ant) accordingly.
     * In passing, it should coerce the value of  the datavalue into the
     * subrange if necessary.
     *
     * However, text strings can't be subranged at present, so all we do
     * is verify that the formal argument doesn't think otherwise.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                           -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof TextStringFormalArg )
        {
            TextStringFormalArg tfa = (TextStringFormalArg)fa;

            if ( tfa.getSubRange() != false )
            {
                throw new SystemErrorException(mName +
                                               "tfa.getSubRange() != FALSE");
            }

            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* TextStringDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * Nominally, this function tests to see if the supplied value is
     * in range for the associated formal argument, returns it if it
     * is, and coerces it into range if it isn't.
     *
     * However, we don't support subranges for text strings.
     *
     * Thus we simply check to see if the value is valid, and return the
     * value if it is.  If it isn't, throw a system error.
     *
     *                                               -- 07/08/18
     *
     * Changes:
     *
     *    - None.
     */

    public String coerceToRange(String value)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::coerceToRange(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }

        if ( ! this.getDB().IsValidTextString(value) )
        {
            throw new SystemErrorException(mName +
                                           "value isn't valid quote string");
        }

        return value;

    } /* TextStringDataValue::coerceToRange() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of TextStringDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed TextStringDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static TextStringDataValue Construct(Database db,
                                                String t)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::Construct(db, t)";
        TextStringDataValue tdv = null;

        tdv = new TextStringDataValue(db);

        tdv.setItsValue(t);

        return tdv;

    } /* TextStringDataValue::Construct(db, t) */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

        return hash;
    }

    /**
     * Compares this TextStringDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        // Must be this class to be here
        TextStringDataValue t = (TextStringDataValue) obj;
        return ((itsValue == null && t.itsValue == null)
                        || (itsValue != null && itsValue.equals(t.itsValue)))
            && super.equals(obj);
    }

} /* TextStringDataValue */

