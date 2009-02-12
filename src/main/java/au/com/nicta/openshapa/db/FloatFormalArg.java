/*
 * FloatFormalArg.java
 *
 * Created on February 11, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class FloatFormalArg
 *
 * Intance of this class are used for formal argument which have been strongly
 * type to float.  Note that we implement the float type with a double, so at
 * first blush, it would make more sense to call this class "DoubleFormalArg".
 * However, for historical reasons, and because floating point values are
 * referred to as "floats" in the user interface, it seems to make more sense
 * to use the "FloatFormalArg" name.
 *
 *                                                      JRM -- 8/15/07
 *
 * @author mainzer
 */
public class FloatFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any double, or only by some double that lies within
     *      the closed interval defined by the minVal and maxVal fields
     *      discussed below
     *
     * minVal:  Double field used to specify the minimum floating point value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     *
     * maxVal:  Double field used to specify the maximum floating point value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     */

    boolean subRange = false;
    double minVal = (-1.0 * Double.MAX_VALUE);
    double maxVal = Double.MAX_VALUE;



    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * FloatFormalArg()
     *
     * Constructors for integer typed formal arguments.
     *
     * Four versions of this constructor -- one that takes only a database
     * reference as an argument, one that takes a database reference and the
     * formal argument name as parameters, one that takes a database referemce,
     * a formal argument name, minimum value, and maximum value as parameters,
     * and one that takes a reference to an instance of FloatFormalArg as its
     * parameter, and uses it to create a copy.
     *
     *                                          JRM -- 2/11/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public FloatFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.FLOAT;

    } /* FloatFormalArg() -- no parameters */

    public FloatFormalArg(Database db,
                          String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.FLOAT;

    } /* FloatFormalArg() -- one parameter */

    public FloatFormalArg(Database db,
                          String name,
                          double minVal,
                          double maxVal)
          throws SystemErrorException
    {
        super(db, name);

        final String mName = "FloatFormalArg::FloatFormalArg(): ";

        this.fargType = FArgType.FLOAT;

        if ( minVal >= maxVal )

        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < (-1.0 * Double.MAX_VALUE) ) ||
                  ( maxVal > Double.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else if ( ( minVal != Double.MIN_VALUE ) || ( maxVal != Double.MAX_VALUE ) )
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }
        else
        {
            this.subRange = false;
            this.maxVal = Double.MAX_VALUE;
            this.minVal = (-1.0 * Double.MAX_VALUE);
        }
   } /* FloatFormalArg() -- three parameters */

    public FloatFormalArg(FloatFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = FArgType.FLOAT;

        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* FloatFormalArg() -- make copy */



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
    public void setRange(double minVal, double maxVal)
        throws SystemErrorException
    {
        final String mName = "FloatFormalArg::setRange(): ";

        if ( minVal >= maxVal )
        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < (-1.0 * Double.MAX_VALUE) ) ||
                  ( maxVal > Double.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else if ( ( minVal == (-1.0 * Double.MAX_VALUE) ) &&
                  ( maxVal == Double.MAX_VALUE ) )
        {
            this.subRange = false;
            this.maxVal = Double.MAX_VALUE;
            this.minVal = -1.0 * Double.MAX_VALUE;
        }
        else
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }

        return;

    } /* FloatFormalArg::setRange() */

    /**
     * getSubRange(), getMinVal(), and getMaxVal()
     *
     * Accessor routines used to obtain the current values of the subRange,
     * minVal, and maxVal fields.
     *                                          JRM -- 2/11/07
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

    public double getMinVal()
    {
        return minVal;
    }

    public double getMaxVal()
    {
        return maxVal;
    }


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of FloatDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * FloatDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        FloatDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new FloatDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof FloatDataValue )
        {
            retVal = new FloatDataValue(this.getDB(), this.getID(),
                    ((FloatDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new FloatDataValue(this.getDB(), this.getID(),
                    (double)(((IntDataValue)salvage).getItsValue()));
        }
        else
        {
            retVal = new FloatDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* FloatDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of FloatDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new FloatDataValue(this.getDB(), this.getID());

     } /* FloatFormalArg::constructEmptyArg() */


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

        return ("(FloatFormalArg " + getID() + " " + getFargName() + " " +
                subRange + " " + minVal + " " + maxVal +")");

    } /* FloatFormalArg::toDBString() */


    /* isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean method that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                             JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        if ( ! Database.IsValidFloat(obj) )
        {
            return false;
        }

        /* If we get this far, obj must be a long */

        if ( ( subRange ) && ( ( (Double)obj < minVal ) || ( (Double)obj > maxVal ) ) )
        {
            return false;
        }

        return true;

    } /* FloatFormalArg::isValidValue() */

} /* class FloatFormalArg */
