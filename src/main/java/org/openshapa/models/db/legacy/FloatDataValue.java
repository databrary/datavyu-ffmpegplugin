/*
 * FloatDataValue.java
 *
 * Created on August 15, 2007, 10:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import java.text.DecimalFormat;
import org.openshapa.util.FloatUtils;

/**
 * An instance of FloatDataValue is used to store a floating point value
 * assigned to a formal argument.
 */
public final class FloatDataValue extends DataValue {
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      float data values unless otherwise specified.
     *
     * itsValue:   Double containing the value assigned to the formal argument.
     *
     * minVal:  If subRange is true, this field contains the minimum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the minVal
     *      of the associated instance of FloatFormalArg.
     *
     *      Note that this data value may be used to hold a floating point
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     *
     * maxVal:  If subRange is true, this field contains the maximum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the maxVal
     *      of the associated instance of FloatFormalArg.
     *
     *      Note that this data value may be used to hold a floating point
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     */

    /** default value for floats. */
    private final double ItsDefault = 0.0;

    /** the value assigned to the associated formal argument in this case. */
    private double itsValue = ItsDefault;

    /** the minimum value -- if subrange is true. */
    private double minVal = 0.0;

    /** the maximum value -- if subrange is true. */
    private double maxVal = 0.0;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * FloatDataValue()
     *
     * Constructor for instances of FloatDataValue.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID
     * as arguments, and attempts to set the itsFargID field of the data value
     * accordingly.  ItsValue is set to the default.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of FloatDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                               -- 8/16/07
     * @param db Database associated
     * @throws SystemErrorException if superclass throws
     */

    public FloatDataValue(Database db) throws SystemErrorException {

        super(db);

    } /* FloatDataValue::FloatDataValue(db) */

    public FloatDataValue(Database db,
                          long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* FloatDataValue::FloatDataValue(db, fargID) */

    public FloatDataValue(Database db,
                          long fargID,
                          double value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* FloatDataValue::FloatDataValue(db, fargID, value) */

    public FloatDataValue(FloatDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        this.itsValue  = dv.itsValue;
        this.minVal    = dv.minVal;
        this.maxVal    = dv.maxVal;

    } /* FloatDataValue::FloatDataValue(dv) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * Return the current value of the data value.
     *
     *                           -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public double getItsValue()
    {
        return this.itsValue;

    } /* FloatDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(double value)
    {
        if ( this.subRange )
        {
            if ( value > this.maxVal )
            {
                this.itsValue = this.maxVal;
            }
            else if ( value < this.minVal )
            {
                this.itsValue = this.minVal;
            }
            else
            {
                this.itsValue = value;
            }
        }
        else
        {
            this.itsValue = value;
        }

        this.valueSet();
        return;

    } /* FloatDataValue::setItsValue() */

    public void setItsValue(final String value) {
        this.setItsValue(new Double(value));
    }

    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return FloatUtils.closeEnough(itsValue, ItsDefault);
    }

    /** @return the max value. */
    public double getMaxVal() {
        return this.maxVal;
    }

    /** @return the min value. */
    public double getMinVal() {
        return this.minVal;
    }

    /** @return the default value. */
    public double getDefault() {
        return this.ItsDefault;
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
        DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);
        return ("" + formatter.format(itsValue));
    }

    public String toEscapedString()
    {
        DecimalFormat formatter = new DecimalFormat(Constants.FLOAT_FORMAT);
        return ("" + formatter.format(itsValue));
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
        return ("(FloatDataValue (id " + this.getID() +
                ") (itsFargID " + this.itsFargID +
                ") (itsFargType " + this.itsFargType +
                ") (itsCellID " + this.itsCellID +
                ") (itsValue " + this.itsValue +
                ") (subRange " + this.subRange +
                ") (minVal " + this.minVal +
                ") (maxVal " + this.maxVal + "))");
    }


    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB file style definition of itsValuet to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will (depending on the subclass) be one
     * instantiation of either <float_cell_value>, or <float> (as defined in
     * the grammar defining the MacSHAPA ODB file format) depending on
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
        final String mName = "FloatDataValue::toMODBFile()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        output.printf("%f ", this.itsValue);

        return;

    } /* FloatDataValue::toMODBFile() */



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
        final String mName = "FloatDataValue::updateForFargChange(): ";

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

    } /* FloatDataValue::updateForFargChange() */


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
        final String mName = "FloatDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof FloatFormalArg )
        {
            FloatFormalArg ffa = (FloatFormalArg)fa;

            if ( this.itsFargType != FormalArgument.FArgType.FLOAT )
            {
                throw new SystemErrorException(mName +
                        "Unexpected this.itsFargType(1).");
            }

            this.subRange = ffa.getSubRange();

            if ( this.subRange )
            {
                this.maxVal = ffa.getMaxVal();
                this.minVal = ffa.getMinVal();

                if ( minVal >= maxVal )
                {
                    throw new SystemErrorException(mName + "minVal >= maxVal");
                }

                if ( this.itsValue > this.maxVal )
                {
                    this.itsValue = this.maxVal;
                }
                else if ( this.itsValue < this.minVal )
                {
                    this.itsValue = this.minVal;
                }
            }
        }
        else if ( fa instanceof UnTypedFormalArg )
        {
            if ( this.itsFargType != FormalArgument.FArgType.UNTYPED )
            {
                throw new SystemErrorException(mName +
                        "Unexpected this.itsFargType(2).");
            }

            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* FloatDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *                                               -- 070815
     *
     * Changes:
     *
     *    - None.
     */
    public double coerceToRange(double value)
    {
        if ( this.subRange )
        {
            if ( value > this.maxVal )
            {
                return maxVal;
            }
            else if ( value < this.minVal )
            {
                return minVal;
            }
        }

        return value;

    } /* FloatDataValue::coerceToRange() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct instance of FloatDataValue with the specified initialization.
     *
     * Return reference to the newly constructed FloatDataValue if successful.
     * Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static FloatDataValue Construct(Database db,
                                           double f)
        throws SystemErrorException
    {
        final String mName = "FloadDataValue::Construct(db, f)";
        FloatDataValue fdv = null;

        fdv = new FloatDataValue(db);

        fdv.setItsValue(f);

        return fdv;

    } /* FloatDataValue::Construct(db, f) */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += Math.floor(this.itsValue) * Constants.SEED1;
        hash += Math.floor(this.maxVal) * Constants.SEED2;
        hash += Math.floor(this.minVal) * Constants.SEED3;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }

    /**
     * Compares this FloatDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this FloatDataValue.
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
        FloatDataValue f = (FloatDataValue) obj;
        return FloatUtils.closeEnough(f.itsValue, this.itsValue)
            && FloatUtils.closeEnough(f.maxVal, this.maxVal)
            && FloatUtils.closeEnough(f.minVal, this.minVal)
            && super.equals(obj);
    }

} /* FloatDataValue */
