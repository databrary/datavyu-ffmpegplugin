/*
 * MatrixVocabElement.java
 *
 * Created on December 14, 2006, 5:58 PM
 *
 */

package org.openshapa.models.db;

import java.util.Vector;


/**
 * Class MatrixVocabElement
 *
 * Instances of MatrixVocabElement are used to store vocabulary data on matrix
 * columns.  All matrix vocab elements are associated with a single data column
 * variable, and all data column have an associated unique matrix vocab
 * element.
 *
 * A major conceptual change in the OpenSHAPA database is the use of matricies
 * for all data columns.  Thus text, integer, float, nominal, and predicate
 * column variables are actualy single element fixed length matricies with
 * the single formal argument strongly typed to match the column type.  All
 * these matricies are system matricies that cannot be edited by the user
 * and therefore do not appear in the global vocab.
 *
 * The vocab element for matrix column variables remains editiable.
 *
 *                                          --  3/03/07
 */

public class MatrixVocabElement extends VocabElement
{

    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/

    /**
     * matrixType:  Enumerated type used to specify the type of the matrix.
     *      The UNDEFINED value is used to set the initial value of the type.
     *      All other values are associated with column types.
     */

    public enum MatrixType
        {UNDEFINED, TEXT, NOMINAL, INTEGER, FLOAT, PREDICATE, MATRIX};

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * type:    Type of the matrix, and of the assoicated column variable.
     *      The system flag must be set iff the type is not MATRIX.
     *
     * itsColID:   ID to the column variable with which this matrix
     *      vocab element is associated.
     *
     * cpfArgList:  Vector containing the formal argument list of the
     *      column predicate implied by the matrix vocab element and the
     *      associated DataColumn.
     *
     *      This argument list is simply a duplicate of the regular formal
     *      argument list, with <ord>, <onset>, and <offset> prepended.  At
     *      present, these arguments are instances  IntFormalArg,
     *      TimeStampFormalArg, and TimeStampFormalArg respectively -- but
     *      we may wish to change this in the future.
     *
     *      While the column predicate formal argument list must be visible
     *      to the outside world, there are no facilites for editing it --
     *      instead all edits to the regular formal argument list are mirrored
     *      in the column pred formal argument list.
     */

    /** type of the matrix vocab element and its associate column */
    MatrixType type = MatrixType.UNDEFINED;

    /** DataColumn which this matrix vocab element describes. */
    long itsColID = DBIndex.INVALID_ID;

    /** column predicate formal argument list */
    protected Vector<FormalArgument> cpfArgList =
            new Vector<FormalArgument>();


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * MatrixVocabElement()
     *
     * Constructor for instances of MatrixVocabElement.
     *
     * Two versions of this constructor -- one which sets the name of the
     * of the new matrix vocab element, and one which doesn't.  Note that
     * we check to verify that the name is a valid spreadsheet variable name.
     *
     *                                               -- 3/03/07
     *
     * Changes:
     *
     *    - Added copy constructor.                  -- 4/30/07
     *
     */

    public MatrixVocabElement(Database db)
        throws SystemErrorException
    {

        super(db);

        this.constructInitCPfArgList();

    } /* MatrixVocabElement::MatrixVocabElement(db) */


    public MatrixVocabElement(Database db,
                              String name)
        throws SystemErrorException
    {

        super(db);

        final String mName =
                "MatrixVocabElement::MatrixVocabElement(db, name): ";

        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }

        this.name = (new String(name));

