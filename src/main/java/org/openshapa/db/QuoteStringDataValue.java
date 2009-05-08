/*
 * QuoteStringDataValue.java
 *
 * Created on August 18, 2007, 3:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * An instance of QuoteStringDataValue is used to store a quote string value
 * assigned to a formal argument.
 *
 * @author mainzer
 */

public final class QuoteStringDataValue extends DataValue {
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      float data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * minVal & maxVal don't appear in QuoteStringDataValue as at present,
     *      we don't support subranging in quote strings
     */

    /** default value for quote strings. */
    final String ItsDefault = null;

    /** the value assigned to the associated formal argument in this case. */
    String itsValue = ItsDefault;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * QuoteStringDataValue()
     *
     * Constructor for instances of QuoteStringDataValue.
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
     * The fourth takes a reference to an instance of QuoteStringDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public QuoteStringDataValue(Database db)
        throws SystemErrorException {

        super(db);

    } /* QuoteStringDataValue::QuoteStringDataValue(db) */

    public QuoteStringDataValue(Database db,
                                long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* QuoteStringDataValue::QuoteStringDataValue(db, fargID) */

    public QuoteStringDataValue(Database db,
                                long fargID,
                                String value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* QuoteStringDataValue::QuoteStringDataValue(db, fargID, value) */

    public QuoteStringDataValue(QuoteStringDataValue dv)
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

    } /* QuoteStringDataValue::QuoteStringDataValue(dv) */

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
        QuoteStringDataValue clone = (QuoteStringDataValue) super.clone();
        try {
            clone = new QuoteStringDataValue(this);
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
     *                          JRM -- 8/16/07
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

    } /* QuoteStringDataValue::getItsValue() */


    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::setItsValue(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! ( Database.IsValidQuoteString(value) ) )
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

    } /* QuoteStringDataValue::setItsValue() */

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
     *                                  JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *     - None.
     */
    @Override
    public String toString()
    {
        if ( this.itsValue == null )
        {
            return "\"\"";
        }
        else
        {
            return "\"" + new String(this.itsValue) + "\"";
        }

    } /* QuoteStringDataValue::toString() */


    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     *                                      JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     */
    @Override
    public String toDBString()
    {
        if ( this.itsValue == null )
        {
            return ("(QuoteStringDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(QuoteStringDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* QuoteStringDataValue::toDBString() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                          JRM -- 3/22/08
     *
     * Changes:
     *
     *    - None.
     */
    @Override
    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::updateForFargChange(): ";

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

    } /* QuoteStringDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Nominally, this method should determine if the formal argument
     * associated with the data value is subranged, and if it is, update
     * the data values representation of  the subrange (if ant) accordingly.
     * In passing, it should coerce the value of  the datavalue into the
     * subrange if necessary.
     *
     * However, quote strings can't be subranged at present, so all we do
     * is verify that the formal argument doesn't think otherwise.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    @Override
    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof QuoteStringFormalArg )
        {
            QuoteStringFormalArg qfa = (QuoteStringFormalArg)fa;

            if ( qfa.getSubRange() != false )
            {
                throw new SystemErrorException(mName +
                                               "qfa.getSubRange() != FALSE");
            }

            this.subRange = false;
        }
        else if ( fa instanceof UnTypedFormalArg )
        {
            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* QuoteStringDataValue::updateSubRange() */


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
     * However, we don't support subranges for quote strings.
     *
     * Thus we simply check to see if the value is valid, and return the
     * value if it is.  If it isn't, throw a system error.
     *
     *                                              JRM -- 07/08/18
     *
     * Changes:
     *
     *    - None.
     */

    public String coerceToRange(String value)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::coerceToRange(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }

        if ( ! Database.IsValidQuoteString(value) )
        {
            throw new SystemErrorException(mName +
                                           "value isn't valid quote string");
        }

        return value;

    } /* QuoteStringDataValue::coerceToRange() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of QuoteStringDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed QuoteStringDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static QuoteStringDataValue Construct(Database db,
                                                 String qs)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::Construct(db, qs)";
        QuoteStringDataValue qsdv = null;

        qsdv = new QuoteStringDataValue(db);

        qsdv.setItsValue(qs);

        return qsdv;

    } /* QuoteStringDataValue::Construct(db, qs) */

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
     * Compares this NominalDataValue against another object.
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
        QuoteStringDataValue q = (QuoteStringDataValue) obj;
        return ((itsValue == null && q.itsValue == null)
                        || (itsValue != null && itsValue.equals(q.itsValue)))
               && super.equals(obj);
    }

} /* QuoteStringDataValue */

