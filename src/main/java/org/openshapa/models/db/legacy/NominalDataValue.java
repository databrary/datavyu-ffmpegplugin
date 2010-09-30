/*
 * NominalDataValue.java
 *
 * Created on August 17, 2007, 5:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;


/**
 * An instance of NominalDataValue is used to store a nominal value
 * assigned to a formal argument.
 */
public final class NominalDataValue extends DataValue {

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      nominal data values unless otherwise specified.
     *
     * itsValue:   String containing the value assigned to the formal argument.
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
     *                                               -- 8/16/07
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

    } /* NominlDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.  That is hard to do with nominals, so for the
     * nonce, we just set itsValue to null -- indicating that the nominal
     * data value is undefined.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None
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
            else if ( itsFargType != FormalArgument.FArgType.NOMINAL )
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

    } /* NominalDataValue::toString() */

    public String toEscapedString() {
        if (this.itsValue == null) {
            return "";
        } else {
            return StringUtils.escapeCSV(new String(this.itsValue));
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
     * toMODBFile()
     *
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will (depending on the subclass) be one
     * instantiation of either <nominal_cell_value>, or <nominal> (as defined
     * in the grammar defining the MacSHAPA ODB file format) depending on
     * context.
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
        final String mName = "NominalDataValue::toMODBFile()";
        DBElement dbe = null;
        FormalArgument farg = null;
        VocabElement ve = null;
        MatrixVocabElement mve = null;


        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( ( this.itsValue != null ) && ( this.itsValue.length() > 0 ) )
        {
            output.printf("|%s| ", this.itsValue);
        }
        else
        {
            // in the context of a MacSHAPA ODB file, this case should only
            // occur if the nominal data value is a value for a nominal
            // formal argument.  Further, since all martix and predicate
            // formal arguments are untyped in MacSHAPA, it follows that
            // this.itsPredID must be invalid, itsCellID and itsFargID must
            // be valid, and itsFargType must be NOMINAL.  
            //
            // However, we are getting external pressure to allow instances 
            // of MacshapaDatabase to allow them.  I've decided to go along
            // with this until the Openshapa database is up and running.
            //
            // Hence the following sanity check.

            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "itsFargID == INVALID_ID");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof FormalArgument ) )
            {
                throw new SystemErrorException(mName + "itsFargID doesn't " +
                                               "refer to a formal argument");
            }

            farg = (FormalArgument)dbe;

            // TODO: will have to expand the following to allow for query
            //       variables in the context of the query column.

            if ( ( farg.getFargType() != FormalArgument.FArgType.UNTYPED ) &&
                 ( farg.getFargType() != FormalArgument.FArgType.NOMINAL ) )
            {
                throw new SystemErrorException(mName + "Encountered empty " +
                        "argument / formal argument type mismatch");
            }

            dbe = this.getDB().idx.getElement(farg.getItsVocabElementID());

            if ( ! ( dbe instanceof VocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "farg.getItsVocabElementID() doesn't refer " +
                        "to a vocab element");
            }

            ve = (VocabElement)dbe;

            if ( this.itsPredID != DBIndex.INVALID_ID )
            {
                // the data value resides in either a predicate or column
                // predicate argument list.
                dbe = this.getDB().idx.getElement(this.itsPredID);

                if ( dbe instanceof Predicate )
                {
                    if ( ( ! this.getDB().typedFormalArgsSupported() ) &&
                         ( farg.getFargType() != FormalArgument.FArgType.UNTYPED ) )
                    {
                        throw new SystemErrorException(mName + "Encountered a " +
                                "typed formal argument in a predicate in a " +
                                "database in which does not support typed " +
                                "formal arguments in this context.");
                    }
                }
                else if ( dbe instanceof ColPred )
                {
                    // This case is tricky, as if the column predicate is 
                    // generated by either a float, integer, nominal, predicate
                    // or text MatrixVocabElement, all the arguments will be
                    // typed, and we must allow this regardless.
                    //
                    // If the column predicate is generated by a 
                    // MatrixVocabElement of type matrix, the first three
                    // arguments of the column predicate will be typed -- and
                    // again we must allow this regardless.  However, if
                    // db.typedFormalArgsSupported() returns false, we must
                    // throw a system error if any of the other formal arguments
                    // of the column predicate are typed.
                    if ( ! this.getDB().typedFormalArgsSupported() )
                    {
                        assert( ve instanceof MatrixVocabElement );

                        mve = (MatrixVocabElement)ve;

                        if ( ( mve.getType() == MatrixVocabElement.MatrixType.MATRIX )
                             &&
                             ( farg.getFargType() != FormalArgument.FArgType.UNTYPED )
                             &&
                             ( farg.getFargName().compareTo("<ord>") != 0 )
                             &&
                             ( farg.getFargName().compareTo("<onset>") != 0 )
                             &&
                             ( farg.getFargName().compareTo("<offset>") != 0 )
                           )
                        {
                            throw new SystemErrorException(mName +
                                    "Encountered a typed formal argument " +
                                    "other than <ord>, <onset>, or <offset> " +
                                    "in a column predicate implied by a " +
                                    "MatrixVocabElement of type matrix in a " +
                                    "database that does not support typed " +
                                    "arguments in this context.");
                        }
                    }
                }
                else
                {
                    throw new SystemErrorException(mName + "this.itsPredID " +
                            "refers to an object that is neither a predicate " +
                            "nor a column predicate.");
                }
            }
            else // the argument must appear in a matrix
            {
                if ( this.itsCellID == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + "Encountered " +
                            "datavalue with both invalid itsCellID and " +
                            "itsPredID");
                }

                if ( ! this.getDB().typedFormalArgsSupported() )
                {

                    assert( ve instanceof MatrixVocabElement );

                    mve = (MatrixVocabElement)ve;

                    if ( ( mve.getType() == MatrixVocabElement.MatrixType.MATRIX )
                         &&
                         ( farg.getFargType() != FormalArgument.FArgType.UNTYPED )
                       )
                    {
                        throw new SystemErrorException(mName + "Encountered " +
                                "typed formal argument in a MatrixVocabElement " +
                                "of type Matrix in a database that does not " +
                                "support typed arguments in this context.");
                    }
                }
                else if ( ( farg.getFargType() != FormalArgument.FArgType.NOMINAL )
                          &&
                          ( farg.getFargType() != FormalArgument.FArgType.UNTYPED )
                        )
                {
                    throw new SystemErrorException(mName +
                                                   "Encountered type mismatch.");
                }
            }

            output.printf("|%s| ", farg.getFargName());
        }

        return;

    } /* NominalDataValue::toMODBFile() */


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
     *                                           -- 8/16/07
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
     *                                               -- 07/08/18
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
            else if ( itsFargType != FormalArgument.FArgType.NOMINAL )
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
     *                                               -- 3/31/08
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

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED2;

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
            && (itsValue == null ? n.itsValue == null
                                 : itsValue.equals(n.itsValue));
    }

} /* NominalDataValue */

