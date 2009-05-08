/*
 * IntFormalArg.java
 *
 * Created on January 26, 2007, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Class IntFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to integer.  Note that we implement the float type with a long, so at
 * first blush, it would make more sense to call this class "LongFormalArg".
 * However, for historical reasons, and because integer values are
 * referred to as "ints" in the user interface, it seems to make more sense
 * to use the "IntFormalArg" name.
 *
 *                                                      JRM -- 1/26/07
 *
 * @author mainzer
 */
public class IntFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any integer, or only by some integer that lies within
     *      the closed interval defined by the minVal and maxVal fields
     *      discussed below
     *
     * minVal:  Long integer field used to specify the minimum integer value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     *
     * maxVal:  Long integer field used to specify the maximum integer value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     */

    boolean subRange = false;
    long minVal = Long.MIN_VALUE;
    long maxVal = Long.MAX_VALUE;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * IntFormalArg()
     *
     * Constructors for integer typed formal arguments.
     *
     * Four versions of this constructor -- one that takes only a database
     * reference arguments, one that takes a database reference and the formal
     * argument name as a parameters, one that takes a database reference,
     * aformal argument name, a minimum value, and a maximum value as parameters,
     * and one that takes a reference to an instance of IntFormalArg and uses
     * it to create a copy.
     *
     *                                          JRM -- 1/25/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public IntFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.INTEGER;

    } /* IntFormalArg() -- one parameters */

    public IntFormalArg(Database db,
                        String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.INTEGER;

    } /* IntFormalArg() -- two parameters */

    public IntFormalArg(Database db,
                        String name,
                        long minVal,
                        long maxVal)
          throws SystemErrorException
    {
        super(db, name);

        final String mName = "IntFormalArg::IntFormalArg(): ";

        this.fargType = FArgType.INTEGER;

        if ( minVal >= maxVal )

        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < Long.MIN_VALUE ) || ( maxVal > Long.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }
    } /* IntFormalArg() -- three parameters */

    public IntFormalArg(IntFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = FArgType.INTEGER;

        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* IntFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * setRange()
     *
     * Set the range of legal values that this formal arguement can assume.
     *
     * If the new minVal and maxVal describe the full range of the underlying
     * type (i.e.  minVal == Long.MIN_VALUE and maxVal == Long.MAX_VALUE), set
     * subRange to false.
     *
     * Otherwise, set subRange to true, and set the new minVal and maxVal.
     *
     *                                          JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public void setRange(long minVal, long maxVal)
        throws SystemErrorException
    {
        final String mName = "IntFormalArg::setRange(): ";
        if ( minVal >= maxVal )

        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < Long.MIN_VALUE ) || ( maxVal > Long.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else if ( ( minVal == Long.MIN_VALUE ) && ( maxVal == Long.MAX_VALUE ) )
        {
            this.subRange = false;
            this.maxVal = Long.MAX_VALUE;
            this.minVal = Long.MIN_VALUE;
        }
        else
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }

        return;

    } /* IntFormalArg::setRange() */

    /**
     * getSubRange(), getMinVal(), and getMaxVal()
     *
     * Accessor routines used to obtain the current values of the subRange,
     * minVal, and maxVal fields.
     *                                          JRM -- 2/5/07
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

    public long getMinVal()
    {
        return minVal;
    }

    public long getMaxVal()
    {
        return maxVal;
    }


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of IntDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * IntDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        IntDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new IntDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new IntDataValue(this.getDB(), this.getID(),
                    ((IntDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof FloatDataValue )
        {
            retVal = new IntDataValue(this.getDB(), this.getID(),
                ((Double)(((FloatDataValue)salvage).getItsValue())).longValue());
        }
        else
        {
            retVal = new IntDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* IntDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of IntDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new IntDataValue(this.getDB(), this.getID());

     } /* IntFormalArg::constructEmptyArg() */


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

        return ("(IntFormalArg " + getID() + " " + getFargName() + " " +
                subRange + " " + minVal + " " + maxVal +")");

    } /* IntFormalArg::toDBString() */


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
        if ( ! Database.IsValidInt(obj) )
        {
            return false;
        }

        /* If we get this far, obj must be a long */

        if ( ( subRange ) && ( ( (Long)obj < minVal ) || ( (Long)obj > maxVal ) ) )
        {
            return false;
        }

        return true;

    } /* IntFormalArg::isValidValue() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += (new Boolean(subRange)).hashCode() * Constants.SEED2;
        hash += HashUtils.Long2H(minVal) * Constants.SEED3;
        hash += HashUtils.Long2H(maxVal) * Constants.SEED4;

        return hash;
    }

    /**
     * Compares this integer formal argument against a object.
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

        IntFormalArg ifa = (IntFormalArg) obj;
        return super.equals(obj) && subRange == ifa.subRange
               && minVal == ifa.minVal && maxVal == ifa.maxVal;
    }
} /* class IntFormalArg */
