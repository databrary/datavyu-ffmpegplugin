/*
 * NominalDataValue.java
 *
 * Created on August 17, 2007, 5:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;


/**
 * An instance of NominalDataValue is used to store a nominal value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public final class NominalDataValue extends DataValue {

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsDefault:  Constant containing the value to be assigned to all
     *      float data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * queryVar: Boolean that is set to true iff the the value of the nominal
     *      is a valid query variable -- that is if the nominal starts with a
     *      '?', and is at least two characters long.
     *
     *      The concept of a query variable is a hold over from MacSHAPA, that
     *      I was hoping to get rid of in OpenSHAPA -- but it seems that I am
     *      stuck with it afterall.  However, by implementing it in this way,
     *      we make it easy to ignore it in contexts other than the MacSHAPA
     *      query column variable.
     *
     * minVal & maxVal don't appear in NominalDataValue as a subrange of
     *      nominals is expressed as a set of allowed values.  Given the
     *      potential size of this set, we don't keep a copy of it here --
     *      referring directly to the associated formal argument when needed
     *      instead.
     */

    /** default value for nominals. */
    final String ItsDefault = null;

    /** the value assigned to the associated formal argument in this case. */
    String itsValue = ItsDefault;

    /** whether the value currently assigned to the Nominal is a valid query
     *  variable name.
     */
    boolean queryVar = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * NominalDataValue()
     *
     * Constructor for instances of NominalDataValue.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID, and
     * ttempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of NominalDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public NominalDataValue(Database db)
        throws SystemErrorException {

        super(db);

    } /* NominalDataValue::NominalDataValue(db) */

    public NominalDataValue(Database db,
                           long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* NominalDataValue::NominalDataValue(db, fargID) */

    public NominalDataValue(Database db,
                           long fargID,
                           String value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* NominalDataValue::NominalDataValue(db, fargID, value) */

    public NominalDataValue(NominalDataValue dv)
        throws SystemErrorException
    {

        super(dv);

        if ( dv.itsValue == null )
        {
            this.itsValue = null;
        }
        else
        {
            this.itsValue = new String(dv.itsValue);
        }

    } /* NominalDataValue::NominalDataValue(dv) */

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
        NominalDataValue clone = (NominalDataValue) super.clone();
        try {
            clone = new NominalDataValue(this);
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

    } /* NominlDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.  That is hard to do with nominals, so for the
     * nonce, we just set itsValue to null -- indicating that the nominal
     * data value is undefined.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - Added code to maintain this.isQueryVar. JRM -- 10/20/08
     */

    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::setItsValue(): ";
        DBElement dbe;
        NominalFormalArg nfa;

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! this.subRange ) // Just verify that value is a valid nominal
        {
            if ( getDB().IsValidNominal(value) )
            {
                this.itsValue = (new String(value));
            }
            else
            {
                throw new SystemErrorException(mName +
                                               "value not valid nominal");
            }
        }
        else // must lookup formal argument, an validate against it
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.NOMINAL )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != NOMINAL");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof NominalFormalArg ) )
            {
                throw new SystemErrorException(mName +
                                       "itsFargID doesn't refer to a nominal");
            }

            nfa = (NominalFormalArg)dbe;

            if ( nfa.approved(value) )
            {
                itsValue = new String(value);
            }
            else // coerce to the undefined state
            {
                this.itsValue = null;
            }
        }

        if ( ( this.itsValue != null ) &&
             ( this.itsValue.length() >= 1 ) &&
             ( this.itsValue.charAt(0) == '?' ) )
        {
            this.queryVar = true;
        }
        else
        {
            this.queryVar = false;
        }

        return;

    } /* QuoteStringDataValue::setItsValue() */


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

    } /* NominalDataValue::toString() */


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

    public String toDBString()
    {
        if ( this.itsValue == null )
        {
            return ("(NominalDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(NominalDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* NominalDataValue::toDBString() */


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

    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::updateForFargChange(): ";

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

    } /* NominalDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if ant) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.
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

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof NominalFormalArg )
        {
            NominalFormalArg nfa = (NominalFormalArg)fa;

            this.subRange = nfa.getSubRange();

            if ( this.subRange )
            {
                if ( ( this.itsValue != null ) &&
                     ( this.itsValue.length() > 0 ) &&
                     ( ! ( nfa.approved(this.itsValue) ) ) )
                {
                    this.itsValue = null;
                }
            }

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

    } /* NominalDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *
     * Coercing to the nearest valid value doesn't doesn't have an obvious
     * meaning in the case of nominals, so in this case, if subrange is true
     * and value contains a valid nominal that is not in the permitted list
     * for the associaged formal argument, just return false.
     *
     * This method should never be passed an invalid nominal, so if it
     * ever receives one, it will throw a system error exception.
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
        final String mName = "NominalDataValue::coerceToRange(): ";
        DBElement dbe;
        NominalFormalArg nfa;

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }

        if ( ! this.getDB().IsValidNominal(value) )
        {
            throw new SystemErrorException(mName + "value isn't valid nominal");
        }

        if ( this.subRange )
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.NOMINAL )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != NOMINAL");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof NominalFormalArg ) )
            {
                throw new SystemErrorException(mName +
                                       "itsFargID doesn't refer to a nominal");
            }

            nfa = (NominalFormalArg)dbe;

            if ( nfa.approved(value) )
            {
                return (new String(value));
            }
            else // coerce to the undefined state
            {
                return null;
            }
        }

        return value;

    } /* NominalDataValue::coerceToRange() */


    /**
     * isQueryVar()
     *
     * Return true if the current value of the nominal is a valid MacSHAPA
     * style query variable name, and false otherwise.
     *
     *                                              JRM -- 10/20/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isQueryVar()

    {

        return this.queryVar;

    } /* NominalDataValue::isQueryVar() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of NominalDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed NominalDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static NominalDataValue Construct(Database db,
                                             String n)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::Construct(db, n)";
        NominalDataValue ndv = null;

        ndv = new NominalDataValue(db);

        ndv.setItsValue(n);

        return ndv;

    } /* NominalDataValue::Construct(db, n) */

    /** Seed value for generating hash codes. */
    private final static int SEED1 = 3;
    /** Seed value for generating hash codes. */
    private final static int SEED2 = 7;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += (this.queryVar ? 1 : 0) * SEED1;
        hash += (this.itsValue == null ? 0 : this.itsValue.hashCode()) * SEED2;

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
        NominalDataValue n = (NominalDataValue) obj;
        return super.equals(obj)
            && (n.queryVar == this.queryVar)
            && (itsValue == null ? n.itsValue == null
                                 : itsValue.equals(n.itsValue));
    }

} /* NominalDataValue */

