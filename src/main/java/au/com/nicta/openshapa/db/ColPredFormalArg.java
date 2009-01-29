/*
 * ColPredFormalArg.java
 *
 * Created on July 20, 2001, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class PredFormalArg
 *
 * Instances of this class are used for formal arguments that have been
 * strongly typed to column predicates.  Note that this class is quite similar
 * to the PredFormalArg class, differing mainly in the lack of any facility
 * for subranging, and in the fact that instances of it must be replaces with
 * column predicates (the predicate implied by columns), instead of regular
 * predicates.
 *
 * @author mainzer
 */
public class ColPredFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /*** None ***/




    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * ColPredFormalArg()
     *
     * Constructors for column predicate typed formal arguments.
     *
     * Three versions of this constructor -- one that takes only a database
     * referenece, one that takes a database reference and the formal argument
     * name as a parameters, and one that takes a reference to an instance of
     * ColPredFormalArg and uses it to create a copy.
     *
     *                                          JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    public ColPredFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = fArgType.COL_PREDICATE;

    } /* NominalFormalArg() -- no parameters */

    public ColPredFormalArg(Database db,
                            String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = fArgType.COL_PREDICATE;

    } /* NominalFormalArg() -- one parameter */

    public ColPredFormalArg(ColPredFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        final String mName = "ColPredFormalArg::ColPredFormalArg(): ";

        this.fargType = fArgType.COL_PREDICATE;

    } /* PredFormalArg() -- make copy */



    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /*** None ***/


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of ColPredDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * PredDataValue otherwise.
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        ColPredDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new ColPredDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof ColPredDataValue )
        {
            retVal = new ColPredDataValue(this.getDB(), this.getID(),
                    ((ColPredDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new ColPredDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* ColPredDataValue::constructArgWithSalvage(salvage) */


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of ColPredDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new ColPredDataValue(this.getDB(), this.getID());

     } /* PredFormalArg::constructEmptyArg() */


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
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString() {

        return ("(ColPredFormalArg " + getID() + " " + getFargName() + ")");

    } /* ColPredFormalArg::toDBString() */


    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean method that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::isValidValue(): ";
        ColPred cPred = null;

        if ( obj instanceof ColPred )
        {
            cPred = (ColPred)obj;

            if ( cPred.getDB() != this.getDB() )
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        return false;

    } /*  ColPredFormalArg::isValidValue() */

} /* class PredFormalArg */