        this.constructInitCPfArgList();

    } /* MatrixVocabElement::MatrixVocabElement(db, name) */


    public MatrixVocabElement(MatrixVocabElement ve)
        throws SystemErrorException
    {

        super(ve);

        final String mName = "MatrixVocabElement::MatrixVocabElement(ve): ";

        if (ve == null) {
            throw new SystemErrorException(mName + "bad ve");
        }

        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }

        /* The type must be set before the formal arguments can be copied
         * over, so there is code in the superclass constructor to do this
         * for instances of MatrixVocabElement.  However, our initialization
         * code will overwrite this, so we have to do it again here.
         */

        this.type = ve.type;

        this.itsColID = ve.itsColID;

        this.cpfArgList = ve.copyCPFormalArgList();

    } /* MatrixVocabElement::MatrixVocabElement(ve) */

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
            return new MatrixVocabElement(this);
        } catch (SystemErrorException e) {
            throw new CloneNotSupportedException(e.toString());
        }
    }

    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getType() and setType()
     *
     * Get and set routines for the type of the matrix vocab element.
     *
     * getType() can be called whenever, but setType() may only be called once,
     * and must set the type to something other than matrixType.UNDEFINED.
     *
     * Note that while we will not allow the type of an instance of
     * MatrixVocabEntry to be changed once it is set, we will have to allow
     * users to change the type of column variables.  We will need conversion
     * routines for this, and probably just replace the old column with a
     * new derived column.
     *
     * Changes:
     *
     *    - None.
     */

    public MatrixType getType()
    {
        return type;

    } /* MatrixVocabElement::getType() */

    public void setType(MatrixType newType)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::setType(): ";

        if ( type != MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type has already been set");
        }
        else if ( newType == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "newType is UNDEFINED");
        }

        type = newType;

        return;

    } /* MatrixVocabElement::setType() */


    /**
     * getItsColID() and setItsColID()
     *
     * Get and set routines for the itsColID field.
     *
     * getItsColID() can be called whenever, but setItsColID may only be
     * called once, and may not set itsColID to the invalid ID.
     *
     * Changes:
     *
     *    - None.
     */

    public long getItsColID()
    {
        return itsColID;

    } /* MatrixVocabElement::getItsColID() */

    public void setItsColID(long colID)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::setItsColID(): ";
        DBElement dbe;
        DataColumn dc;

        if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type is UNDEFINED");
        }

        if ( itsColID != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "itsColID already set");
        }
        else if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "colID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(colID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "colID has no referenct");
        }
        else if ( ! ( dbe instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName +
                    "colID doesn't refer to a DataColumn");
        }

        dc = (DataColumn)dbe;

        if ( this.name.compareTo(dc.getName()) != 0 )
        {
            throw new SystemErrorException(mName + "name mismatch");
        }

        if ( this.type != dc.getItsMveType() )
        {
            throw new SystemErrorException(mName + "type mismatch");
        }

        this.itsColID = colID;

        return;

    } /* MatrixVocabElement::setItsColID() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

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
    public String toDBString()
    {
        String s;

        try
        {
            s = "((MatrixVocabElement: ";
            s += getID();
            s += " ";
            s += getName();
            s += ") (system: ";
            s += system;
            s += ") (type: ";
            s += type;
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

    } /* MatrixVocabElement::toDBString() */


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

    } /* toString() */


    /*** accessor overrides ***/

    /* setName() -- Override of method in VocabElement
     *
     * Does some additional error checking and then set the name directly.
     * Do this as the superclass mthod will throw a system error if this is
     * a system MVE -- which it is unless this.type == MATRIX.
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
        final String mName = "MatrixVocabElement::setName(): ";

        if ( ( name == null ) ||
             ( ! ( name instanceof String ) ) ||
             ( name.length() <= 0 ) )
        {
            throw new SystemErrorException(mName + "Bad name param");
        }

        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "Bad name param");
        }

        this.name = (new String(name));

        return;

    } /* MatrixVocabElement::setName() */


    /**
     * setSystem() -- Override
     *
     * Do some additional error checking and then call the superclass version
     * of this method.
     *
     * Changes:
     *
     *   - None.
     */
    public void setSystem()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::setSystem(): ";

        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type undefined?!?!");
        }
