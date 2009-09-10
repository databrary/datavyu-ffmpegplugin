package org.openshapa.db;

/**
 * Untyped formal argument in a matrix or predicate argument list.
 *
 * This is the old style MacSHAPA formal argument that can be replaced
 * with a value of integer, floating point, text, nominal, or predicate
 * type.
 */
public class UnTypedFormalArg
        extends FormalArgument
{

    /**
     * Constructor.
     *
     * @param db The database that this UnTypedFormalArg belongs too.
     *
     * @throws SystemErrorException If the supplied database is null.
     *
     * @date 2007/01/25
     */

    public UnTypedFormalArg(Database db)
        throws SystemErrorException
    {
        super(db);

        this.fargType = FArgType.UNTYPED;

    } /* UnTypedFormalArg() -- one argument */


    /**
     * Constructor.
     *
     * @param db The database that this UnTypedFormalArg belongs too.
     * @param name The name of the UnTypedFormalArg.
     *
     * @throws SystemErrorException If the supplied database is null.
     *
     * @date 2007/01/25
     */

    public UnTypedFormalArg(Database db,
                            String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.UNTYPED;

    } /* UnTypedFormalArg() -- two arguments */


    /**
     * Copy Constructor.
     *
     * @param fArg The UntypedFormalArg to copy.
     *
     * @throws SystemErrorException If the supplied UnTypedFormalArg is null or
     * if the parent database of UnTypedFormalArg is null.
     */

    public UnTypedFormalArg(UnTypedFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = FArgType.UNTYPED;

        // copy over fields -- none in this case.

    } /* UnTypedFormalArg() -- make copy */


    // constructArgWithSalvage()
    /**
     * Builds a instance of DataValue initizlized from salvage.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param salvage The data value to salvage.
     *
     * @return An instance of DataValue initialized from salvage if
     * possible, and an instance UndefinedDataValue initialized with the
     * formal argument name otherwise.
     *
     * @throws SystemErrorException If the Salvage is of unknown type.
     */

    @Override
    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        final String mName = "UnTypedFormalArg::constructArgWithSalvage(): ";
        DataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal =  new UndefinedDataValue(this.getDB(), this.getID(),
                                             this.getFargName());
        }
        else if ( salvage instanceof ColPredDataValue )
        {
            retVal = new ColPredDataValue(this.getDB(), this.getID(),
                    ((ColPredDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof FloatDataValue )
        {
            retVal = new FloatDataValue(this.getDB(), this.getID(),
                    ((FloatDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new IntDataValue(this.getDB(), this.getID(),
                    ((IntDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof NominalDataValue )
        {
            retVal = new NominalDataValue(this.getDB(), this.getID(),
                    ((NominalDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof PredDataValue )
        {
            retVal = new PredDataValue(this.getDB(), this.getID(),
                    ((PredDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof TextStringDataValue )
        {
            TextStringDataValue textDV = (TextStringDataValue)salvage;

            if ( this.getDB().IsValidQuoteString(textDV.getItsValue()) )
            {
                retVal = new QuoteStringDataValue(this.getDB(), this.getID(),
                                                  textDV.getItsValue());
            }
            else
            {
                // todo: Think of coercing the text string into a quote string
                //       instead of just discarding it.
                retVal =  new UndefinedDataValue(this.getDB(), this.getID(),
                                             this.getFargName());
            }
        }
        else if ( salvage instanceof TimeStampDataValue )
        {
            retVal = new TimeStampDataValue(this.getDB(), this.getID(),
                    ((TimeStampDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof QuoteStringDataValue )
        {
            retVal = new QuoteStringDataValue(this.getDB(), this.getID(),
                    ((QuoteStringDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof UndefinedDataValue )
        {
            retVal =  new UndefinedDataValue(this.getDB(), this.getID(),
                                             this.getFargName());
        }
        else
        {
            throw new SystemErrorException(mName + "salvage of unknown type");
        }

        return retVal;

    } /* UnTypedDataValue::constructArgWithSalvage(salvage) */


    // constructEmptyArg()
    /**
     * @return an instance of UndefinedDataValue initialized with the
     * formal argument name.
     */

    public DataValue constructEmptyArg()
        throws SystemErrorException
    {

        return new UndefinedDataValue(this.getDB(), this.getID(), this.getFargName());

    } /* UnTypedFormalArg::constructEmptyArg() */


    // toDBString()
    /**
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @return A database string representation of the UnTypedFormalArg.
     */

    public String toDBString() {

        return ("(UnTypedFormalArg " + getID() + " " + getFargName() + ")");

    } /* UnTypedFormalArg::toDBString() */


    // isValidValue()
    /**
     * Boolean method that returns true if the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     * Note that the method will not accept valid text strings -- these can
     * only be used to replace instance of class TextStringFormalArg.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param object to check if is valid value for this UnTypedFormalArg.
     *
     * @return true if the supplied argument can be used as a value for this
     * formal argument.
     *
     * @date 2007/02/05
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        String mName = "UnTypedFormalArg::isValidValue()";

        if ( ( Database.IsValidInt(obj) ) ||
             ( Database.IsValidFloat(obj) ) ||
             ( Database.IsValidNominal(obj) ) ||
             ( Database.IsValidQuoteString(obj) ) )
        {
            return true;
        }

        if ( Database.IsValidTimeStamp(obj) )
        {
            TimeStamp ts;

            ts = (TimeStamp)obj;

            if ( ts.getTPS() == this.getDB().getTicks() )
            {
                return true;
            }
        }

        if ( obj instanceof Predicate )
        {
            long pveID;
            DBElement dbe = null;
            Predicate pred = null;

            pred = (Predicate)obj;

            if ( pred.getDB() != this.getDB() )
            {
                return false;
            }

            pveID = pred.getPveID();

            if ( pveID != DBIndex.INVALID_ID )
            {
                // lookup the target pve.  Throw a system error if
                // the target pve doesn't exist.

                dbe = this.getDB().idx.getElement(pveID);

                if ( dbe == null )
                {
                    throw new SystemErrorException(mName +
                                                   "pveID has no referent");
                }

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                            "pveID doesn't refer to a predicate vocab element");
                }
            }

            return true;
        }

        return false;

    } /* UnTypedFormalArg::isValidValue() */

} /* class UnTypedFormalArg */
