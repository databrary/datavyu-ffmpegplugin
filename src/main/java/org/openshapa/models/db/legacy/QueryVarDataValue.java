/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;

/**
 * Class QueryVarDataValue
 *
 * An instance of QueryVarDataValue is used to store a query variable.
 *
 * As of this writing, query variables may appear only in MacSHAPA databases,
 * although it is possible that we will choose to extend the feature to other
 * types of OpenSHAPA databases.
 *
 * They are used to construct queries in the old MacSHAPA query language,
 * and may be used to replace formal arguments of all types.  In the best of
 * all possible worlds, they would appear only in the query variable of a
 * MacSHAPA database.  However, given that the ultimate destination of a
 * data value is frequently not known at creation time, the may appear
 * throughout a MacSHAPA database.
 *
 * Structurally, query variables are identical to nominals, save that they
 * must always be defined, and that they must begin with a "?" and be at
 * least two characters long.
 */
public final class QueryVarDataValue extends DataValue {


    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      query var data values unless otherwise specified.
     *
     *      This is a bit of a mis-nomer, as query variables must always be
     *      defined -- and thus the default value should always be overwritten.
     *      the default value is included so we can have the usual selection
     *      of constructors that do not specify a value.  However, it this
     *      default value is ever inserted in the database, it will almost
     *      certainly not be what the user wants.
     *
     * itsValue:   String containing the name of the query variable assigned
     *      to the formal argument.
     *
     * minVal & maxVal don't appear in QueryVarDataValue as query variables
     *      may not have a subrange.
     *
     * Likewise, since all QueryVarDataValues must be defined, ItsDefault
     *      does not appear.
     */

    /** default value for nominals. */
    final String ItsDefault = "?default"; // an innocuous default query var

    /** the value assigned to the associated formal argument in this case. */
    String itsValue = ItsDefault;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * QueryVarDataValue()
     *
     * Constructor for instances of QueryVarDataValue.
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
     * The fourth takes a reference to an instance of NominalDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                               -- 11/28/09
     *
     * Changes:
     *
     *    - None.
     *
     */

    public QueryVarDataValue(Database db)
        throws SystemErrorException {

        super(db);

    } /* QueryVarDataValue::NominalDataValue(db) */

    public QueryVarDataValue(Database db,
                             long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* QueryVarDataValue::NominalDataValue(db, fargID) */

    public QueryVarDataValue(Database db,
                             long fargID,
                             String value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* QueryVarDataValue::QueryVarDataValue(db, fargID, value) */

    public QueryVarDataValue(QueryVarDataValue dv)
        throws SystemErrorException
    {

        super(dv);

        final String mName = "QueryVarDataValue::QueryVarDataValue(dv):";

        if ( dv.itsValue == null )
        {
            throw new SystemErrorException(mName + "dv.itsValue is null?!?");
        }
        else
        {
            this.itsValue = new String(dv.itsValue);
        }

    } /* QueryVarDataValue::QueryVarDataValue(dv) */

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
        QueryVarDataValue clone = (QueryVarDataValue) super.clone();
        try {
            clone = new QueryVarDataValue(this);
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
     *                           -- 11/28/09
     *
     * Changes:
     *
     *    - None.
     */

    public String getItsValue()
        throws SystemErrorException
    {
        final String mName = "QueryVarDataValue::getItsValue()";

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "this.itsValue is null?!?");
        }
        else
        {
            return (new String(this.itsValue));
        }

    } /* QueryVarDataValue::getItsValue() */


    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If the supplied value is invalid,
     * scream and die.
     *
     *                                               -- 11/28/09
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "QueryVarDataValue::setItsValue(): ";
        DBElement dbe;

        if ( ( value == null ) || ( value.length() < 2 ) )
        {
            throw new SystemErrorException(mName +
                                           "new value empty or too short");
        }
        else // Verify that value is a valid nominal
        {
            if ( getDB().IsValidQueryVar(value) )
            {
                this.itsValue = (new String(value));
            }
            else
            {
                throw new SystemErrorException(mName +
                                               "value not valid query variable");
            }
        }

        return;

    } /* QuoteStringDataValue::setItsValue() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    // isDefault()
    /**
     * @return true if the value equals the default value -- this should always
     *  be false, as query variables must always be defined.
     *
     * Believe this is going away shortly -- if not, perhaps we should add
     * a check for the pseudo-default value.
     *                                              11/28/09
     */
    @Override
    public boolean isDefault()
    {
        return false;
    }


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

    // TODO: Query variables must always be defined.  Thus we should throw a
    //       system error expception if this is not the case.  However, none
    //       of the other data value toString methods throw a system error at
    //       present -- hence neither does this.  That should be fixed.

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

    } /* QueryVarDataValue::toString() */


    // TODO: As per toString(), this method should throw a system error if
    //       this.itsValue is NULL.

    public String toEscapedString()
    {
        if ( this.itsValue == null )
        {
            return "";
        }
        else
        {
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

    // TODO: As per toString(), this method should throw a system error
    //       exception if this.itsValue is null.

    public String toDBString()
    {
        if ( this.itsValue == null )
        {
            return ("(QueryVarDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(QueryVarDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* QueryVarDataValue::toDBString() */


    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *     *
     *                                              11/28/09
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(java.io.PrintStream output)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "QueryVarDataValue::toMODBFile()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( ( this.itsValue == null ) ||
             ( this.itsValue.length() < 2 ) )
        {
            throw new SystemErrorException(mName + "this.itsValue invalid.");
        }

        output.printf("|%s| ", this.itsValue);

        return;

    } /* QueryVarDataValue::toMODBFile() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                           -- 11/28/09
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
        final String mName = "QueryVarDataValue::updateForFargChange(): ";

        if ( ( this.itsValue == null ) ||
             ( ! Database.IsValidQueryVar(this.itsValue) ) )
        {
            throw new SystemErrorException(mName +
                    "this.itsValue null or invalid on entry");
        }

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

    } /* QueryVarDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * While query variables can be substituted for formal arguments of all
     * types, they pay no attention to the subranging (if any) of the formal
     * argument -- hence this method is a no-op.
     *
     *                                           -- 11/28/09
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "QueryVarDataValue::updateSubRange(): ";

        if ( ( this.itsValue == null ) ||
             ( ! Database.IsValidQueryVar(this.itsValue) ) )
        {
            throw new SystemErrorException(mName +
                    "this.itsValue null or invalid on entry");
        }

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        return;

    } /* QueryVarDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /*** None ***/


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of QueryVarDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed QueryVarDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                               -- 11/28/09
     *
     * Changes:
     *
     *    - None.
     */

    public static QueryVarDataValue Construct(Database db,
                                             String n)
        throws SystemErrorException
    {
        final String mName = "QueryVarDataValue::Construct(db, n)";
        QueryVarDataValue ndv = null;

        if ( ! Database.IsValidQueryVar(n) )
        {
            throw new SystemErrorException(mName + "n not a valid query var");
        }

        ndv = new QueryVarDataValue(db);

        ndv.setItsValue(n);

        return ndv;

    } /* QueryVarDataValue::Construct(db, n) */

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
     * Compares this QueryVarDataValue against another object.
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
        QueryVarDataValue n = (QueryVarDataValue) obj;
        return super.equals(obj)
            && (itsValue == null ? n.itsValue == null
                                 : itsValue.equals(n.itsValue));
    }

} // class QueryVarDataValue