//        /*** TODO:  Uncomment this once we have columns going ***/
//        else if ( itsColumn == null )
//        {
//            throw new SystemErrorException(mName + "itsColumn is null?!?!");
//        }
        else if ( this.name.compareTo("") == 0 )
        {
            throw new SystemErrorException(mName + "name is empty?!?!");
        }

        super.setSystem();

        return;

    } /* MatrixFormalArgument::setSystem() */


    /*** formal argument list management overrides ***/

    /**
     * appendFormalArg()
     *
     * Make FormalArgument::appendFormalArg() accessible to
     * the outside world, but add some error checking.
     *
     *                                           -- 3/04/07
     *
     * Changes:
     *
     *    - Modified function to append a copy of the newArg to the
     *      column predicate formal argument list.
     *
     *                                           -- 8/09/08
     */

    public void appendFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::appendFormalArg(): ";
        boolean typeMisMatch = false;

        if ( system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "must set type before adding arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( fArgList.size() != 0 ) )
        {
            throw new SystemErrorException(mName + "too many arguments.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg is null on entry");
        }

        switch ( this.type )
        {
            case FLOAT:
                if ( ! ( newArg instanceof FloatFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case INTEGER:
                if ( ! ( newArg instanceof IntFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case MATRIX:
                if ( newArg instanceof TextStringFormalArg )
                {
                    typeMisMatch = true;
                }
                break;

            case NOMINAL:
                if ( ! ( newArg instanceof NominalFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case PREDICATE:
                if ( ! ( newArg instanceof PredFormalArg ) )
                {
                    typeMisMatch = true;
                }

                break;

            case TEXT:
                if ( ! ( newArg instanceof TextStringFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
        }

        if ( typeMisMatch )
        {
            throw new SystemErrorException(mName + "type miss match.");
        }

        if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }

        super.appendFormalArg(newArg);

        this.appendCPFormalArg(newArg.CopyFormalArg(true, true));

        return;

    } /* MatrixVocabElement::appendFormalArg() */


    /**
     * deleteFormalArg()
     *
     * Make FormalArgument::deleteFormalArg() accessible to
     * the outside world, but add some error checking.
     *
     *                                           -- 3/4/07
     *
     * Changes:
     *
     *    - Updated to support maintenance of the column predicate
     *      formal argument list.
     *                                           -- 8/9/08
     */

    public void deleteFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::deleteFormalArg(): ";
        FormalArgument fa = null;
        FormalArgument cpfa = null;

        if ( this.system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "must set type before deleting arguments.");
        }
        else if ( this.fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( type != MatrixType.MATRIX )
        {
            throw new SystemErrorException(mName +
               "can't delete formal args from non matrix type vocab elements.");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= fArgList.size() )
        {
            throw new SystemErrorException(mName + "no nth formal argument");
        }
        else if ( (fArgList.size() + 3) != cpfArgList.size() )
        {
            throw new SystemErrorException(mName +
                    "unexpected cpfArgList.size()");
        }

        fa = this.getFormalArg(n);
        cpfa = this.getCPFormalArg(n + 3);

        if ( ! FormalArgument.FormalArgsAreEquivalent(fa, cpfa) )
        {
            System.out.printf("  fa = %s\n", fa.toDBString());
            System.out.printf("cpfa = %s\n", cpfa.toDBString());
            System.out.printf("  fArgList = %s\n", this.fArgListToDBString());
            System.out.printf("cpfArgList = %s\n", this.cpfArgListToDBString());

            throw new SystemErrorException(mName +
                    "target fArg & cpfArg aren't equivalent.");
        }

        super.deleteFormalArg(n);

        this.deleteCPFormalArg(n + 3);

        return;

    } /* MatrixVocabElement::deleteFormalArg() */


    /**
     * getFormalArg()
     *
     * Make FormalArgument::getFormalArg() accessible to
     * the outside world, but add some error checking.
     *
     *                                           -- 3/4/07
     *
     * Changes:
     *
     *   - None.
     */
    protected FormalArgument getFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getFormalArg(): ";

        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "must set type before getting arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) && ( n != 0 ) )
        {
            throw new SystemErrorException(mName +
                    "n must be 0 if type isn't MATRIX.");
        }

        return super.getFormalArg(n);

    } /*MatrixVocabElement::getFormalArg() */


    /**
     * getFormalArgCopy()
     *
     * Add some extry error checking to FormalArgument::getFormalArgCopy().
     *
     *                                           -- 9/15/09
     *
     * Changes:
     *
     *   - None.
     */
    public FormalArgument getFormalArgCopy(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getFormalArgCopy(): ";

        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "must set type before getting arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) && ( n != 0 ) )
        {
            throw new SystemErrorException(mName +
                    "n must be 0 if type isn't MATRIX.");
        }

        return super.getFormalArgCopy(n);

    } /* MatrixVocabElement::getFormalArgCopy() */


    /**
     * getNumFormalArgs()
     *
     * Make VocabElement::getNumFormalArgs() public with some additional
     * error checking.
     *
     *                                       3/04/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumFormalArgs()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getNumFormalArgs(): ";

        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "type must be defined before fArgList manipulations");
        }

        return super.getNumFormalArgs();

    } /* MatrixVocabElement::getNumFormalArgs() */


    /**
     * insertFormalArg()
     *
     *
     * Make VocabElement::insertFormalArg() public with some additional
     * error checking.
     *
     *                                           -- 3/04/07
     *
     * Changes:
     *
     *    - Updated function to insert a copy of the supplied formal argument
     *      in the column predicate formal argument list.
     */

    public void insertFormalArg(FormalArgument newArg,
                                int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::insertFormalArg(): ";
        boolean typeMisMatch = false;

        if ( system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName +
                    "must set type before inserting arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( fArgList.size() != 0 ) )
        {
            throw new SystemErrorException(mName + "too many arguments.");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( n != 0 ) )
        {
            throw new SystemErrorException(mName +
                    "insertion point must be 0.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry.");
        }
        else if ( ! this.cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName +
                    "newArg.getFargName() isn't unique.");
        }

        switch ( type )
        {
            case FLOAT:
                if ( ! ( newArg instanceof FloatFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case INTEGER:
                if ( ! ( newArg instanceof IntFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case NOMINAL:
                if ( ! ( newArg instanceof NominalFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case PREDICATE:
                if ( ! ( newArg instanceof PredFormalArg ) )
                {
                    typeMisMatch = true;
                }

                break;

            case TEXT:
                if ( ! ( newArg instanceof TextStringFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;

            case MATRIX:
                if ( newArg instanceof TextStringFormalArg )
                {
                    typeMisMatch = true;
                }
                break;
        }

        if ( typeMisMatch )
        {
            throw new SystemErrorException(mName + "type miss match.");
        }

        super.insertFormalArg(newArg, n);

        this.insertCPFormalArg(newArg.CopyFormalArg(true, true),
                                                            n + 3);

        return;

    } /* MatrixVocabElement::insertFormalArg() */


    /**
     * isWellFormed() -- Override
     *
     * Examine the vocab element and return true if it is well formed and
     * thus acceptable for insertion into the vocab list, and false if it
     * is not.
     *
     * This method is essentially a duplicate of the PredicateVocabElement
     * version with the addition of code that verifies that the type has
     * been set, and that the size and contents of the formal argument
     * list is congruent with the type.
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
        final String mName = "MatrixVocabElement::isWellFormed(): ";
        boolean wellFormed = true;
        int i;
        int j;
        FormalArgument fArg = null;
        FormalArgument scanfArg = null;
        VocabElement ve = null;
        MatrixVocabElement mve = null;

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
        else if ( ! Database.IsValidSVarName(this.getName()) )
        {
            wellFormed = false;
            throw new SystemErrorException(mName + "Invalid matrix name");
        }
        else
        {
            if ( ! newVE )
            {
                ve = this.getDB().vl.getVocabElement(this.getID());

                if ( ( ve == null ) ||
                     ( ! ( ve instanceof MatrixVocabElement ) ) )
                {
                    wellFormed = false;
                }
                else
                {
                    mve = (MatrixVocabElement)ve;

                    if ( mve.getType() != this.getType() )
                    {
                        wellFormed = false;
                    }
                }
            }
        }

        if ( wellFormed )
        {
            switch(this.getType())
            {
                case FLOAT:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            FloatFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case INTEGER:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            IntFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case NOMINAL:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            NominalFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case PREDICATE:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            PredFormalArg ) )
                    {
                        wellFormed = false;
                    }

                    break;

                case TEXT:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            TextStringFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case MATRIX:
                    if ( this.fArgList.size() < 1 )
                    {
                        wellFormed = false;
                    }
                    else
                    {
                        i = 0;
                        while ( ( i < this.fArgList.size() ) && ( wellFormed ) )
                        {
                            fArg = this.fArgList.get(i);

                            j = 0;
                            while ( ( j < this.fArgList.size() ) &&
                                    (  wellFormed ) )
                            {
                                if ( i != j )
                                {
                                    scanfArg = this.fArgList.get(j);

                                    if ( fArg.getFargName().
                                            compareTo(scanfArg.getFargName())
                                            == 0 )
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
                                        "Text String fArg in matrix");
                            }

                            i++;
                        }
                    }
                    break;

                default:
                    throw new SystemErrorException(mName +
                                                   "Unknown matrix ve type?!?");
            }
        }

        return wellFormed;

    } /* MatrixVocabElement::isWellFormed() */


    /**
     * This prepares the vocab element for removal from the database, when
     * deleting vocab elements, some types require the removal of additional
     * data (columns, cells, etc) to ensure that the database does not become
     * corrupted.
     *
     * @throws SystemErrorException If unable to prepare for removal.
     */
    public void prepareForRemoval() throws SystemErrorException {
        // Matrix vocab elements are strongly linked to matrix columns - we need
        // to delete cells and columns associated with this matrix value.
        DataColumn dc = this.getDB().getDataColumn(this.getItsColID());

        // Must remove cells from the data column before removing it.
        while (dc.getNumCells() > 0) {
            Cell c = this.getDB().getCell(dc.getID(), 1);
            this.getDB().removeCell(c.getID());
            dc = this.getDB().getDataColumn(this.getItsColID());
        }

        // All cells in the column removed - now delete the column.
        this.getDB().removeColumn(this.getItsColID());
    }


    /**
     * propagateID() -- Override
     *
     * Propagate the id assigned to the MatrixVocabElement to all current
     * formal arguments, if any.  This method should be called after the
     * MatrixVocabElement is assigned an ID and inserted into the vocab list.
     *
     *                                          -- 8/31/07
     *
     * Changes:
     *
     *   - None.
     */

    public void propagateID()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::propagateID(): ";
        int i;
        int numCPFArgs;
        FormalArgument fArg;

        super.propagateID();

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        numCPFArgs = cpfArgList.size();

        if ( numCPFArgs > 0 )
        {
            i = 0;

            while ( i <= (numCPFArgs - 1) )
            {
                fArg = getCPFormalArg(i);
                fArg.setItsVocabElementID(this.getID());
                i++;
            }
        }

        return;

    } /* MatrixVocabElement::propagateID() */


    /**
     * replaceFormalArg()
     *
     * Rewrite of the inherited method to deal with the peculiarities of
     * matrix vocab elements.
     *
     *                                           -- 3/04/07
     *
     * Changes:
     *
     *    - None.
     */

    public void replaceFormalArg(FormalArgument newArg,
                                 int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::replaceFormalArg()";
        boolean unique = true;
        FormalArgument oldArg;
        FormalArgument newCPArg;
        FormalArgument oldCPArg;

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }

        if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName +
                    "attempt to modify a system matrix vocab element.");
        }

        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry.");
        }

        if ( n < 0 )
        {
            throw new SystemErrorException(mName + "negative index supplied.");
        }

        if ( n >= this.fArgList.size() )
        {
            throw new SystemErrorException(mName + "no n-th formal argument.");
        }

        assert( (this.fArgList.size() + 3) == this.cpfArgList.size() );

        oldArg = this.getFormalArg(n);

        if ( ( this.type != MatrixType.MATRIX ) &&
             ( newArg.getFargType() != oldArg.getFargType() ) )
        {
            throw new SystemErrorException(mName + "In non matrix MVEs, " +
                    "formal arguments may only be replaced with formal " +
                    "arguments of the same type.");
        }

        if ( ( this.type == MatrixType.MATRIX ) &&
             ( newArg instanceof TextStringFormalArg ) )
        {
            throw new SystemErrorException(mName + "Text formal arguments, " +
                    "may not appear in MATRIX mve's.");
        }

        for ( FormalArgument t : this.fArgList )
        {
            if ( ( oldArg != t ) &&
                 ( newArg.getFargName().compareTo(t.getFargName()) == 0 ) )
            {
                unique = false;
            }
        }

        if ( ! unique )
        {
            throw new SystemErrorException(mName +
                    "new arg name not unique in fArgList");
        }

        oldCPArg = this.getCPFormalArg(n + 3);

        if ( ( oldArg.getID() == newArg.getID() ) &&
             ( oldArg.getID() != DBIndex.INVALID_ID ) )
        {
            if ( oldArg.getFargType() != newArg.getFargType() )
            {
                throw new SystemErrorException(mName + "Attempt to replace " +
                        "old fArg with new fArg with the same ID but of " +
                        "different type.");
            }

            newCPArg = newArg.CopyFormalArg(true, true);

            assert( oldCPArg.getFargType() == newCPArg.getFargType() );

            if ( oldCPArg.getID() != DBIndex.INVALID_ID )
            {
                newCPArg.setID(oldCPArg.getID());
            }
        }
        else
        {
            newCPArg = newArg.CopyFormalArg(true, true);
        }

        for ( FormalArgument t : this.cpfArgList )
        {
            if ( ( oldCPArg != t ) &&
                 ( newArg.getFargName().compareTo(t.getFargName()) == 0 ) )
            {
                unique = false;
            }
        }

        if ( ! unique )
        {
            throw new SystemErrorException(mName +
                    "new arg name not unique in cpfArgList");
        }

        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        newCPArg.setItsVocabElement(this);
        newCPArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        if ( this.fArgList.set(n, newArg) != oldArg )
        {
            throw new SystemErrorException(mName + "arg replace failed.");
        }

        if ( this.cpfArgList.set(n + 3, newCPArg) != oldCPArg )
        {
            throw new SystemErrorException(mName + "cp arg replace failed.");
        }

        return;

    } /* MatrixVocabElement::replaceFormalArg() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * appendCPFormalArg()
     *
     * Append the supplied formal argument to the end of the column predicate
     * formal argument list.
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private void appendCPFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
        final String mName = "VocabElement::appendCPFormalArg(): ";

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }

        if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }

        if ( newArg == null )
        {
            throw new SystemErrorException(mName +
                    "Attempt to insert null formal argument");
        }
        else if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }

        cpfArgList.add(newArg);

        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        return;

    } /* VocabElement::appendCPFormalArg() */


    /**
     * constructInitCPfArgList()
     *
     * Construct the formal argument list for the column predicate implied
     * by the MatrixVocabElement.  This method is called at construction time
     * only, and should not be called otherwise.
     *
     * Note that the method presumes that this.db has been initialized prior
     * to the method's invocation.
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */

    private void constructInitCPfArgList()
        throws SystemErrorException
    {
        final String mName =
                "MatrixVocabElement::constructInitCPfArgList(): ";
        FormalArgument fa = null;

        if ( this.getDB() == null )
        {
            throw new SystemErrorException(mName + "this.db null on entry.");
        }

        if ( this.cpfArgList == null )
        {
            throw new SystemErrorException(mName +
                    "this.colPredFormalArgList == null?!?");
        }

        if ( this.cpfArgList.size() != 0 )
        {
            throw new SystemErrorException(mName +
                    "this.colPredFormalArgList.size() != 0?!?");
        }

        fa = new IntFormalArg(this.getDB(), "<ord>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);

        fa = new TimeStampFormalArg(this.getDB(), "<onset>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);

        fa = new TimeStampFormalArg(this.getDB(), "<offset>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);

        if ( this.cpfArgList.size() != 3 )
        {
            throw new SystemErrorException(mName +
                    "this.colPredFormalArgList.size() != 3");
        }

        return;

    } /* "MatrixVocabElement::constructInitCPfArgList() */


    /**
     * cpfArgListIsValid()
     *
     * Verify that the column predicate formal argument list is valid.  Return
     * true if it is, and false otherwise.
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private boolean cpfArgListIsValid()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListIsValid()";
        boolean valid = true;
        int i;
        FormalArgument cpfa = null;
        FormalArgument fa = null;

        if ( ( this.cpfArgList.size() < 4 ) ||
             ( this.cpfArgList.size() != ( this.fArgList.size() + 3 ) ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(0);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof IntFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<ord>") != 0 ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(1);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof TimeStampFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<onset>") != 0 ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(2);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof TimeStampFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<offset>") != 0 ) )
        {
            return false;
        }

        i = 3;

        while ( i < cpfArgList.size() )
        {
            fa = fArgList.get(i - 3);
            cpfa = cpfArgList.get(i);

            if ( ( fa == null ) || ( cpfa == null ) )
            {
                return false;
            }
            else if ( ! FormalArgument.FormalArgsAreEquivalent(fa, cpfa) )
            {
                return false;
            }

            i++;
        }

        return true;

    } /* MatrixVocabElement::cpfArgListIsValid() */


    /**
     * cpfArgListToDBString()
     *
     * Construct a string containing the names of the formal arguments in
     * the column predicate implied by this MatrixVocabElement in a
     * format that displays the full status of the formal arguments and
     * facilitates debugging.
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private String cpfArgListToDBString()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListToDBString(): ";
        int i = 0;
        int numCPFArgs = 0;
        String s = new String("(");

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName +
                    "cpfArgList unitialized?!?!");
        }

        numCPFArgs = cpfArgList.size();

        if ( numCPFArgs < 3 )
        {
            throw new SystemErrorException(mName + "numCPFArgs < 3");
        }

        if ( numCPFArgs > 0 )
        {
            while ( i < (numCPFArgs - 1) )
            {
                s += getCPFormalArg(i).toDBString() + ", ";
                i++;
            }
            s += getCPFormalArg(i).toDBString();
        }

        s += ")";

        return s;

    } /* MatrixVocabElement::cpfArgListToDBString() */


    /**
     * cpfArgListToString()
     *
     * Construct a string containing the names of the formal arguments of the
     * column predicate implied by the MatrixVocabElement in the
     * format: (<ord>, <onset>, <offset>, <arg0>, <arg1>, ... <argn>).
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private String cpfArgListToString()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListToString(): ";
        int i = 0;
        int numCPFArgs = 0;
        String s = new String("(");

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }

        numCPFArgs = cpfArgList.size();

        if ( numCPFArgs < 3 )
        {
            throw new SystemErrorException(mName + "numCPFArgs < 3");
        }

        if ( numCPFArgs > 0 )
        {
            while ( i < (numCPFArgs - 1) )
            {
                s += getCPFormalArg(i).toString() + ", ";
                i++;
            }
            s += getCPFormalArg(i).toString();
        }

        s += ")";

        return s;

    } /* MatrixVocabElement::cpfArgListToString() */


    /**
     * cpfArgNameIsUnique()
     *
     * Scan the column predicate formal argument list, and test to see if the
     * supplied formal argument list is unique.  Return true if it is, and
     * false otherwise.
     *
     *                                       -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */

    private boolean cpfArgNameIsUnique(String fArgName)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgNameIsUnique(): ";
        boolean unique = true;

        if ( fArgName == null )
        {
            throw new SystemErrorException(mName + "fArgName null on entry.");
        }
        else if ( ! Database.IsValidFargName(fArgName) )
        {
            throw new SystemErrorException(mName +
                    "fArgName not a valid formal arg name.");
        }
        else if ( this.cpfArgList == null )
        {
            throw new SystemErrorException(mName + "this.cpfArgList null??");
        }

        for ( FormalArgument t : this.cpfArgList )
        {
            if ( fArgName.compareTo(t.getFargName()) == 0 )
            {
                unique = false;
            }
        }

        return unique;

    } /* MatrixVocabElement::cpfArgNameIsUnique() */


    /**
     * copyCPFormalArg()
     *
     * Returns a copy of the n-th column predicate formal argument, or null
     * if there is no such argument.
     *                                           -- 8/10/08
     *
     * Changes:
     *
     *   - None.
     *
     */

    protected FormalArgument copyCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyCPFormalArg(): ";
        FormalArgument fArg = null;
        FormalArgument fArgCopy = null;

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }

        fArg = cpfArgList.get(n);

        fArgCopy = fArg.CopyFormalArg(false, false);

        if ( fArgCopy == null )
        {
            throw new SystemErrorException(mName + "fArgcopy is null");
        }

        return fArgCopy;

    } /* VocabElement::copyCPFormalArg() */


    /**
     * copyCPFormalArgList()
     *
     * Construct and return a vector containing a copy of the column predicate
     * formal argument list.
     *                                           -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected java.util.Vector<FormalArgument> copyCPFormalArgList()
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyCPFormalArgList()";
        int i;
        java.util.Vector<FormalArgument> copy =
                new java.util.Vector<FormalArgument>();

        if ( this.cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }

        for ( i = 0; i < this.cpfArgList.size(); i++)
        {
            copy.add(this.copyCPFormalArg(i));
        }

        return copy;

    } /* VocabElement::copyCPFormalArgList() */


    /**
     * deleteCPFormalArg()
     *
     * Delete the n-th formal argument from the column predicate formal
     * argument list.  Throw a system error exception if there is no n-th
     * formal argument.
     *
     *                                           -- 8/09/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private void deleteCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::deleteCPFormalArg(): ";
        FormalArgument deletedArg = null;

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName +
                    "cpfArgList unitialized?!?!");
        }
        else if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            throw new SystemErrorException(mName + "no nth formal argument");
        }

        deletedArg = cpfArgList.remove(n);

        if ( deletedArg == null )
        {
            throw new SystemErrorException(mName + "deleted arg is null");
        }

        deletedArg.setItsVocabElement(null);
        deletedArg.setItsVocabElementID(DBIndex.INVALID_ID);

        return;

    } /* MatrixVocabElement::deleteCPFormalArg() */


    /**
     * getCPFormalArg()
     *
     * Returns a reference to the n-th formal argument in the column predicate
     * implied by the MatrixVoacabElement, or null if there is no such argument.
     *
     * N.B. The formal argument whose referene is returned is the formal
     *      argument that appears in the column predicate formal argument
     *      list.  Thus the caller must be careful not to alter it.
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     */

    protected FormalArgument getCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getCPFormalArg(): ";
        FormalArgument cpfArg = null;

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }

        cpfArg = cpfArgList.get(n);

        if ( cpfArg == null )
        {
            throw new SystemErrorException(mName + "cpfArg is null?!?");
        }

        if ( ! ( ( cpfArg instanceof UnTypedFormalArg ) ||
                 ( cpfArg instanceof ColPredFormalArg ) ||
                 ( cpfArg instanceof IntFormalArg ) ||
                 ( cpfArg instanceof FloatFormalArg ) ||
                 ( cpfArg instanceof TimeStampFormalArg ) ||
                 ( cpfArg instanceof QuoteStringFormalArg ) ||
                 ( cpfArg instanceof TextStringFormalArg ) ||
                 ( cpfArg instanceof NominalFormalArg ) ||
                 ( cpfArg instanceof PredFormalArg ) ) )
        {
            throw new SystemErrorException(mName + "cpfArg of unknown type");
        }

        return cpfArg;

    } /* MatrixVocabElement::getCPFormalArg() */


    /**
     * getNumCPFormalArgs()
     *
     * Return the number of formal arguments in the column predicate implied
     * by the MatrixVocabElement.
     *
     *                                       8/9/08
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumCPFormalArgs()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getNumCPFormalArgs(): ";
        int cpfArgListLen;
        int fArgListLen;

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName +
                    "cpfArgList unitialized?!?!");
        }

        cpfArgListLen = this.cpfArgList.size();
        fArgListLen = this.fArgList.size();

        if ( cpfArgListLen != ( fArgListLen + 3 ) )
        {
            throw new SystemErrorException(mName +
                    "cpfArgListLen != ( fArgListLen + 3 )!?!");
        }

        return cpfArgList.size();

    } /* MatrixVocabElement::getNumCPFormalArgs() */


    /**
     * getNumElements()
     *
     * Gets the number of elements in the Matrix
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumElements()
        throws SystemErrorException
    {
        return (this.getNumFormalArgs());

    } /* MatrixFormalArgument::getNumElements() */


    /**
     * insertCPFormalArg()
     *
     * Insert the supplied formal argument in the n-th position in the
     * column predicate formal argument list.  If n is not zero, there must
     * be at least n-1 formal arguments in the list to begin with.  Any
     * existing arguments with index greater than or equal to n have their
     * indicies increased by 1.
     *
     *                                           -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private void insertCPFormalArg(FormalArgument newArg,
                                     int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::insertCPFormalArg(): ";

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( system )
        {
            /* attempt to insert an argument in a system (and thus read only)
             * vocab element.
             */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName +
                    "Attempt to insert null formal argument");
        }
        else if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }
        else  if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n > cpfArgList.size() )
        {
            /* n-1-th formal argument doesn't exist -- scream and die */
            throw new SystemErrorException(mName + "n > cp arg list len");
        }

        cpfArgList.insertElementAt(newArg, n);

        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        return;

    } /* MatrixVocabElement::insertCPFormalArg() */


// delete this eventually -- 
//    /**
//     * replaceCPFormalArg()
//     *
//     * If the n-th column predicate formal argument exists, replace it with
//     * the supplied formal argument.
//     *
//     * Throw a system error exception if there is no n-th formal column
//     * predicate argument to begin with.
//     *
//     *                                           -- 2/27/07
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     */
//
//    protected void replaceCPFormalArg(FormalArgument newCPArg,
//                                      int n)
//        throws SystemErrorException
//    {
//        final String mName = "MatrixVocabElement::replaceCPFormalArg()";
//
//        deleteCPFormalArg(n);
//        insertCPFormalArg(newCPArg, n);
//
//        return;
//
//    } /* MatrixVocabElement::replaceCPFormalArg() */


    /**
     * toCPDBString()
     *
     * Returns a String representation of the column predicate implied by
     * the instance of MatrixVocabElement for comparison against the
     * database's expected value.<br>
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
    public String toCPDBString()
    {
        String s;

        try
        {
            s = "((ColumnPredicate: ";
            s += getID();
            s += " ";
            s += getName();
            s += ") (system: ";
            s += system;
            s += ") (type: ";
            s += type;
            s += ") (varLen: ";
            s += varLen;
            s += ") (fArgList: ";
            s += cpfArgListToDBString();
            s += ")";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* MatrixVocabElement::toDBString() */


    /**
     * toCPString()
     *
     * Returns a String representation of the column predicate that is
     * implied by the instance of MatrixVocabElement.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toCPString()
    {
        String s;

        try
        {
            s = getName();
            s += cpfArgListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* toString() */

} /* class MatrixFormalArgument */
