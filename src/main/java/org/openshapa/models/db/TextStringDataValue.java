package org.openshapa.models.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;

/**
 * An instance of TextStringDataValue is used to store a quote string value
 * assigned to a formal argument.
 */
public final class TextStringDataValue extends DataValue
{
    /** default value for text strings. */
    final String ItsDefault = null;

    /** The value assigned to this text string data value. */
    String itsValue = ItsDefault;


    /**
     * Constructor.
     *
     * @param db The parent database to which this TextStringDataValue will
     * belong.
     * @throws SystemErrorException If unable to create the TextStringDataValue.
     *
     * @date 2007/08/16
     */
    public TextStringDataValue(Database db)
        throws SystemErrorException
    {
        super(db);

    } /* TextStringDataValue::TextStringDataValue(db) */


    /**
     * Constructor.
     *
     * @param db The parent database to which this TextStringDataValue will
     * belong.
     * @param fargID The ID of a parent formal argument, that this is a value
     * for.
     * @throws SystemErrorException If unable to create the TextStringDataValue.
     *
     * @date 2007/08/16
     */
    public TextStringDataValue(Database db,
                               long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* TextStringDataValue::TextStringDataValue(db, fargID) */


    /**
     * Constructor.
     *
     * @param db The parent database to which this TextStringDataValue will
     * belong.
     * @param fargID The ID of a parent formal argument, that this is a value
     * for.
     * @param value The value to use with this TextStringDataValue.
     *
     * @throws SystemErrorException If unable to create the TextStringDataValue.
     *
     * @date 2007/08/16
     */
    public TextStringDataValue(Database db,
                               long fargID,
                               String value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* TextStringDataValue::TextStringDataValue(db, fargID, value) */


    /**
     * Copy Constructor.
     *
     * @param dv The data value to create a copy from.
     *
     * @throws SystemErrorException If unable to create the TextStringDataValue.
     *
     * @date 2007/08/16
     */
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
     * @throws CloneNotSupportedException If the clone interface has
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


    // getItsValue()
    /**
     * @returns If the a value is defined, a copy of the current value,
     * otherwise return null.
     *
     * @date 2007/08/16
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


    // setItsValue()
    /**     * Set itsValue to the specified value if it is valid.
     * Sets itsValue to the specified value.
     *
     * @param value The new value to use for this data value.
     *
     * @throws SystemErrorException if the supplied value is invalid.
     *
     * @date 2007/08/16
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


    // isDefault()
    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return itsValue == null ?
                           itsValue == ItsDefault : itsValue.equals(ItsDefault);
    }


    // toString()
    /**
     * @return the string representation of the DBValue for display.
     *
     * @date 2007/08/15
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


    // toEscapedString()
    /**
     * @return The string represnetation of the DBValue with CSV delimiters
     * escaped with a '\'.
     */
    public String toEscapedString() {
        if (this.itsValue == null) {
            return "";
        } else {
            return StringUtils.escapeCSV(this.itsValue);
        }

    }


    // toDBString()
    /**
     * @return A database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     * @date 2007/08/15
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


    // toMODBFile()
    /**
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will an instantiation of <text_quote_string>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     * @param output The target stream to output this TextStringDataValue as a
     * MacSHAPA ODB file.
     *
     * @throws SystemErrorException If the supplied output is null
     * @throws IOException If unable to write the TextStringDataValue to the
     * supplied PrintStream.
     *
     * @date 2009/01/18
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


    // updateForFargChange()
    /**
     * Update for a change in the formal argument name, and/or subrange.
     *
     * @param fargNameChanged Has the formal argument name changed?
     * @param fargSubRangeChanged Has the formal argument subrange changed?
     * @param fargRangeChanged Has the formal argument range changed?
     * @param oldFA The old formal argument, before the indicated changes.
     * @param newFA The new formal argument, fater the indicated changes.
     *
     * @throws SystemErrorException If unable to update for formal argument
     * change.
     *
     * @date 2008/03/22
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


    // updateSubRange()
    /**
     * Nominally, this method should determine if the formal argument
     * associated with the data value is subranged, and if it is, update
     * the data values representation of  the subrange (if ant) accordingly.
     * In passing, it should coerce the value of  the datavalue into the
     * subrange if necessary.
     *
     * However, text strings can't be subranged at present, so all we do
     * is verify that the formal argument doesn't think otherwise.
     *
     * @param fa A reference to the current representation of the formal
     * argument associated with the data value.
     *
     * @throws SystemErrorException if the supplied formal argument is null,
     * or is not an instance of a TextStringFormalArg.
     *
     * @date 2007/08/16
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


    // coerceToRange()
    /**
     * Nominally, this function tests to see if the supplied value is
     * in range for the associated formal argument, returns it if it
     * is, and coerces it into range if it isn't.
     *
     * However, we don't support subranges for text strings.
     *
     * Thus we simply check to see if the value is valid, and return the
     * value if it is.  If it isn't, throw a system error.
     *
     * @param value The value to coerce to the range of the data value.
     *
     * @throws SystemErrorException If the supplied value is invalid.
     *
     * @date 2007/08/18
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


    // Construct()
    /**
     * Construct an instance of TextStringDataValue with the specified
     * initialization.
     *
     * @param db The parent database that the new TextStringDataValue will
     * belong too.
     *
     * @param t The value to supplied to the new TextStringDataValue.
     *
     * @return A reference to the newly constructed TextStringDataValue if
     * successful.
     *
     * @throws SystemErrorException If unable to create the TextStringDataValue.
     *
     * @date 2008/03/31
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


    // hashCode()
    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

        return hash;
    }


    // equals()
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

