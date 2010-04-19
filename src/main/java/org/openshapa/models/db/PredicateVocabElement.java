/*
 * PredicateVocabElement.java
 *
 * Created on December 14, 2006, 6:10 PM
 *
 */

package org.openshapa.models.db;

import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.MatrixVocabElement.MatrixType;

/**
 * Class PredicateVocabElement
 *
 * Instances of PredicateVocabElement are used to store vocab data on
 * predicates, both system and user defined.
 *
 *                                           -- 3/05/07
 */

public final class PredicateVocabElement extends VocabElement
{

    /**
     * PredicateVocabElement()
     *
     * Constructor for instances of PredicateVocabElement.
     *
     * Predicates always have names, so only one version of this constructor,
     * which sets the name of the new predicate vocab element.
     *
     *                                               -- 3/05/07
     *
     * Changes:
     *
     *    - Added copy constructor.                  -- 4/30/07
     *
     */

    public PredicateVocabElement(Database db,
                                 String name)
        throws SystemErrorException
    {

        super(db);

        final String mName =
                "PredicateVocabElement::PredicateVocabElement(db, name): ";

        if ( ! Database.IsValidPredName(name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }

        this.name = (new String(name));

    } /* PredicateVocabElement::PredicateVocabElement(db, name) */

    /**
     * Copy contructor.
     *
     * @param ve The predicate vocab element to copy.
     *
     * @throws SystemErrorException if unable to copy predicate vocab element.
     */
    public PredicateVocabElement(PredicateVocabElement ve)
        throws SystemErrorException
    {
        super(ve);

        final String mName =
                "PredicateVocabElement::PredicateVocabElement(ve): ";

        if (ve == null) {
            throw new SystemErrorException(mName + "bad ve");
        }

        if ( ! Database.IsValidPredName(ve.name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }

    } /* PredicateVocabElement::PredicateVocabElement(ve) */


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
        try {
            return new PredicateVocabElement(this);
        } catch (SystemErrorException e) {
            throw new CloneNotSupportedException(e.toString());
        }
    }

    /**
     * isWellFormed() -- override of abstract method in VocabElement
     *
     * Examine the predicate vocab element and return true if it is well formed
     * and thus acceptable for insertion into the vocab list, and false if it
     * is not.
     *
     * For predicate vocab elements, a vocab element is well formed if it
     * contains at least one formal argument, the ve name is non-empty and
     * valid, and each formal argument has a name that is unique within the
     * formal argument list.  The VocabElement code should enforce the latter
     * two, so we flag a system error if they do not obtain.
     *
     * If the vocab element is new (i.e. it is about to be inserted into the
     * vocab list), the name of the vocab element may not appear in the vocab
     * list.  If the vocab element is to replace an existing vocab element,
     * its id must appear in the vocab list.
     *
     *                                                   -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean isWellFormed(boolean newVE)
        throws SystemErrorException
    {
        final String mName = "PredicateVocabElement::isWellFormed(): ";
        boolean wellFormed = true;
        int i;
        int j;
        FormalArgument fArg = null;
        FormalArgument scanfArg = null;

        if ( this.getName().length() == 0 )
        {
            wellFormed = false;
        }
        else if ( this.getDB() == null )
        {
            wellFormed = false;
        }
        else if ( ( newVE ) && ( this.getDB().vl.inVocabList(this.getName()) ) )
        {
            wellFormed = false;
        }
        else if ( ( ! newVE ) &&
                  ( ( this.getID() == DBIndex.INVALID_ID) ||
                    ( ! this.getDB().vl.inVocabList(this.getID()) ) ) )
        {
            wellFormed = false;
        }
        else if ( this.fArgList.size() <= 0 )
        {
            wellFormed = false;
        }
        else if ( ! Database.IsValidPredName(this.getName()) )
        {
            wellFormed = false;
            throw new SystemErrorException(mName + "Invalid pred name");
        }
        else
        {
            i = 0;

            while ( ( i < this.fArgList.size() ) && (  wellFormed ) )
            {
                fArg = this.fArgList.get(i);

                j = 0;
                while ( ( j < this.fArgList.size() ) && (  wellFormed ) )
                {
                    if ( i != j )
                    {
                        scanfArg = this.fArgList.get(j);

                        if ( fArg.getFargName().
                                compareTo(scanfArg.getFargName()) == 0 )
                        {
                            wellFormed = false;
                            throw new SystemErrorException(mName +
                                    "non unique fArg name");
                        }
                    }

                    j++;
                }

                if ( fArg instanceof TextStringFormalArg )
                {
                    wellFormed = false;
                    throw new SystemErrorException(mName +
                            "pred contains text string fArg");
                }

                i++;
            }
        }

        return wellFormed;

    } /* PredicateVocabElement::isWellFormed() */


    /**
     * This prepares the vocab element for removal from the database, when
     * deleting vocab elements, some types require the removal of additional
     * data (columns, cells, etc) to ensure that the database does not become
     * corrupted.
     *
     * @throws SystemErrorException If unable to prepare for removal.
     */
    public void prepareForRemoval() throws SystemErrorException {
        // Nothing additional needs to be done for predicate vocab element. I.e.
        // We leave columns untouched. The data for predicate vocab elements are
        // handled with InternalVocabElementListeners.
    }


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
    @Override
    public String toDBString()
        throws SystemErrorException
    {
        String s;

        try
        {
            s = "((PredicateVocabElement: ";
            s += getID();
            s += " ";
            s += getName();
            s += ") (system: ";
            s += system;
            s += ") (varLen: ";
            s += varLen;
            s += ") (fArgList: ";
            s += fArgListToDBString();
            s += ")";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* PredicateVocabElement::toDBString() */


    /**
     * toString() -- Override of abstract method in DataValue
     *
     * Returns a String representation of the DBValue for display.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    @Override
    public String toString()
    {
        String s;

        try
        {
            s = getName();
            s += fArgListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* PredicateVocabElement::toString() */

    /* setName() -- Override of method in VocabElement
     *
     * Does some additional error checking and then calls the superclass
     * version of the method.
     *
     *                                               -- 3/04/07
     *
     * Changes:
     *
     *    - None.
     */
    @Override
    public void setName(String name)
        throws SystemErrorException
    {
        final String mName = "PredicateVocabElement::setName(): ";

        if ( ! Database.IsValidPredName(name) )
        {
            throw new SystemErrorException(mName + "Bad name param");
        }

        super.setName(name);

        return;

    } /* PredicateVocabElement::setName() */

    /**
     * getNumElements()
     *
     * Gets the number of elements in the Predicate
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumElements()
        throws SystemErrorException
    {
        return (this.getNumFormalArgs());

    } /* PredicateFormalArgument::getNumElements() */


    /**
     * toMODBFile_predDefs()
     *
     * Write the predicate vocab element to the supplied file in MacSHAPA ODB
     * file format.  The output of this method is the <pred_def> in the
     * grammar defining the MacSHAPA ODB file format.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     * Note that this method throws away a lot of information about each
     * predicate vocab element, as this data is not used in MacSHAPA.
     *
     *                                              12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(java.io.PrintStream output,
                              String newLine,
                              String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "PredicateVocabElement::toMODBFile()";
        int i;
        int numFArgs;
        FormalArgument farg = null;

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        if ( this.system )
        {
            throw new SystemErrorException(mName + "this is a system PVE!!!");
        }

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        output.printf("%s( |%s| ( ", indent, this.name);

        if ( this.varLen )
        {
            output.printf("( VARIABLE-LENGTH> TRUE ) ");
        }
        else
        {
            output.printf("( VARIABLE-LENGTH> FALSE ) ");
        }

        output.printf("( FORMAL-ARG-LIST> ( ");

        numFArgs = fArgList.size();

        i = 0;

        while ( i < numFArgs )
        {
            farg = this.getFormalArg(i);
            output.printf("|%s| ", farg.getFargName());
            i++;
        }

        output.printf(") ) ) )%s", newLine);

        return;

    } /* PredicateVocabElement::toMODBFile() */

} /* Class PredicateVocabElement */

