/*
 * UnTypedFormalArg.java
 *
 * Untyped formal argument in a matrix or predicate argument list.
 *
 * This is the old style MacSHAPA formal argument that can be replaced
 * with a value of integer, floating point, text, nominal, or predicate
 * type.
 *
 * Created on January 25, 2007, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 *
 * @author mainzer
 */
public class UnTypedFormalArg
        extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    // None.


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * UnTypedFormalArg()
     *
     * Constructors for un-typed formal arguments.
     *
     * Three versions of this constructor -- one that takes a formal
     * argument name, one that doesn't, and one that accepts another
     * instance of UnTypedFormalArg and creates a copy.
     *
     * Changes:
     *
     *    - None.
     *
     *                                          JRM -- 1/25/07
     */

    public UnTypedFormalArg(Database db)
        throws SystemErrorException
    {
        super(db);

        this.fargType = FArgType.UNTYPED;

    } /* UnTypedFormalArg() -- one argument */

    public UnTypedFormalArg(Database db,
                            String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = FArgType.UNTYPED;

    } /* UnTypedFormalArg() -- two arguments */

    public UnTypedFormalArg(UnTypedFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "UnTypedFormalArg::UnTypedFormalArg(): ";

        this.fargType = FArgType.UNTYPED;

        // copy over fields -- none in this case.

    } /* UnTypedFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    // None.


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of DataValue initialized from salvage if
     * possible, and an instance UndefinedDataValue initialized with the
     * formal argument name otherwise.
     *
     * Changes:
     *
     *    - None.
     */

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


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of UndefinedDataValue initialized with the
     * formal argument name.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new UndefinedDataValue(this.getDB(), this.getID(), this.getFargName());

     } /* UnTypedFormalArg::constructEmptyArg() */


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

        return ("(UnTypedFormalArg " + getID() + " " + getFargName() + ")");

    } /* UnTypedFormalArg::toDBString() */


    /**
     * isValidValue() -- Overide abstract method in FormalArgument
     *
     * Boolean method that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     * Note that the method will not accept valid text strings -- these can
     * only be used to replace instance of class TextStringFormalArg.
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
