package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.Vector;

/**
 * Class Matrix
 *
 * Primitive class for matricies.  Instances of this class are used to store
 * the list of values assigned to a matrix in a DataCell in a database.
 * Since matricies must be defined in the vocab list before they can be
 * created, instances of this class are tightly bound to their host database,
 * Column, and associated MatricVocabElement.
 *
 *                                                   -- 8/19/07
 */
public class Matrix implements Cloneable {

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /* db:  Reference to the instance of DataBase within which this predicae
     *      is defined.
     *
     * mveID:   Long containing the ID of the MatrixVocabElement represented in
     *      this instance of Matrix, or INVALID_ID if the Matrix is undefined.
     *      Note that this value is assigned on construction, and should not
     *      change thereafter.
     *
     * argList: Vector of data values representing the arguments of the
     *      Matrix represented in this data value, or null if the Matrix
     *      is undefined.
     *
     * varLen:  Boolean flag indicating whether the argument list is of
     *      variable length.
     *
     */

    /** Database containing the Matrix. */
    private Database db = null;

    /** ID of the represented Matrix. */
    private long mveID = DBIndex.INVALID_ID;

    /** Argument list of the Matrix. */
    private Vector < DataValue > argList = null;

    /** Whether the Matrix has a variable length argument list. */
    private boolean varLen = false;



    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Matrix()
     *
     * Constructor for instances of Matrix.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database, and a MatrixVocabElement
     * ID, and constructs a representation of the specified Matrix with an
     * empty/undefined argument list.
     *
     * The second takes a reference to a database, a MatrixVocabElementID,
     * and a vector of DataValue specifying the values assigned to
     * each of the Matrix arguments, and then constructs an instance of
     * Matrix representing the specified Matrix with the indicated
     * values as its arguments.
     *
     * The third takes a reference to an instance of Matrix as an
     * argument, and uses it to create a copy.
     *
     * The fourth is the same as the third, save that it takes the additional
     * blindCopy parameter.  When this parameter is true, it copies the
     * matrix without reference to the underlying mve, or the pve's associated
     * with any predicate that may appear in the argument list.  This is
     * necessary when a mve or pve changes, and we need a copy of the data cell
     * to modify into conformance with the changes
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     * @param db Database associated with new matrix.
     * @param mveID ID of the MatrixVocabElement this matrix will use.
     * @throws SystemErrorException If anything breaks.
     */

    public Matrix(Database db, long mveID) throws SystemErrorException {
        super();

        final String mName = "Matrix::Matrix(db, matrixID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if (db == null) {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        if ( mveID != DBIndex.INVALID_ID )
        {
            dbe = this.db.idx.getElement(mveID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + "mveID has no referent");
            }

            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "mveID doesn't refer to a Matrix vocab element");
            }

            this.mveID = mveID;

            mve = (MatrixVocabElement)dbe;

            this.varLen = mve.getVarLen();

            this.argList = this.constructEmptyArgList(mve);
        }
    } /* Matrix::Matrix(db, mveID) */

    public Matrix(Database db,
                  long mveID,
                  java.util.Vector<DataValue> argList)
        throws SystemErrorException
    {
        super();

        final String mName = "Matrix::Matrix(db, mveID, argList): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if (db == null) {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else
        {
            dbe = this.db.idx.getElement(mveID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + "mveID has no referent");
            }

            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "mveID doesn't refer to a Matrix vocab element");
            }

            this.mveID = mveID;

            mve = (MatrixVocabElement)dbe;

            this.varLen = mve.getVarLen();

            this.copyArgList(argList);
        }
    } /* Matrix::Matrix(db, mveID, argList) */

    public Matrix(Matrix m)
        throws SystemErrorException
    {
        super();

        final String mName = "Matrix::Matrix(m): ";

        if ( m == null )
        {
            throw new SystemErrorException(mName + "m null on entry");
        }

        this.db     = m.db;
        this.mveID  = m.mveID;
        this.varLen = m.varLen;

        if ( m.argList == null )
        {
            this.argList = null;
        }
        else
        {
            this.copyArgList(m.argList);
        }

    } /* Matrix::Matrix(m) */

    protected Matrix(Matrix m,
                     boolean blindCopy)
        throws SystemErrorException
    {
        super();

        final String mName = "Matrix::Matrix(m, blindCopy): ";

        if ( m == null )
        {
            throw new SystemErrorException(mName + "m null on entry");
        }

        this.db     = m.db;
        this.mveID  = m.mveID;
        this.varLen = m.varLen;

        if ( m.argList == null )
        {
            this.argList = null;
        }
        else if ( blindCopy )
        {
            this.argList = this.blindCopyArgList(m.argList);
        }
        else
        {
            this.copyArgList(m.argList);
        }

    } /* Matrix::Matrix(m, blindCopy) */

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
        Matrix clone = (Matrix) super.clone();
        try {
            clone = new Matrix(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getDB()
     *
     * Return the current value of the db field.
     *
     *                           -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public Database getDB()
    {

        return this.db;

    } /* Predicate::getdb() */


    /**
     * getMveID()
     *
     * Return the current value of the mveID field.
     *
     *                           -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public long getMveID()
    {

        return this.mveID;

    } /* Matrix::getMveID() */


    /**
     * getVarLen()
     *
     * Return the current value of the varLen field.
     *
     *                           -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getVarLen()
    {

        return this.varLen;

    } /* Matrix::getVarLen() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * argListToDBString()
     *
     * Construct a string containing the values of the arguments in a
     * format that displays the full status of the arguments and
     * facilitates debugging.
     *                                           -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String argListToDBString()
        throws SystemErrorException
    {
        final String mName = "Matrix::argListToDBString(): ";
        int i = 0;
        int numArgs = 0;
        String s;

        if ( this.argList == null )
        {
            s = "(argList ())";
        }
        else
        {
            s = new String("(argList (");

            if ( this.argList == null )
            {
                /* fArgList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            while ( i < (numArgs - 1) )
            {
                s += this.getArg(i).toDBString() + ", ";
                i++;
            }

            s += this.getArg(i).toDBString();

            s += "))";
        }

        return s;

    } /* Matrix::argListToDBString() */


    /**
     * argListToString()
     *
     * Construct a string containing the values of the arguments in the
     * format: (value0, value1, ... value).
     *                                           -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String argListToString()
        throws SystemErrorException
    {
        final String mName = "Matrix::argListToString(): ";
        int i = 0;
        int numArgs = 0;
        String s = "";

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            while ( i < (numArgs - 1) )
            {
                s += this.getArg(i).toString() + ", ";
                i++;
            }

            s += getArg(i).toString();
        }

        return s;

    } /* Matrix::argListToString() */


    /**
     * insertInIndex()
     *
     * This method is called when the DataCell whose value is stored in this
     * instance ov Matrix if first inserted in the database and becomes the
     * first cannonical version of the DataCell.
     *
     * The method passes the cell's ID down to the instance(s) of DataValue
     * that stores the value of the Matrix, which in turn pass that ID along
     * to any instances or Predicate that may appear in the Matrix.
     *
     * In addition, any DBElements in the matrix are instructed to insert
     * themselves in the index as appropriate.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "Matrix::insertInIndex(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( DCID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "DCID invalid?!?");
        }

        dbe = this.db.idx.getElement(DCID);

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "ID doesn't refer to a DataCell");
        }

        dc = (DataCell)dbe;

        if ( dc.getItsMveID() != this.mveID )
        {
            throw new SystemErrorException(mName +
                    "mveID mismatch with DataCell");
        }

        if ( this.argList == null )
        {
            throw new SystemErrorException(mName + "argList is null!?!");
        }

        for ( DataValue dv : this.argList )
        {
            dv.insertInIndex(DCID);
        }

        return;

} /* Matrix::insertInIndex(DCID) */


    /**
     * lookupMatrixVE()
     *
     * Given an ID, attempt to look up the associated MatrixVocabElement
     * in the database associated with the instance of Matrix.  Return a
     * reference to same.  If there is no such MatrixVocabElement, throw
     * a system error.
     *                                               -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private MatrixVocabElement lookupMatrixVE(long mveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::lookupMatrixVE(mveID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        dbe = this.db.idx.getElement(mveID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "mveID has no referent");
        }

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                    "mveID doesn't refer to a Matrix vocab element");
        }

        mve = (MatrixVocabElement)dbe;

        return mve;

    } /* Matrix::lookupMatrixVE(mveID) */


    /**
     * removeFromIndex()
     *
     * This method is called when the DataCell whose value is stored in this
     * instance ov Matrix is removed from the database, and all the DBElements
     * that make up its value must be removed from the index as well.
     *
     * Matrix is not a subclass of DBElement, so it has nothing to do beyond
     * sanity checking, and passing the remove from index message on to its
     * constituent DataValues.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "Matrix::removeFromIndex(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( DCID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "DCID invalid?!?");
        }

        dbe = this.db.idx.getElement(DCID);

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "ID doesn't refer to a DataCell");
        }

        dc = (DataCell)dbe;

        if ( dc.getItsMveID() != this.mveID )
        {
            throw new SystemErrorException(mName +
                    "mveID mismatch with DataCell");
        }

        if ( this.argList == null )
        {
            throw new SystemErrorException(mName + "argList is null!?!");
        }

        for ( DataValue dv : this.argList )
        {
            dv.removeFromIndex(DCID);
        }

        return;

} /* Matrix::removeFromIndex(DCID) */


   /**
     * toDBString()
     *
     * Returns a database String representation of the Matrix for comparison
     * against the expected value.<br>
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
            s = "(Matrix (mveID " + this.mveID +
                ") (varLen " + this.varLen + ") " +
                this.argListToDBString() + "))";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* Matrix::toDBString() */



    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB style definition of the matrix and its contests
     * to the supplied file in MacSHAPA ODB file format.  The output of this
     * method is one instantiation of the <s_var_cell_value_attribute> (as
     * defined in the grammar defining the MacSHAPA ODB file format) for each
     * entry in the matrix.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     * Note that this method throws away a lot of information about each
     * data column, as this data is not used in MacSHAPA.
     *
     *                                              JRM -- 12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(MatrixVocabElement mve,
                              java.io.PrintStream output,
                              String newLine,
                              String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "matrix::toMODBFile()";
        int i = 0;
        int numArgs;
        FormalArgument farg;
        DataValue arg;

        if ( ( mve == null ) ||
             ( mve.getID() != this.mveID ) )
        {
            throw new SystemErrorException(mName + "bad mve on entry");
        }

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

        if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }

        numArgs = this.argList.size();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        while ( i < numArgs )
        {
            farg = mve.getFormalArg(i);
            arg = this.getArg(i);

            output.printf("%s( |%s| ", indent, farg.getFargName());

            arg.toMODBFile(output);

            output.printf(")%s", newLine);

            i++;
        }

        return;

    } /* Matrix::toMODBFile() */


    /**
     * toString()
     *
     * Returns a String representation of the Matrix for display.
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
            s = "(";
            s += this.argListToString();
            s += ")";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* Matrix::toString() */

    /*************************************************************************/
    /********************* Argument List Management: *************************/
    /*************************************************************************/


    /**
     * copyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * Matrix indicated by the current value of mveID, copy the argument
     * list without attempting any sanity checks against the mve, or against
     * the pve's associated with any predicates that may appear in the
     * argument list.
     *
     * This is necessary if the definition of the mve or a pve has changed,
     * and we need a copy of the matrix to modify into accordance with the new
     * version.
     *
     * Throw a system error if any errors aredetected.  Otherwise, return the
     * copy.
     *
     *                                               -- 4/6/08
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> blindCopyArgList(Vector<DataValue> srcArgList)
        throws SystemErrorException
    {
        final String mName = "Matrix::blindCopyArgList(pve): ";
        int i;
        int numArgs;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        DataValue dv;
        DataValue cdv = null;

        if ( srcArgList == null )
        {
            throw new SystemErrorException(mName + "srcArgList null on entry");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID undefined");
        }

        numArgs = srcArgList.size();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
           // get the i'th argument from the argument list.  Again, this
            // is the actual argument -- must be careful not to modify it
            // in any way.
            dv = srcArgList.get(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th source argument?!?!");
            }

            try {
                cdv = (DataValue) dv.blindClone();
            } catch (CloneNotSupportedException e) {
                throw new SystemErrorException("Unable to clone DataValue.");
            }

            newArgList.add(cdv);
        }

        if ( newArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        return newArgList;

    } /* Matrix::blindCopyArgList(srcArgList) */


    /**
     * constructEmptyArgList()
     *
     * Given a reverence to a MatrixVocabElement, construct an empty
     * argument list as directed by the formal argument list of the supplied
     * MatrixVocabElement.
     *
     * Return the newly constructed argument list.
     *
     *                                               -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> constructEmptyArgList(MatrixVocabElement mve)
        throws SystemErrorException
    {
        final String mName = "Matrix::constructEmptyArgList(mve): ";
        int i;
        int numArgs;
        Vector<DataValue> argList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;

        if ( mve == null )
        {
            throw new SystemErrorException(mName + "mve == null");
        }

        numArgs = mve.getNumFormalArgs();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the Matrix.  Observe that
            // getFormaArg() returns a reference to the actual formal
            // argument in the MatrixVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = mve.getFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }

            dv = fa.constructEmptyArg();

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "dv == null?!?!");
            }

            argList.add(dv);
        }

        if ( argList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        return argList;

    } /* Matrix::constructEmptyArgList(mve) */


    /**
     * copyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * Matrix indicated by the current value of mveID, attempt to make a
     * copy of that argument list.  Throw a system error if any errors are
     * detected.  Otherwise, return the copy.
     *
     *                                               -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> copyArgList(Vector<DataValue> srcArgList)
        throws SystemErrorException
    {
        final String mName = "Matrix::copyArgList(srcArgList): ";
        int i;
        int numArgs;
        MatrixVocabElement mve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;
        DataValue cdv = null;

        if ( srcArgList == null )
        {
            throw new SystemErrorException(mName + "srcArgList null on entry");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID undefined");
        }

        mve = this.lookupMatrixVE(this.mveID);

        numArgs = mve.getNumFormalArgs();

        if ( srcArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "arg list size mis-match");
        }

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the Matrix.  Observe that
            // getFormaArg() returns a reference to the actual formal
            // argument in the MatrixVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = mve.getFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }

            // get the i'th argument from the argument list.  Again, this
            // is the actual argument -- must be careful not to modify it
            // in any way.
            dv = srcArgList.get(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th source argument?!?!");
            }

            switch (fa.getFargType())
            {
                case COL_PREDICATE:
                    if ( ! ( dv instanceof ColPredDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": col pred expected.");
                    }
                    cdv = new ColPredDataValue((ColPredDataValue)dv);
                    break;

                case FLOAT:
                    if ( ! ( dv instanceof FloatDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": float expected.");
                    }
                    cdv = new FloatDataValue((FloatDataValue)dv);
                    break;

                case INTEGER:
                    if ( ! ( dv instanceof IntDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": integer expected.");
                    }
                    cdv = new IntDataValue((IntDataValue)dv);
                    break;

                case NOMINAL:
                    if ( ! ( dv instanceof NominalDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": nominal expected.");
                    }
                    cdv = new NominalDataValue((NominalDataValue)dv);
                    break;

                case PREDICATE:
                    if ( ! ( dv instanceof PredDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": Predicate expected.");
                    }
                    cdv = new PredDataValue((PredDataValue)dv);
                    break;

                case TIME_STAMP:
                    if ( ! ( dv instanceof TimeStampDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": time stamp expected.");
                    }
                    cdv = new TimeStampDataValue((TimeStampDataValue)dv);
                    break;

                case QUOTE_STRING:
                    if ( ! ( dv instanceof QuoteStringDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": quote string expected.");
                    }
                    cdv = new QuoteStringDataValue((QuoteStringDataValue)dv);
                    break;

                case TEXT:
                    if ( ! ( dv instanceof TextStringDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": text string expected.");
                    }
                    cdv = new TextStringDataValue((TextStringDataValue)dv);
                    break;

                case UNTYPED:
                    if (dv.getClass().equals(TextStringDataValue.class)) {
                        throw new SystemErrorException(mName +
                                "Text String(s) can't be " +
                                "substituted for untyped arguments.");
                    }

                    try {
                        cdv = (DataValue) dv.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new SystemErrorException("Unable to clone dv.");
                    }

                    break;

                case UNDEFINED:
                    throw new SystemErrorException(mName +
                            "formal arg type undefined???");
                    /* break statement commented to keep the compiler happy */
                    // break;

                default:
                    throw new SystemErrorException(mName +
                                                   "Unknown Formal Arg Type");
                    /* break statement commented to keep the compiler happy */
                    // break;
            }

            if ( dv.getItsFargID() == DBIndex.INVALID_ID )
            {
                cdv.setItsFargID(fa.getID());
            }
            else if ( dv.getItsFargID() != fa.getID() )
            {
                throw new SystemErrorException(mName + "fargID mismatch");
            }

            newArgList.add(cdv);
        }

        if ( newArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        this.argList = newArgList;

        return newArgList;

    } /* Matrix::copyArgList(srcArgList) */


    /**
     * deregisterPreds()
     *
     * Call the deregister preds method on any instances of column predicate or
     * predicate data value that appear in the matrix.
     *
     * The objective is to get any instance of column predicate or predicate
     * that appears in the Matrix to deregister as internal (matrix) vocab
     * element listeners with its associated matrix or predicate vocab element.
     *
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - Modified method to work with column predicates as well.
     *
     *                                               -- 8/31/08
     */

    protected void deregisterPreds(boolean cascadeMveDel,
                                   long cascadeMveID,
                                   boolean cascadePveDel,
                                   long cascadePveID)
        throws SystemErrorException
    {
        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof ColPredDataValue )
            {
                ((ColPredDataValue)dv).deregisterPreds(cascadeMveDel,
                                                       cascadeMveID,
                                                       cascadePveDel,
                                                       cascadePveID);
            }
            else if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).deregisterPreds(cascadeMveDel,
                                                    cascadeMveID,
                                                    cascadePveDel,
                                                    cascadePveID);
            }
        }

        return;

    } /* Matrix::deregisterPreds() */


    /**
     * getArg()
     *
     * Return a reference to the n-th argument if it exists, or null if it
     * doesn't.
     *
     *                                       -- 8/23/07
     */

    protected DataValue getArg(int n)
        throws SystemErrorException
    {
        final String mName = "Matrix::getArg(): ";
        int numArgs;
        DataValue arg = null;

        if ( mveID == DBIndex.INVALID_ID )
        {
            arg = null;
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= argList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            arg = null;
        }
        else /* we have work to do */
        {
            arg = argList.get(n);

            if ( arg == null )
            {
                throw new SystemErrorException(mName + "arg is null?!?");
            }

            if ( ! ( ( arg instanceof FloatDataValue ) ||
                     ( arg instanceof FloatDataValue ) ||
                     ( arg instanceof IntDataValue ) ||
                     ( arg instanceof NominalDataValue ) ||
                     ( arg instanceof PredDataValue ) ||
                     ( arg instanceof TimeStampDataValue ) ||
                     ( arg instanceof QuoteStringDataValue ) ||
                     ( arg instanceof TextStringDataValue ) ||
                     ( arg instanceof UndefinedDataValue ) ) )
            {
                throw new SystemErrorException(mName + "arg of unknown type");
            }
        }

        return arg;

    } /* Matrix::getArg() */


    /**
     * getArgCopy()
     *
     * Return a reference to a copy of the n-th argument if it exists, or
     * null if it doesn't.
     *                                       -- 5/23/08
     *
     * Changes:
     *
     *    - None.
     */

    public DataValue getArgCopy(int n)
        throws SystemErrorException
    {
        final String mName = "Matrix::getArgCopy(): ";
        DataValue arg = null;
        DataValue argCopy = null;

        arg = this.getArg(n);

        if ( arg != null )
        {
            try {
                argCopy = (DataValue) arg.clone();
            } catch (CloneNotSupportedException e) {
                throw new SystemErrorException("Unable to clone DataValue");
            }
        }

        return argCopy;

    } /* Matrix::getArgCopy() */


    /**
     * getNumArgs()
     *
     * Return the number of arguments.  Return 0 if the predID hasn't been
     * specified yet.
     *                                       -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumArgs()
        throws SystemErrorException
    {
        final String mName = "Matrix::getNumArgs(): ";
        int numArgs = 0;

        if ( mveID != DBIndex.INVALID_ID )
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }
        }

        return numArgs;

    } /* Matrix::getNumArgs() */


    /**
     * registerPreds()
     *
     * Call the register preds method on any instances of column predicate or
     * predicate data value that appears in the matrix.
     *
     * The objective is to get any instance of column predicate or
     * predicate that appears in the Matrix to register as internal (matrix)
     * vocab element listeners with its associated matrix or predicate vocab
     * element.
     *
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - Modified to work with column predicates as well.
     *
     *                                               -- 8/31/08
     */

    protected void registerPreds()
        throws SystemErrorException
    {
        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof ColPredDataValue )
            {
                ((ColPredDataValue)dv).registerPreds();
            }
            else if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).registerPreds();
            }
        }

        return;

    } /* Matrix::registerPreds() */


    /**
     * replaceArg()
     *
     * Replace the argument specified by n with the supplied datavalue.  Throw
     * a system error if any errors are detected.
     *
     *                                               -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public void replaceArg(int n,
                           DataValue newArg)
        throws SystemErrorException
    {
        final String mName = "Matrix::replaceArg(n, newArg): ";
        int i;
        int numArgs;
        MatrixVocabElement mve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue oldArg = null;

        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry");
        }
        else if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= argList.size() )
        {
            /* n-th formal argument doesn't exist -- scream and die */
            throw new SystemErrorException(mName + n +
                    "th argument doesn't exist");
        }

        mve = this.lookupMatrixVE(this.mveID);

        // get the n'th formal argument of the Matrix.  Observe that
        // getFormaArg() returns a reference to the actual formal
        // argument in the MatrixVocabElement data structure, so we
        // must be careful not to modify it in any way, or expose the
        // reference to the user.
        fa = mve.getFormalArg(n);

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "no " + n +
                    "th formal argument?!?!");
        }
        else if ( ( fa instanceof TextStringFormalArg ) &&
                  ( mve.getType() !=
                    MatrixVocabElement.MatrixType.TEXT ) )
        {
            throw new SystemErrorException(mName +
                    "non-text mve contains a text formal arg?!?!");
        }

        // get the n'th argument from the argument list.  Again, this
        // is the actual argument -- must be careful not to modify it
        // in any way.
        oldArg = this.argList.get(n);

        if ( oldArg == null )
        {
            throw new SystemErrorException(mName + "no " + n +
                    "th source argument?!?!");
        }

        switch (fa.getFargType())
        {
            case COL_PREDICATE:
                if ( ! ( newArg instanceof ColPredDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: col pred expected.");
                }
                break;

            case FLOAT:
                if ( ! ( newArg instanceof FloatDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: float expected.");
                }
                break;

            case INTEGER:
                if ( ! ( newArg instanceof IntDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: integer expected.");
                }
                break;

            case NOMINAL:
                if ( ! ( newArg instanceof NominalDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: nominal expected.");
                }
                break;

            case PREDICATE:
                if ( ! ( newArg instanceof PredDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: Predicate expected.");
                }
                break;

            case TIME_STAMP:
                if ( ! ( newArg instanceof TimeStampDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: time stamp expected.");
                }
                break;

            case QUOTE_STRING:
                if ( ! ( newArg instanceof QuoteStringDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: quote string expected.");
                }
                break;

            case TEXT:
                if ( ! ( newArg instanceof TextStringDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: text string expected.");
                }
                break;

            case UNTYPED:
                if ( newArg instanceof TextStringDataValue )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: Text String can't be " +
                            "substituted for untyped arguments.");
                }
                else if ( ! ( ( newArg instanceof ColPredDataValue ) ||
                              ( newArg instanceof FloatDataValue ) ||
                              ( newArg instanceof IntDataValue ) ||
                              ( newArg instanceof NominalDataValue ) ||
                              ( newArg instanceof PredDataValue ) ||
                              ( newArg instanceof TimeStampDataValue ) ||
                              ( newArg instanceof QuoteStringDataValue ) ||
                              ( newArg instanceof UndefinedDataValue ) ) )
                {
                    throw new SystemErrorException(mName +
                            "Unknown subtype of DataValue");
                }
                break;

            case UNDEFINED:
                throw new SystemErrorException(mName +
                        "formal arg type undefined???");
                /* break statement commented out to keep the compiler happy */
                // break;

            default:
                throw new SystemErrorException(mName +
                                               "Unknown Formal Arg Type");
                /* break statement commented out to keep the compiler happy */
                // break;
        }

        if ( newArg.getItsFargID() == DBIndex.INVALID_ID )
        {
            newArg.setItsFargID(fa.getID());
        }
        else if ( newArg.getItsFargID() != fa.getID() )
        {
            throw new SystemErrorException(mName + "formal arg ID mismatch");
        }

        if ( this.argList.set(n, newArg) != oldArg )
        {
            throw new SystemErrorException(mName + "replaced wrong arg?!?");
        }

        return;

    } /* Matrix::replaceArg(n, newArg) */


    /**
     * updateForMVEDefChange()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * matrix vocab element definition change message to any predicate
     * or column predicate data values.
     *                                           -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDefChange(
                                 Database db,
                                 long mveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList,
                                 long[] cpn2o,
                                 long[] cpo2n,
                                 boolean[] cpFargNameChanged,
                                 boolean[] cpFargSubRangeChanged,
                                 boolean[] cpFargRangeChanged,
                                 boolean[] cpFargDeleted,
                                 boolean[] cpFargInserted,
                                 java.util.Vector<FormalArgument> oldCPFargList,
                                 java.util.Vector<FormalArgument> newCPFargList)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForMVEDefChange(): ";
        DBElement dbe = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        dbe = this.db.idx.getElement(mveID);

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "mveID doesn't refer to a mve.");
        }

        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).updateForMVEDefChange(db,
                                                          mveID,
                                                          nameChanged,
                                                          oldName,
                                                          newName,
                                                          varLenChanged,
                                                          oldVarLen,
                                                          newVarLen,
                                                          fargListChanged,
                                                          n2o,
                                                          o2n,
                                                          fargNameChanged,
                                                          fargSubRangeChanged,
                                                          fargRangeChanged,
                                                          fargDeleted,
                                                          fargInserted,
                                                          oldFargList,
                                                          newFargList,
                                                          cpn2o,
                                                          cpo2n,
                                                          cpFargNameChanged,
                                                          cpFargSubRangeChanged,
                                                          cpFargRangeChanged,
                                                          cpFargDeleted,
                                                          cpFargInserted,
                                                          oldCPFargList,
                                                          newCPFargList);
            }
            else if ( dv instanceof ColPredDataValue )
            {
                ((ColPredDataValue)dv).updateForMVEDefChange(
                                                          db,
                                                          mveID,
                                                          nameChanged,
                                                          oldName,
                                                          newName,
                                                          varLenChanged,
                                                          oldVarLen,
                                                          newVarLen,
                                                          fargListChanged,
                                                          n2o,
                                                          o2n,
                                                          fargNameChanged,
                                                          fargSubRangeChanged,
                                                          fargRangeChanged,
                                                          fargDeleted,
                                                          fargInserted,
                                                          oldFargList,
                                                          newFargList,
                                                          cpn2o,
                                                          cpo2n,
                                                          cpFargNameChanged,
                                                          cpFargSubRangeChanged,
                                                          cpFargRangeChanged,
                                                          cpFargDeleted,
                                                          cpFargInserted,
                                                          oldCPFargList,
                                                          newCPFargList);
            }
        }

        return;

    } /* Matrix::updateForMVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * matrix vocab element deletion message to any column predicate or
     * predicate data values.
     *                                           -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDeletion(Database db,
                                        long mveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForMVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        MatrixVocabElement mve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        ColPredDataValue cpdv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        numArgs = this.argList.size();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        i = 0;

        while ( i < numArgs )
        {
            dv = this.getArg(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "arg " + i +
                                               " is null?!?!");
            }

            if ( dv instanceof ColPredDataValue )
            {
                cpdv = (ColPredDataValue)dv;

                if ( cpdv.getItsValueMveID() == mveID )
                {
                    if ( cpdv.getItsFargType()
                            == FormalArgument.FArgType.UNTYPED ) {
                        if ( mve == null )
                        {
                            dbe = this.db.idx.getElement(this.mveID);

                            if ( dbe == null )
                            {
                                throw new SystemErrorException(mName +
                                        "this.mveID has no referent");
                            }

                            if ( ! ( dbe instanceof MatrixVocabElement ) )
                            {
                                throw new SystemErrorException(mName +
                                 "mveID doesn't refer to Matrix vocab element");
                            }

                            mve = (MatrixVocabElement)dbe;
                        }

                        fa = mve.getFormalArg(i);

                        if ( fa == null )
                        {
                            throw new SystemErrorException(mName + "no " + i +
                                    "th formal argument?!?!");
                        }

                        dv = fa.constructEmptyArg();

                        this.replaceArg(i, dv);
                    }
                    else if ( dv.getItsFargType() ==
                              FormalArgument.FArgType.COL_PREDICATE )
                    {
                        ((ColPredDataValue)dv).updateForMVEDeletion(db, mveID);
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "arg " + i +
                                                   " has unexpected fArgType.");
                    }
                }
                else
                {
                    ((ColPredDataValue)dv).updateForMVEDeletion(db, mveID);
                }
            }
            else if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).updateForMVEDeletion(db, mveID);
            }

            i++;
        }

        return;

    } /* Matrix::updateForMVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to any predicate
     * data values.
     *                                           -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDefChange(
                                 Database db,
                                 long pveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForPVEDefChange(): ";
        DBElement dbe = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        dbe = this.db.idx.getElement(pveID);

        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "pveID doesn't refer to a pve.");
        }

        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).updateForPVEDefChange(db,
                                                          pveID,
                                                          nameChanged,
                                                          oldName,
                                                          newName,
                                                          varLenChanged,
                                                          oldVarLen,
                                                          newVarLen,
                                                          fargListChanged,
                                                          n2o,
                                                          o2n,
                                                          fargNameChanged,
                                                          fargSubRangeChanged,
                                                          fargRangeChanged,
                                                          fargDeleted,
                                                          fargInserted,
                                                          oldFargList,
                                                          newFargList);
            }
        }

        return;

    } /* Matrix::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to any predicate
     * data values.
     *                                           -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long pveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForPVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        MatrixVocabElement mve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        numArgs = this.argList.size();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        i = 0;

        while ( i < numArgs )
        {
            dv = this.getArg(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "arg " + i +
                                               " is null?!?!");
            }

            if ( dv instanceof PredDataValue )
            {
                pdv = (PredDataValue)dv;

                if ( pdv.getItsValuePveID() == pveID )
                {
                    if (dv.getItsFargType() == FormalArgument.FArgType.UNTYPED)
                    {
                        if ( mve == null )
                        {
                            dbe = this.db.idx.getElement(this.mveID);

                            if ( dbe == null )
                            {
                                throw new SystemErrorException(mName +
                                        "this.mveID has no referent");
                            }

                            if ( ! ( dbe instanceof MatrixVocabElement ) )
                            {
                                throw new SystemErrorException(mName +
                                 "mveID doesn't refer to Matrix vocab element");
                            }

                            mve = (MatrixVocabElement)dbe;
                        }

                        fa = mve.getFormalArg(i);

                        if ( fa == null )
                        {
                            throw new SystemErrorException(mName + "no " + i +
                                    "th formal argument?!?!");
                        }

                        dv = fa.constructEmptyArg();

                        this.replaceArg(i, dv);
                    }
                    else if ( dv.getItsFargType()
                                        == FormalArgument.FArgType.PREDICATE )
                    {
                        ((PredDataValue)dv).updateForPVEDeletion(db, pveID);
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "arg " + i +
                                                   " has unexpected fArgType.");
                    }
                }
                else
                {
                    ((PredDataValue)dv).updateForPVEDeletion(db, pveID);
                }
            }

            i++;
        }

        return;

    } /* Matrix::updateForPVEDeletion() */


    /**
     * updateIndexForReplacementVal()
     *
     * When the old incarnation of the canonnical version of a DataCell is
     * replaced with the new, we must update the index so that DataValues and
     * predicates that don't appear in the new incarnation are removed from
     * the index, DataValues and Predicates that are introduced in the
     * new incarnation are inserted in the index, and the index is updated
     * to point to the new versions of DataValues and Predicates that appear
     * in both.
     *
     * If there is no structural change in the underlying mve's and pve's,
     * this task relatively straight forward, as continuing objects will
     * reside in the same location in the old and new argument lists, and
     * will share IDs.  New items will reside in the new argument list, and
     * have invalid IDs, and items that will cease to exist will reside in the
     * old argument list, and not have a new version with the same ID in the
     * same location in the new argument list.
     *
     * If there is structural change, things get much more complicated --
     * however we limit the complexity by allowing at most one mve or pve
     * to be modified or deleted in any one cycle.  Thus we are given that
     * at most one of the cascadeMveMod, cascadeMveDel, cascadePveMod, and
     * cascadePveDel parameters will be true.
     *
     *
     * 1) cascadeMveMod == true
     *
     * If cascadeMveMod is true, then a mve has been modified, and the ID of
     * the modified mve is in cascadeMveID.
     *
     * If cascadeMveID == this.mveID, then the definition of the mve that
     * defines the structure of this instance of Matrix has changed.
     *
     * Thus it is possible that formal arguments have been deleted and/or
     * re-arranged.  Thus instead of looking just in the corresponding location
     * in the old argument list for the old version of an argument in the new
     * list, we must scan the entire old argument list for the old version.
     * Similarly for each item in the old argument list, we must scan the
     * new argument list to verify that there is no new version, and the
     * old argument (and all its descendants -- if any) must be removed from
     * the index.
     *
     * If cascadeMveID != this.mveID, then we can proceed as per the no
     * structural change case -- for this matrix at least.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * In this case, verify that this.mveID != cascadeMveID, and then proceed
     * as per the no structural change case.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * Proceed as per the no structural change case.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and teh ID of
     * the deleted pve is in cascadePveID.
     *
     * Proceed as per the no structural change case.
     *
     *                                       -- 2/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateIndexForReplacementVal(Matrix oldMatrix,
                                                long DCID,
                                                boolean cascadeMveMod,
                                                boolean cascadeMveDel,
                                                long cascadeMveID,
                                                boolean cascadePveMod,
                                                boolean cascadePveDel,
                                                long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateIndexForReplacementVal(): ";

        int i = 0;
        DBElement dbe = null;
        DataCell dc = null;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue newArg = null;
        DataValue oldArg = null;
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        new_cpdv;
        FloatDataValue          new_fdv;
        IntDataValue            new_idv;
        NominalDataValue        new_ndv;
        PredDataValue           new_pdv;
        TimeStampDataValue      new_tsdv;
        QuoteStringDataValue    new_qsdv;
        TextStringDataValue     new_tdv;
        ColPredDataValue        old_cpdv;
        FloatDataValue          old_fdv;
        IntDataValue            old_idv;
        NominalDataValue        old_ndv;
        PredDataValue           old_pdv;
        TimeStampDataValue      old_tsdv;
        QuoteStringDataValue    old_qsdv;
        TextStringDataValue     old_tdv;

        // validate the oldMatrix parameter

        if ( oldMatrix == null )
        {
            throw new SystemErrorException(mName + "oldMatrix == null");
        }
        else if ( oldMatrix.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                    "oldMatrix.mveID == INVALID_ID");
        }
        else if ( oldMatrix.argList == null )
        {
            throw new SystemErrorException(mName + "oldMatrix.argList == null");
        }
        else if ( oldMatrix.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName +
                    "oldMatrix.getNumArgs() <= 0");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else if ( ( cascadeMveDel ) && ( this.mveID == cascadeMveID ) )
        {
            throw new SystemErrorException(mName +
                    "cascadeMveDel && (this.mveID == cascadeMveID)");
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( this.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName + "this.getNumArgs() <= 0");
        }

        if ( this.mveID != oldMatrix.mveID )
        {
            throw new SystemErrorException(mName + "mveID mismatch");
        }
        else if ( ( ! cascadeMveMod ) &&
                  ( this.getNumArgs() != oldMatrix.getNumArgs() ) )
        {
            throw new SystemErrorException(mName + "num args mismatch");
        }

        mve = this.lookupMatrixVE(this.mveID);

        if ( mve.getDB() != this.getDB() )
        {
            throw new SystemErrorException(mName +
                                           "mve.getDB() != this.getDB()");
        }
        else if ( mve.getDB() != oldMatrix.getDB() )
        {
            throw new SystemErrorException(mName +
                    "mve.getDB() != oldMatrix.getDB()");
        }

        if ( mve.getNumFormalArgs() != this.getNumArgs() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getNumFormalArgs() != this.getNumArgs()");
        }

        if ( mve.getVarLen() != this.getVarLen() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getVarLen() != this.getValLen()");
        }

        // Validate the DCID parameter

        if ( DCID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "DCID invalid?!?");
        }

        dbe = this.db.idx.getElement(DCID);

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "ID doesn't refer to a DataCell");
        }

        dc = (DataCell)dbe;

        if ( dc.getItsMveID() != this.mveID )
        {
            throw new SystemErrorException(mName +
                    "mveID mismatch with DataCell");
        }

        // Now scan the argument list
        if ( ( cascadeMveMod ) && ( cascadeMveID == this.mveID ) )
        {
            assert( ! cascadeMveDel );
            assert( ! cascadePveDel );
            assert( ! cascadePveMod );

            // The mve whose definition underlies the old and new incarnations
            // of the data cell has changed -- thus it is possible that formal
            // arguments have shifted location, been removed, or added.  We
            // must update the index accordingly.
            //
            // Fortunately, we can count on the following:
            //
            // 1) If the formal argument associated with an argument has been
            //    removed, then the new version of the data cell will contain
            //    no argument with the same ID as that associated with the
            //    formal argument that has been removed.
            //
            // 2) If a formal argument has been added, then the argument
            //    associated with the formal argument in the new data cell
            //    will have the invalid id.
            //
            // With these two assurances in hand, we can process the two
            // argument lists as follows once the sanity checks pass:
            //
            // First, scan the old list for IDs that don't exist in the new
            // list.  Delete the associated entries from the index.
            //
            // Second scan the new list.  If an entry has invalid ID, just
            // insert it in the index.  If it has valid id, use it to replace
            // the entry in the old list with the same ID.  If no such old
            // argument exists, scream and die.

            // first remove unmatched old arguments from the index...
            i = 0;
            while ( i < oldMatrix.getNumArgs() )
            {
                int j = 0;
                boolean foundMatch = false;

                oldArg = oldMatrix.argList.get(i);

                while ( j < this.getNumArgs() )
                {
                    newArg = this.argList.get(j);

                    if ( newArg.getID() == oldArg.getID() )
                    {
                        if ( foundMatch )
                        {
                            throw new SystemErrorException(mName +
                                                   "found duplicate match?!?");
                        }
                        else
                        {
                            foundMatch = true;
                        }
                    }
                    j++;
                }

                if ( ! foundMatch )
                {
                    oldArg.removeFromIndex(DCID);
                }

                i++;
            }

            i = 0;
            while ( i < this.getNumArgs() )
            {
                // get the i-th formal argument.  This is the mve's actual
                // argument,  so be careful not to modify it in any way.
                fa = mve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( ( fa instanceof QuoteStringFormalArg ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.MATRIX) )
                {
                    throw new SystemErrorException(mName +
                       "non-matrix mve contains a quote string formal arg?!?!");
                }
                else if ( ( fa instanceof TextStringFormalArg ) &&
                          ( i != 0 ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.TEXT ) )
                {
                    throw new SystemErrorException(mName +
                        "non-text mve contains a text string formal arg?!?!");
                }

                // get the i'th argument from the new argument list, and
                // the matching argument (if any) from the old argument list.
                // Again, these are the actual arguments -- must be
                // careful not to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = null;

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( newArg.getID() != DBIndex.INVALID_ID )
                {
                    // the old argument list must contain an argument
                    // with the same ID.  Scan the list to find it.
                    int j = 0;

                    while ( ( j < oldMatrix.getNumArgs() ) &&
                            ( oldArg == null ) )
                    {
                        oldArg = oldMatrix.argList.get(j);

                        if ( oldArg.getID() == DBIndex.INVALID_ID )
                        {
                            throw new SystemErrorException(mName + i +
                                    "th old argument not in index?!?!");
                        }

                        if ( oldArg.getID() != newArg.getID() )
                        {
                            oldArg = null;
                        }

                        j++;
                    }

                    if ( oldArg == null )
                    {
                        throw new SystemErrorException(mName +
                            "new arg has valid ID but no matching old arg.");
                    }
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                switch (fa.getFargType())
                {
                    case COL_PREDICATE:
                        if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof ColPredDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                   "Type mismatch: Col Predicate(s) expected.");
                        }

                        cpfa = (ColPredFormalArg)fa;
                        new_cpdv = (ColPredDataValue)newArg;

                        if ( oldArg != null )
                        {
                            old_cpdv = (ColPredDataValue)oldArg;
                        }
                        else
                        {
                            old_cpdv = null;
                        }

                        if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_cpdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_cpdv.getItsValue()
                                                .validateColumnPredicate(true);
                        }
                        else if ( old_cpdv != null )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName +
                            "new_cpdv has valid ID but old_cpdv is null?!?");
                        }
                        break;

                    case FLOAT:
                        if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof FloatDataValue ) ) ) )
                        {
                           throw new SystemErrorException(mName +
                                    "Type mismatch: float(s) expected.");
                        }

                        ffa = (FloatFormalArg)fa;
                        new_fdv = (FloatDataValue)newArg;

                        if ( new_fdv.getSubRange() != ffa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_fdv.getSubRange() != ffa.getSubRange().");
                        }

                        if ( new_fdv.getSubRange() )
                        {
                            if ( ( ffa.getMinVal() >
                                    new_fdv.getItsValue() ) ||
                                 ( ffa.getMaxVal() <
                                    new_fdv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_fdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case INTEGER:
                        if ( ( ! ( newArg instanceof IntDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof IntDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: integer(s) expected.");
                        }

                        ifa = (IntFormalArg)fa;
                        new_idv = (IntDataValue)newArg;

                        if ( new_idv.getSubRange() != ifa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_idv.getSubRange() != ifa.getSubRange().");
                        }

                        if ( new_idv.getSubRange() )
                        {
                            if ( ( ifa.getMinVal() >
                                    new_idv.getItsValue() ) ||
                                 ( ifa.getMaxVal() <
                                    new_idv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_idv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case NOMINAL:
                        if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof NominalDataValue ) )
                             )
                           )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: nominal(s) expected.");
                        }

                        nfa = (NominalFormalArg)fa;
                        new_ndv = (NominalDataValue)newArg;

                        if ( new_ndv.getSubRange() != nfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_ndv.getSubRange() != nfa.getSubRange().");
                        }

                        if ( ( new_ndv.getSubRange() ) &&
                             ( new_ndv.getItsValue() != null ) )
                        {
                            if ( ! nfa.approved(new_ndv.getItsValue()) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_ndv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case PREDICATE:
                        if ( ( ! ( newArg instanceof PredDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof PredDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Predicate(s) expected.");
                        }

                        pfa = (PredFormalArg)fa;
                        new_pdv = (PredDataValue)newArg;

                        if ( oldArg != null )
                        {
                            old_pdv = (PredDataValue)oldArg;
                        }
                        else
                        {
                            old_pdv = null;
                        }

                        if ( new_pdv.getSubRange() != pfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_pdv.getSubRange() != pfa.getSubRange().");
                        }

                        if ( ( new_pdv.getItsValue().getPveID() !=
                                DBIndex.INVALID_ID ) &&
                             ( new_pdv.getSubRange() ) &&
                             ( ! pfa.approved(new_pdv.getItsValue().
                                        getPveID()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "new_pdv.getItsValue() out of range.");
                        }

                        if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_pdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_pdv.getItsValue().validatePredicate(true);
                        }
                        else if ( old_pdv != null )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName +
                            "new_pdv has valid ID but old_pdv is null?!?");
                        }
                        break;

                    case TIME_STAMP:
                        if ( ( ! ( newArg instanceof
                                    TimeStampDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      TimeStampDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "Type mismatch: time stamp(s) expected.");
                        }

                        tsfa = (TimeStampFormalArg)fa;
                        new_tsdv = (TimeStampDataValue)newArg;

                        if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                           "new_tsdv.getSubRange() != tsfa.getSubRange().");
                        }

                        if ( new_tsdv.getSubRange() )
                        {
                            if ( ( tsfa.getMinVal().
                                    gt(new_tsdv.getItsValue()) ) ||
                                 ( tsfa.getMaxVal().
                                    lt(new_tsdv.getItsValue()) ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_tsdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case QUOTE_STRING:
                        if ( ( ! ( newArg instanceof
                                    QuoteStringDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      QuoteStringDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: quote string(s) expected.");
                        }
                        break;

                    case TEXT:
                        if ( ( ! ( newArg instanceof
                                    TextStringDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      TextStringDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: text string(s) expected.");
                        }
                        break;

                    case UNTYPED:
                        if ( ( newArg instanceof TextStringDataValue ) ||
                             ( ( oldArg != null ) &&
                               ( oldArg instanceof TextStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "Type mismatch: Text String(s) can't be " +
                                "substituted for untyped arguments.");
                        }
                        else if ( ! ( ( newArg instanceof
                                        ColPredDataValue ) ||
                                      ( newArg instanceof
                                        FloatDataValue ) ||
                                      ( newArg instanceof
                                        IntDataValue ) ||
                                      ( newArg instanceof
                                        NominalDataValue ) ||
                                      ( newArg instanceof
                                        PredDataValue ) ||
                                      ( newArg instanceof
                                        TimeStampDataValue ) ||
                                      ( newArg instanceof
                                        QuoteStringDataValue ) ||
                                      ( newArg instanceof
                                        UndefinedDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Unknown subtype of DataValue");
                        }

                        if ( ( ( oldArg == null )
                               ||
                               ( newArg.getClass() != oldArg.getClass() )
                             )
                             &&
                             ( newArg.getID() != DBIndex.INVALID_ID ) )
                        {
                            throw new SystemErrorException(mName +
                                    "dv type change and id set(2)");
                        }

                        if ( newArg instanceof ColPredDataValue )
                        {
                            new_cpdv = (ColPredDataValue)newArg;

                            if ( ( oldArg != null ) &&
                                 ( oldArg instanceof ColPredDataValue ) )
                            {
                                old_cpdv = (ColPredDataValue)oldArg;

                                assert( cascadeMveMod );

                                new_cpdv.getItsValue().
                                        validateReplacementColPred(
                                            old_cpdv.getItsValueBlind(),
                                            cascadeMveMod,
                                            cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveMod,
                                            cascadePveDel,
                                            cascadePveID);
                            }
                            else
                            {
                                new_cpdv.getItsValue().
                                        validateColumnPredicate(true);
                            }
                        }
                        else if ( newArg instanceof PredDataValue )
                        {
                            new_pdv = (PredDataValue)newArg;

                            if ( ( oldArg != null ) &&
                                 ( oldArg instanceof PredDataValue ) )
                            {
                                old_pdv = (PredDataValue)oldArg;

                                new_pdv.getItsValue().
                                        validateReplacementPredicate(
                                            old_pdv.getItsValueBlind(),
                                            cascadeMveMod,
                                            cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveMod,
                                            cascadePveDel,
                                            cascadePveID);
                            }
                            else
                            {
                                new_pdv.getItsValue().
                                        validatePredicate(true);
                            }
                        }
                        break;

                    case UNDEFINED:
                        throw new SystemErrorException(mName +
                                "formal arg type undefined???");
                        /* break statement commented out to keep the
                         * compiler happy
                         */
                        // break;

                    default:
                        throw new SystemErrorException(mName +

                                "Unknown Formal Arg Type");
                        /* break statement commented out to keep the
                         * compiler happy
                         */
                        // break;
                }

                // Sanity checks pass.  If oldArg is defined, the IDs must
                // match and we replace the old version with the new in the
                // index.  Otherwise, just insert the new argument in the
                // index.
                if ( oldArg != null )
                {
                    assert( newArg.getID() == oldArg.getID() );

                    newArg.replaceInIndex(oldArg,
                                          DCID,
                                          cascadeMveMod,
                                          cascadeMveDel,
                                          cascadeMveID,
                                          cascadePveMod,
                                          cascadePveDel,
                                          cascadePveID);
                }
                else
                {
                    assert( newArg.getID() == DBIndex.INVALID_ID );

                    newArg.insertInIndex(DCID);
                }

                i++;

            } /* while */
        }
        else /* no structural change -- at least for this Matrix */
        {
            while ( i < this.getNumArgs() )
            {

                // get the i-th formal argument.  This is the mve's actual
                // argument, so be careful not to modify it in any way.
                fa = mve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( ( fa instanceof TextStringFormalArg ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.TEXT ) )
                {
                    throw new SystemErrorException(mName +
                            "non-text mve contains a text formal arg?!?!");
                }

                // get the i'th arguments from the old and new argument lists.
                // Again, these are the actual arguments -- must be careful not
                // to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = oldMatrix.argList.get(i);

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( oldArg == null )
                {
                    throw new SystemErrorException(mName + "no old" + i +
                            "th argument?!?!");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( fa.getID() != oldArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( oldArg.getID() == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + i +
                            "th old argument not in index?!?!");
                }

                if ( ( newArg.getID() != DBIndex.INVALID_ID ) &&
                     ( newArg.getID() != oldArg.getID() ) )
                {
                    throw new SystemErrorException(mName + i +
                            "th argument id mismatch");
                }

                switch (fa.getFargType())
                {
                    case COL_PREDICATE:
                        if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
                             ( ! ( oldArg instanceof ColPredDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Predicate expected.");
                        }

                        cpfa = (ColPredFormalArg)fa;
                        new_cpdv = (ColPredDataValue)newArg;
                        old_cpdv = (ColPredDataValue)oldArg;

                        if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_cpdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_cpdv.getItsValue()
                                                .validateColumnPredicate(true);
                        }
                        else if ( ( ! cascadeMveMod ) &&
                                  ( ! cascadeMveDel ) &&
                                  ( ! cascadePveMod ) &&
                                  ( ! cascadePveDel ) )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        break;

                    case FLOAT:
                        if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
                             ( ! ( oldArg instanceof FloatDataValue ) ) )
                        {
                           throw new SystemErrorException(mName +
                                    "Type mismatch: float expected.");
                        }

                        ffa = (FloatFormalArg)fa;
                        new_fdv = (FloatDataValue)newArg;
                        old_fdv = (FloatDataValue)oldArg;

                        if ( new_fdv.getSubRange() != ffa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                 "new_fdv.getSubRange() != ffa.getSubRange().");
                        }

                        if ( new_fdv.getSubRange() )
                        {
                            if ( ( ffa.getMinVal() > new_fdv.getItsValue() ) ||
                                 ( ffa.getMaxVal() < new_fdv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_fdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case INTEGER:
                        if ( ( ! ( newArg instanceof IntDataValue ) ) ||
                             ( ! ( oldArg instanceof IntDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: integer expected.");
                        }

                        ifa = (IntFormalArg)fa;
                        new_idv = (IntDataValue)newArg;
                        old_idv = (IntDataValue)oldArg;

                        if ( new_idv.getSubRange() != ifa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                "new_idv.getSubRange() != ifa.getSubRange().");
                        }

                        if ( new_idv.getSubRange() )
                        {
                            if ( ( ifa.getMinVal() > new_idv.getItsValue() ) ||
                                 ( ifa.getMaxVal() < new_idv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_idv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case NOMINAL:
                        if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
                             ( ! ( oldArg instanceof NominalDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: nominal expected.");
                        }

                        nfa = (NominalFormalArg)fa;
                        new_ndv = (NominalDataValue)newArg;
                        old_ndv = (NominalDataValue)oldArg;

                        if ( new_ndv.getSubRange() != nfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                "new_ndv.getSubRange() != nfa.getSubRange().");
                        }

                        if ( ( new_ndv.getSubRange() ) &&
                             ( new_ndv.getItsValue() != null ) )
                        {
                            if ( ! nfa.approved(new_ndv.getItsValue()) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_ndv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case PREDICATE:
                        if ( ( ! ( newArg instanceof PredDataValue ) ) ||
                             ( ! ( oldArg instanceof PredDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Predicate expected.");
                        }

                        pfa = (PredFormalArg)fa;
                        new_pdv = (PredDataValue)newArg;
                        old_pdv = (PredDataValue)oldArg;

                        if ( new_pdv.getSubRange() != pfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                "new_pdv.getSubRange() != pfa.getSubRange().");
                        }

                        if ( ( new_pdv.getItsValue().getPveID() !=
                                DBIndex.INVALID_ID ) &&
                             ( new_pdv.getSubRange() ) &&
                             ( ! pfa.approved(
                                     new_pdv.getItsValue().getPveID()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "new_pdv.getItsValue() out of range.");
                        }

                        if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_pdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_pdv.getItsValue().validatePredicate(true);
                        }
                        else if ( ( ! cascadeMveMod ) &&
                                  ( ! cascadeMveDel ) &&
                                  ( ! cascadePveMod ) &&
                                  ( ! cascadePveDel ) )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        break;

                    case TIME_STAMP:
                        if ( ( ! ( newArg instanceof TimeStampDataValue ) ) ||
                             ( ! ( oldArg instanceof TimeStampDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: time stamp expected.");
                        }

                        tsfa = (TimeStampFormalArg)fa;
                        new_tsdv = (TimeStampDataValue)newArg;
                        old_tsdv = (TimeStampDataValue)oldArg;

                        if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                               "new_tsdv.getSubRange() != tsfa.getSubRange().");
                        }

                        if ( new_tsdv.getSubRange() )
                        {
                            if ( ( tsfa.getMinVal().gt(new_tsdv.getItsValue()) )
                                 ||
                                 ( tsfa.getMaxVal().lt(new_tsdv.getItsValue()) )
                               )
                            {
                                throw new SystemErrorException(mName +
                                        "new_tsdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case QUOTE_STRING:
                        if ( ( ! ( newArg instanceof QuoteStringDataValue ) ) ||
                             ( ! ( oldArg instanceof QuoteStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: quote string expected.");
                        }
                        break;

                    case TEXT:
                        if ( ( ! ( newArg instanceof TextStringDataValue ) ) ||
                             ( ! ( oldArg instanceof TextStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: text string expected.");
                        }
                        break;

                    case UNTYPED:
                        if ( ( newArg instanceof TextStringDataValue ) ||
                             ( oldArg instanceof TextStringDataValue ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Text String can't be " +
                                    "substituted for untyped arguments.");
                        }
                        else if ( ! ( ( newArg instanceof ColPredDataValue )
                                      ||
                                      ( newArg instanceof FloatDataValue )
                                      ||
                                      ( newArg instanceof IntDataValue )
                                      ||
                                      ( newArg instanceof NominalDataValue )
                                      ||
                                      ( newArg instanceof PredDataValue )
                                      ||
                                      ( newArg instanceof TimeStampDataValue )
                                      ||
                                      ( newArg instanceof QuoteStringDataValue )
                                      ||
                                      ( newArg instanceof UndefinedDataValue )
                                    )
                                )
                        {
                            throw new SystemErrorException(mName +
                                    "Unknown subtype of DataValue");
                        }

                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
                             ( newArg.getID() != DBIndex.INVALID_ID ) )
                        {
                            throw new SystemErrorException(mName +
                                    "dv type change and id set");
                        }

                        if ( newArg instanceof ColPredDataValue )
                        {
                            new_cpdv = (ColPredDataValue)newArg;

                            if ( oldArg instanceof ColPredDataValue )
                            {
                                old_cpdv = (ColPredDataValue)oldArg;

                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                                     ( cascadePveMod ) || ( cascadePveDel ) )
                                {
                                    new_cpdv.getItsValue().
                                            validateReplacementColPred(
                                                old_cpdv.getItsValueBlind(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                                else
                                {
                                    new_cpdv.getItsValue().
                                            validateReplacementColPred(
                                                old_cpdv.getItsValue(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                            }
                            else
                            {
                                new_cpdv.getItsValue().
                                        validateColumnPredicate(true);
                            }
                        }
                        else if ( newArg instanceof PredDataValue )
                        {
                            new_pdv = (PredDataValue)newArg;

                            if ( oldArg instanceof PredDataValue )
                            {
                                old_pdv = (PredDataValue)oldArg;

                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                                     ( cascadePveMod ) || ( cascadePveDel ) )
                                {
                                    new_pdv.getItsValue().
                                            validateReplacementPredicate(
                                                old_pdv.getItsValueBlind(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                                else
                                {
                                    new_pdv.getItsValue().
                                            validateReplacementPredicate(
                                                old_pdv.getItsValue(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                            }
                            else
                            {
                                new_pdv.getItsValue().
                                        validatePredicate(true);
                            }
                        }
                        break;

                    case UNDEFINED:
                        throw new SystemErrorException(mName +
                                "formal arg type undefined???");
                        // break statement commented out to keep the compiler
                        // happy
                        // break;

                    default:
                        throw new SystemErrorException(mName +
                                "Unknown Formal Arg Type");
                        // break statement commented out to keep the compiler
                        // happy
                        // break;
                }

                // Sanity checks pass.  If the ID's of the old and new versions
                // of the argument match, replace the old incarnation of the
                // formal argument with the new in the index.
                //
                // Otherwise, remove the old from the index, and insert the new.
                if ( newArg.getID() == oldArg.getID() )
                {
                    newArg.replaceInIndex(oldArg,
                                          DCID,
                                          cascadeMveMod,
                                          cascadeMveDel,
                                          cascadeMveID,
                                          cascadePveMod,
                                          cascadePveDel,
                                          cascadePveID);
                }
                else /* new_fdv.getID() == DBIndex.INVALID_ID */
                {
                    oldArg.removeFromIndex(DCID);
                    newArg.insertInIndex(DCID);
                }

                i++;

            } /* while */
        }

        return;

    } /* Matrix::updateIndexForReplacementVal() */


    /**
     * validateMatrix()
     *
     * Verify that the arguments of the matrix are of type and value
     * consistant with the target MatrixVocabElement.  If specified by the
     * idMustBeInvalid parameter, verify that the ids associated with all
     * the DataValues and Predicates in the matrix are invalid.  This is purely
     * a sanity checking routine.  The test should always pass.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateMatrix(boolean idMustBeInvalid)
        throws SystemErrorException
    {
        final String mName = "Matrix::validateMatrix(): ";
        int i = 0;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue arg = null;
        ColPredFormalArg cpfa;
        FloatFormalArg ffa;
        IntFormalArg ifa;
        NominalFormalArg nfa;
        PredFormalArg pfa;
        TimeStampFormalArg tsfa;
        ColPredDataValue cpdv;
        FloatDataValue fdv;
        IntDataValue idv;
        NominalDataValue ndv;
        PredDataValue pdv;
        TimeStampDataValue tsdv;

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( this.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName + "this.getNumArgs() <= 0");
        }

        mve = this.lookupMatrixVE(this.mveID);

        if ( mve.getDB() != this.getDB() )
        {
            throw new SystemErrorException(mName +
                                           "mve.getDB() != this.getDB()");
        }

        if ( mve.getNumFormalArgs() != this.getNumArgs() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getNumFormalArgs() != this.getNumArgs()");
        }

        if ( mve.getVarLen() != this.getVarLen() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getVarLen() != this.getValLen()");
        }


        // Now scan the argument list
        while ( i < this.getNumArgs() )
        {

            // get the i-th formal argument.  This is the mve's actual argument,
            // so be careful not to modify it in any way.
            fa = mve.getFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }
            else if ( ( fa instanceof TextStringFormalArg ) &&
                      ( mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT ) )
            {
                throw new SystemErrorException(mName +
                        "non-text mve contains a text formal arg?!?!");
            }

            // get the i'th argument from the argument list.  Again, this
            // is the actual argument -- must be careful not to modify it
            // in any way.
            arg = this.argList.get(i);

            if ( arg == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th argument?!?!");
            }

            if ( fa.getID() != arg.getItsFargID() )
            {
                throw new SystemErrorException(mName +
                                "fa.getID() != arg.getItsFargID()");
            }

            if ( ( idMustBeInvalid ) &&
                 ( arg.getID() != DBIndex.INVALID_ID ) )
            {
                throw new SystemErrorException(mName + i +
                        "th argument not new?!?!");
            }

            switch (fa.getFargType())
            {
                case COL_PREDICATE:
                    if ( ! ( arg instanceof ColPredDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: col pred expected");
                    }

                    cpfa = (ColPredFormalArg)fa;
                    cpdv = (ColPredDataValue)arg;

                    cpdv.getItsValue().validateColumnPredicate(idMustBeInvalid);
                    break;

                case FLOAT:
                    if ( ! ( arg instanceof FloatDataValue ) )
                    {
                       throw new SystemErrorException(mName +
                                "Type mismatch: float expected.");
                    }

                    ffa = (FloatFormalArg)fa;
                    fdv = (FloatDataValue)arg;

                    if ( fdv.getSubRange() != ffa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                                "fdv.getSubRange() != ffa.getSubRange().");
                    }

                    if ( fdv.getSubRange() )
                    {
                        if ( ( ffa.getMinVal() > fdv.getItsValue() ) ||
                             ( ffa.getMaxVal() < fdv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                    "fdv.getItsValue() out of range.");
                        }
                    }
                    break;

                case INTEGER:
                    if ( ! ( arg instanceof IntDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: integer expected.");
                    }

                    ifa = (IntFormalArg)fa;
                    idv = (IntDataValue)arg;

                    if ( idv.getSubRange() != ifa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                                "idv.getSubRange() != ifa.getSubRange().");
                    }

                    if ( idv.getSubRange() )
                    {
                        if ( ( ifa.getMinVal() > idv.getItsValue() ) ||
                             ( ifa.getMaxVal() < idv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                    "idv.getItsValue() out of range.");
                        }
                    }
                    break;

                case NOMINAL:
                    if ( ! ( arg instanceof NominalDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: nominal expected.");
                    }

                    nfa = (NominalFormalArg)fa;
                    ndv = (NominalDataValue)arg;

                    if ( ndv.getSubRange() != nfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                                "ndv.getSubRange() != nfa.getSubRange().");
                    }

                    if ( ( ndv.getSubRange() ) &&
                         ( ndv.getItsValue() != null ) )
                    {
                        if ( ! nfa.approved(ndv.getItsValue()) )
                        {
                            throw new SystemErrorException(mName +
                                    "ndv.getItsValue() out of range.");
                        }
                    }
                    break;

                case PREDICATE:
                    if ( ! ( arg instanceof PredDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: Predicate expected.");
                    }

                    pfa = (PredFormalArg)fa;
                    pdv = (PredDataValue)arg;

                    if ( pdv.getSubRange() != pfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                                "pdv.getSubRange() != pfa.getSubRange().");
                    }

                    if ( ( pdv.getItsValue().getPveID() !=
                            DBIndex.INVALID_ID ) &&
                         ( pdv.getSubRange() ) &&
                         ( ! pfa.approved(pdv.getItsValue().getPveID()) ) )
                    {
                        throw new SystemErrorException(mName +
                                "pdv.getItsValue() out of range.");
                    }

                    pdv.getItsValue().validatePredicate(idMustBeInvalid);
                    break;

                case TIME_STAMP:
                    if ( ! ( arg instanceof TimeStampDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: time stamp expected.");
                    }

                    tsfa = (TimeStampFormalArg)fa;
                    tsdv = (TimeStampDataValue)arg;

                    if ( tsdv.getSubRange() != tsfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                                "tsdv.getSubRange() != tsfa.getSubRange().");
                    }

                    if ( tsdv.getSubRange() )
                    {
                        if ( ( tsfa.getMinVal().gt(tsdv.getItsValue()) ) ||
                             ( tsfa.getMaxVal().lt(tsdv.getItsValue()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "tsdv.getItsValue() out of range.");
                        }
                    }
                    break;

                case QUOTE_STRING:
                    if ( ! ( arg instanceof QuoteStringDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: quote string expected.");
                    }
                    break;

                case TEXT:
                    if ( ! ( arg instanceof TextStringDataValue ) )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: text string expected.");
                    }
                    break;

                case UNTYPED:
                    if ( arg instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch: Text String can't be " +
                                "substituted for untyped arguments.");
                    }
                    else if ( ! ( ( arg instanceof ColPredDataValue ) ||
                                  ( arg instanceof FloatDataValue ) ||
                                  ( arg instanceof IntDataValue ) ||
                                  ( arg instanceof NominalDataValue ) ||
                                  ( arg instanceof PredDataValue ) ||
                                  ( arg instanceof TimeStampDataValue ) ||
                                  ( arg instanceof QuoteStringDataValue ) ||
                                  ( arg instanceof UndefinedDataValue ) ) )
                    {
                        throw new SystemErrorException(mName +
                                "Unknown subtype of DataValue");
                    }

                    if ( arg instanceof ColPredDataValue )
                    {
                        cpdv = (ColPredDataValue)arg;

                        cpdv.getItsValue().validateColumnPredicate(
                                idMustBeInvalid);
                    }
                    else if ( arg instanceof PredDataValue )
                    {
                        pdv = (PredDataValue)arg;

                        pdv.getItsValue().validatePredicate(idMustBeInvalid);
                    }
                    break;

                case UNDEFINED:
                    throw new SystemErrorException(mName +
                            "formal arg type undefined???");
                    /* break statement commented out to keep the
                     * compiler happy
                     */
                    // break;

                default:
                    throw new SystemErrorException(mName +
                                                   "Unknown Formal Arg Type");
                    /* break statement commented out to keep the
                     * compiler happy
                     */
                    // break;
            }

            i++;

        } /* while */

        return;

    } /* Matrix::validateMatrix() */


    /**
     * validateNewMatrix()
     *
     * Verify that the arguments of the matrix are of type and value
     * consistant with the target MatrixVocabElement.  Verify that the IDs
     * of all the DataValues and Predicates in the matrix are invalid. This
     * is purely a sanity checking routine.  The test should always pass.
     *
     * Note that this method is run on the initial value of a DataCell.
     * Thus, all DataValues and Predicates that may appear in the matrix
     * must be new as well -- that is the id must be INVALID_ID pending
     * initial insertion in the index.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateNewMatrix()
        throws SystemErrorException
    {
        this.validateMatrix(true);

        return;

    } /* Matrix::validateNewMatrix()


    /**
     * validateReplacementMatrix()
     *
     * Verify that this matrix is a valid replacement for the supplied old
     * matrix.  This method is called when a new version of a DataCell is
     * about to replace an old version as the cannonical incarnation of the
     * DataCell.  This is purely a sanity checking routine.  The test should
     * always pass.
     *
     * In all cases, this requires that we verify that the argument list of
     * the matrix is congruent with the formal argument list supplied by the
     * target mveID.
     *
     * Further, verify that all arguments either have invalid ID, or have an
     * argument of matching type and ID in oldMatrix.  Unless the target mve
     * has been modified (i.e. cascadeMveMod == true and cascadeMveID ==
     * this.mveID), these matching arguments must be in the same location
     * in oldMatrix's argument list.
     *
     * If there is no structural change in the underlying mve's and pve's,
     * this task relatively straight forward, as continuing objects will
     * reside in the same location in the old and new argument lists, and
     * will share IDs.  New items will reside in the new argument list, and
     * have invalid IDs, and items that will cease to exist will reside in the
     * old argument list, and not have a new version with the same ID in the
     * same location in the new argument list.
     *
     * If there is structural change, things get much more complicated --
     * however we limit the complexity by allowing at most one mve or pve
     * to be modified or deleted in any one cycle.  Thus we are given that
     * at most one of the cascadeMveMod, cascadeMveDel, cascadePveMod, and
     * cascadePveDel parameters will be true.
     *
     *
     * 1) cascadeMveMod == true
     *
     * If cascadeMveMod is true, then a mve has been modified, and the ID of
     * the modified mve is in cascadeMveID.
     *
     * If cascadeMveID == this.mveID, then the definition of the mve that
     * defines the structure of this instance of Matrix has changed.
     *
     * Thus it is possible that formal arguments have been deleted and/or
     * re-arranged.  Thus instead of looking just in the corresponding location
     * in the old argument list for the old version of an argument in the new
     * list, we must scan the entire old argument list for the old version.
     * Similarly for each item in the old argument list, we must scan the
     * new argument list to verify that there is no new version.
     *
     * If cascadeMveID != this.mveID, then we can proceed as per the no
     * structural change case -- for this matrix at least.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * In this case, verify that this.mveID != cascadeMveID, and then proceed
     * as per the no structural change case.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * Proceed as per the no structural change case.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and teh ID of
     * the deleted pve is in cascadePveID.
     *
     * Proceed as per the no structural change case.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateReplacementMatrix(Matrix oldMatrix,
                                          boolean cascadeMveMod,
                                          boolean cascadeMveDel,
                                          long cascadeMveID,
                                          boolean cascadePveMod,
                                          boolean cascadePveDel,
                                          long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::validateReplacementMatrix(): ";
        int i = 0;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue newArg = null;
        DataValue oldArg = null;
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        new_cpdv;
        FloatDataValue          new_fdv;
        IntDataValue            new_idv;
        NominalDataValue        new_ndv;
        PredDataValue           new_pdv;
        TimeStampDataValue      new_tsdv;
        QuoteStringDataValue    new_qsdv;
        TextStringDataValue     new_tdv;
        ColPredDataValue        old_cpdv;
        FloatDataValue          old_fdv;
        IntDataValue            old_idv;
        NominalDataValue        old_ndv;
        PredDataValue           old_pdv;
        TimeStampDataValue      old_tsdv;
        QuoteStringDataValue    old_qsdv;
        TextStringDataValue     old_tdv;

        if ( oldMatrix == null )
        {
            throw new SystemErrorException(mName + "oldMatrix == null");
        }
        else if ( oldMatrix.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                    "oldMatrix.mveID == INVALID_ID");
        }
        else if ( oldMatrix.argList == null )
        {
            throw new SystemErrorException(mName + "oldMatrix.argList == null");
        }
        else if ( oldMatrix.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName +
                    "oldMatrix.getNumArgs() <= 0");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else if ( ( cascadeMveDel ) && ( cascadeMveID == this.mveID ) )
        {
            throw new SystemErrorException(mName + "( cascadeMveDel ) && " +
                    "( cascadeMveID == this.mveID )");
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( this.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName + "this.getNumArgs() <= 0");
        }

        if ( this.mveID != oldMatrix.mveID )
        {
            throw new SystemErrorException(mName + "mveID mismatch");
        }
        else if ( ( ! cascadeMveMod ) &&
                  ( this.getNumArgs() != oldMatrix.getNumArgs() ) )
        {
            throw new SystemErrorException(mName + "num args mismatch");
        }

        mve = this.lookupMatrixVE(this.mveID);

        if ( mve.getDB() != this.getDB() )
        {
            throw new SystemErrorException(mName +
                                           "mve.getDB() != this.getDB()");
        }
        else if ( mve.getDB() != oldMatrix.getDB() )
        {
            throw new SystemErrorException(mName +
                    "mve.getDB() != oldMatrix.getDB()");
        }

        if ( mve.getNumFormalArgs() != this.getNumArgs() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getNumFormalArgs() != this.getNumArgs()");
        }

        if ( mve.getVarLen() != this.getVarLen() )
        {
            throw new SystemErrorException(mName +
                                 "mve.getVarLen() != this.getValLen()");
        }


        if ( ( cascadeMveMod ) && ( cascadeMveID == this.mveID ) )
        {
            assert( ! cascadeMveDel );
            assert( ! cascadePveMod );
            assert( ! cascadePveDel );

            /* The definition of the mve defining both the old and
             * new versions of the data cell has changed.  Thus it is
             * possible that the formal argument list has changed
             * as well.
             *
             * Verify that each of the arguments in the new predicate
             * match the pve.  Further, for each argument in the new
             * predicate with a valid id, verify that there is an
             * argument in the old predicate with the same id and type.
             */

            i = 0;
            while ( i < this.getNumArgs() )
            {
                // get the i-th formal argument.  This is the mve's actual
                // argument,  so be careful not to modify it in any way.
                fa = mve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( ( fa instanceof QuoteStringFormalArg ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.MATRIX) )
                {
                    throw new SystemErrorException(mName +
                        "non-matrix mve contains a quote string formal arg?!");
                }
                else if ( ( fa instanceof TextStringFormalArg ) &&
                          ( i != 0 ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.TEXT ) )
                {
                    throw new SystemErrorException(mName +
                        "non-text mve contains a text string formal arg?!?!");
                }

                // get the i'th argument from the new argument list, and
                // the matching argument (if any) from the old argument list.
                // Again, these are the actual arguments -- must be
                // careful not to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = null;

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( newArg.getID() != DBIndex.INVALID_ID )
                {
                    // the old argument list must contain an argument
                    // with the same ID.  Scan the list to find it.
                    int j = 0;

                    while ( ( j < oldMatrix.getNumArgs() ) &&
                            ( oldArg == null ) )
                    {
                        oldArg = oldMatrix.argList.get(j);

                        if ( oldArg.getID() == DBIndex.INVALID_ID )
                        {
                            throw new SystemErrorException(mName + i +
                                    "th old argument not in index?!?!");
                        }

                        if ( oldArg.getID() != newArg.getID() )
                        {
                            oldArg = null;
                        }

                        j++;
                    }

                    if ( oldArg == null )
                    {
                        throw new SystemErrorException(mName +
                            "new arg has valid ID but no matching old arg.");
                    }
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                switch (fa.getFargType())
                {
                    case COL_PREDICATE:
                        if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof ColPredDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                  "Type mismatch: Col Predicate(s) expected.");
                        }

                        cpfa = (ColPredFormalArg)fa;
                        new_cpdv = (ColPredDataValue)newArg;

                        if ( oldArg != null )
                        {
                            old_cpdv = (ColPredDataValue)oldArg;
                        }
                        else
                        {
                            old_cpdv = null;
                        }

                        if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_cpdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_cpdv.getItsValue().
                                                validateColumnPredicate(true);
                        }
                        else if ( old_cpdv != null )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName +
                            "new_cpdv has valid ID but old_cpdv is null?!?");
                        }
                        break;

                    case FLOAT:
                        if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof FloatDataValue ) ) ) )
                        {
                           throw new SystemErrorException(mName +
                                    "Type mismatch: float(s) expected.");
                        }

                        ffa = (FloatFormalArg)fa;
                        new_fdv = (FloatDataValue)newArg;

                        if ( new_fdv.getSubRange() != ffa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_fdv.getSubRange() != ffa.getSubRange().");
                        }

                        if ( new_fdv.getSubRange() )
                        {
                            if ( ( ffa.getMinVal() >
                                    new_fdv.getItsValue() ) ||
                                 ( ffa.getMaxVal() <
                                    new_fdv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_fdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case INTEGER:
                        if ( ( ! ( newArg instanceof IntDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof IntDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: integer(s) expected.");
                        }

                        ifa = (IntFormalArg)fa;
                        new_idv = (IntDataValue)newArg;

                        if ( new_idv.getSubRange() != ifa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_idv.getSubRange() != ifa.getSubRange().");
                        }

                        if ( new_idv.getSubRange() )
                        {
                            if ( ( ifa.getMinVal() >
                                    new_idv.getItsValue() ) ||
                                 ( ifa.getMaxVal() <
                                    new_idv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_idv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case NOMINAL:
                        if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof NominalDataValue ) )
                             )
                           )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: nominal(s) expected.");
                        }

                        nfa = (NominalFormalArg)fa;
                        new_ndv = (NominalDataValue)newArg;

                        if ( new_ndv.getSubRange() != nfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_ndv.getSubRange() != nfa.getSubRange().");
                        }

                        if ( ( new_ndv.getSubRange() ) &&
                             ( new_ndv.getItsValue() != null ) )
                        {
                            if ( ! nfa.approved(new_ndv.getItsValue()) )
                            {
                                throw new SystemErrorException(mName +
                                    "new_ndv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case PREDICATE:
                        if ( ( ! ( newArg instanceof PredDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof PredDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Predicate(s) expected.");
                        }

                        pfa = (PredFormalArg)fa;
                        new_pdv = (PredDataValue)newArg;

                        if ( oldArg != null )
                        {
                            old_pdv = (PredDataValue)oldArg;
                        }
                        else
                        {
                            old_pdv = null;
                        }

                        if ( new_pdv.getSubRange() != pfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                             "new_pdv.getSubRange() != pfa.getSubRange().");
                        }

                        if ( ( new_pdv.getItsValue().getPveID() !=
                                DBIndex.INVALID_ID ) &&
                             ( new_pdv.getSubRange() ) &&
                             ( ! pfa.approved(new_pdv.getItsValue().
                                        getPveID()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "new_pdv.getItsValue() out of range.");
                        }

                        if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_pdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_pdv.getItsValue().validatePredicate(true);
                        }
                        else if ( old_pdv != null )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName +
                            "new_pdv has valid ID but old_pdv is null?!?");
                        }
                        break;

                    case TIME_STAMP:
                        if ( ( ! ( newArg instanceof
                                    TimeStampDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      TimeStampDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "Type mismatch: time stamp(s) expected.");
                        }

                        tsfa = (TimeStampFormalArg)fa;
                        new_tsdv = (TimeStampDataValue)newArg;

                        if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                           "new_tsdv.getSubRange() != tsfa.getSubRange().");
                        }

                        if ( new_tsdv.getSubRange() )
                        {
                            if ( ( tsfa.getMinVal().
                                    gt(new_tsdv.getItsValue()) ) ||
                                 ( tsfa.getMaxVal().
                                    lt(new_tsdv.getItsValue()) ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_tsdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case QUOTE_STRING:
                        if ( ( ! ( newArg instanceof
                                    QuoteStringDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      QuoteStringDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: quote string(s) expected.");
                        }
                        break;

                    case TEXT:
                        if ( ( ! ( newArg instanceof
                                    TextStringDataValue ) ) ||
                             ( ( oldArg != null ) &&
                               ( ! ( oldArg instanceof
                                      TextStringDataValue ) ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: text string(s) expected.");
                        }
                        break;

                    case UNTYPED:
                        if ( ( newArg instanceof TextStringDataValue ) ||
                             ( ( oldArg != null ) &&
                               ( oldArg instanceof TextStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "Type mismatch: Text String(s) can't be " +
                                "substituted for untyped arguments.");
                        }
                        else if ( ! ( ( newArg instanceof
                                        ColPredDataValue ) ||
                                      ( newArg instanceof
                                        FloatDataValue ) ||
                                      ( newArg instanceof
                                        IntDataValue ) ||
                                      ( newArg instanceof
                                        NominalDataValue ) ||
                                      ( newArg instanceof
                                        PredDataValue ) ||
                                      ( newArg instanceof
                                        TimeStampDataValue ) ||
                                      ( newArg instanceof
                                        QuoteStringDataValue ) ||
                                      ( newArg instanceof
                                        UndefinedDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Unknown subtype of DataValue");
                        }

                        if ( ( ( oldArg == null )
                               ||
                               ( newArg.getClass() != oldArg.getClass() )
                             )
                             &&
                             ( newArg.getID() != DBIndex.INVALID_ID ) )
                        {
                            throw new SystemErrorException(mName +
                                    "dv type change and id set(2)");
                        }

                        if ( newArg instanceof ColPredDataValue )
                        {
                            new_cpdv = (ColPredDataValue)newArg;

                            if ( ( oldArg != null ) &&
                                 ( oldArg instanceof ColPredDataValue ) )
                            {
                                old_cpdv = (ColPredDataValue)oldArg;

                                assert( cascadeMveMod );

                                new_cpdv.getItsValue().
                                        validateReplacementColPred(
                                            old_cpdv.getItsValueBlind(),
                                            cascadeMveMod,
                                            cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveMod,
                                            cascadePveDel,
                                            cascadePveID);
                            }
                            else
                            {
                                new_cpdv.getItsValue().
                                        validateColumnPredicate(true);
                            }
                        }
                        else if ( newArg instanceof PredDataValue )
                        {
                            new_pdv = (PredDataValue)newArg;

                            if ( ( oldArg != null ) &&
                                 ( oldArg instanceof PredDataValue ) )
                            {
                                old_pdv = (PredDataValue)oldArg;

                                new_pdv.getItsValue().
                                        validateReplacementPredicate(
                                            old_pdv.getItsValueBlind(),
                                            cascadeMveMod,
                                            cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveMod,
                                            cascadePveDel,
                                            cascadePveID);
                            }
                            else
                            {
                                new_pdv.getItsValue().
                                        validatePredicate(true);
                            }
                        }
                        break;

                    case UNDEFINED:
                        throw new SystemErrorException(mName +
                                "formal arg type undefined???");
                        /* break statement commented out to keep the
                         * compiler happy
                         */
                        // break;

                    default:
                        throw new SystemErrorException(mName +

                                "Unknown Formal Arg Type");
                        /* break statement commented out to keep the
                         * compiler happy
                         */
                        // break;
                }

                i++;

            } /* while */
        }
        else // no structural change case -- for this matrix at least
        {
            while ( i < this.getNumArgs() )
            {

                // get the i-th formal argument.  This is mve's actual argument,
                // so be careful not to modify it in any way.
                fa = mve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( ( fa instanceof TextStringFormalArg ) &&
                          ( mve.getType() !=
                            MatrixVocabElement.MatrixType.TEXT ) )
                {
                    throw new SystemErrorException(mName +
                            "non-text mve contains a text formal arg?!?!");
                }

                // get the i'th arguments from the old and new argument lists.
                // Again, these are the actual arguments -- must be careful not
                // to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = oldMatrix.argList.get(i);

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( oldArg == null )
                {
                    throw new SystemErrorException(mName + "no old" + i +
                            "th argument?!?!");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( fa.getID() != oldArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( oldArg.getID() == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + i +
                            "th old argument not in index?!?!");
                }

                if ( ( newArg.getID() != DBIndex.INVALID_ID ) &&
                     ( newArg.getID() != oldArg.getID() ) )
                {
                    throw new SystemErrorException(mName + i +
                            "th argument id mismatch");
                }

                switch (fa.getFargType())
                {
                    case COL_PREDICATE:
                        if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
                             ( ! ( oldArg instanceof ColPredDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                   "Type mismatch: Column Predicate expected.");
                        }

                        cpfa = (ColPredFormalArg)fa;
                        new_cpdv = (ColPredDataValue)newArg;
                        old_cpdv = (ColPredDataValue)oldArg;

                        if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_cpdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_cpdv.getItsValue().
                                                validateColumnPredicate(true);
                        }
                        else if ( ( ! cascadeMveMod ) &&
                                  ( ! cascadeMveDel ) &&
                                  ( ! cascadePveMod ) &&
                                  ( ! cascadePveDel ) )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        break;

                    case FLOAT:
                        if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
                             ( ! ( oldArg instanceof FloatDataValue ) ) )
                        {
                           throw new SystemErrorException(mName +
                                    "Type mismatch: float expected.");
                        }

                        ffa = (FloatFormalArg)fa;
                        new_fdv = (FloatDataValue)newArg;
                        old_fdv = (FloatDataValue)oldArg;

                        if ( new_fdv.getSubRange() != ffa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                 "new_fdv.getSubRange() != ffa.getSubRange().");
                        }

                        if ( new_fdv.getSubRange() )
                        {
                            if ( ( ffa.getMinVal() > new_fdv.getItsValue() ) ||
                                 ( ffa.getMaxVal() < new_fdv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_fdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case INTEGER:
                        if ( ( ! ( newArg instanceof IntDataValue ) ) ||
                             ( ! ( oldArg instanceof IntDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: integer expected.");
                        }

                        ifa = (IntFormalArg)fa;
                        new_idv = (IntDataValue)newArg;
                        old_idv = (IntDataValue)oldArg;

                        if ( new_idv.getSubRange() != ifa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                 "new_idv.getSubRange() != ifa.getSubRange().");
                        }

                        if ( new_idv.getSubRange() )
                        {
                            if ( ( ifa.getMinVal() > new_idv.getItsValue() ) ||
                                 ( ifa.getMaxVal() < new_idv.getItsValue() ) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_idv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case NOMINAL:
                        if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
                             ( ! ( oldArg instanceof NominalDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: nominal expected.");
                        }

                        nfa = (NominalFormalArg)fa;
                        new_ndv = (NominalDataValue)newArg;
                        old_ndv = (NominalDataValue)oldArg;

                        if ( new_ndv.getSubRange() != nfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                 "new_ndv.getSubRange() != nfa.getSubRange().");
                        }

                        if ( ( new_ndv.getSubRange() ) &&
                             ( new_ndv.getItsValue() != null ) )
                        {
                            if ( ! nfa.approved(new_ndv.getItsValue()) )
                            {
                                throw new SystemErrorException(mName +
                                        "new_ndv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case PREDICATE:
                        if ( ( ! ( newArg instanceof PredDataValue ) ) ||
                             ( ! ( oldArg instanceof PredDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Predicate expected.");
                        }

                        pfa = (PredFormalArg)fa;
                        new_pdv = (PredDataValue)newArg;
                        old_pdv = (PredDataValue)oldArg;

                        if ( new_pdv.getSubRange() != pfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                                "new_pdv.getSubRange() != pfa.getSubRange().");
                        }

                        if ( ( new_pdv.getItsValue().getPveID() !=
                                DBIndex.INVALID_ID ) &&
                             ( new_pdv.getSubRange() ) &&
                             ( ! pfa.approved(new_pdv.
                                              getItsValue().getPveID()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "new_pdv.getItsValue() out of range.");
                        }

                        if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
                             ( new_pdv.getItsValue().getID() ==
                                DBIndex.INVALID_ID ) )
                        {
                            new_pdv.getItsValue().validatePredicate(true);
                        }
                        else if ( ( ! cascadeMveMod ) &&
                                  ( ! cascadeMveDel ) &&
                                  ( ! cascadePveMod ) &&
                                  ( ! cascadePveDel ) )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        break;

                    case TIME_STAMP:
                        if ( ( ! ( newArg instanceof TimeStampDataValue ) ) ||
                             ( ! ( oldArg instanceof TimeStampDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: time stamp expected.");
                        }

                        tsfa = (TimeStampFormalArg)fa;
                        new_tsdv = (TimeStampDataValue)newArg;
                        old_tsdv = (TimeStampDataValue)oldArg;

                        if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
                        {
                           throw new SystemErrorException(mName +
                               "new_tsdv.getSubRange() != tsfa.getSubRange().");
                        }

                        if ( new_tsdv.getSubRange() )
                        {
                            if ((tsfa.getMinVal().gt(new_tsdv.getItsValue()))
                               || (tsfa.getMaxVal().lt(new_tsdv.getItsValue())))
                            {
                                throw new SystemErrorException(mName +
                                        "new_tsdv.getItsValue() out of range.");
                            }
                        }
                        break;

                    case QUOTE_STRING:
                        if ( ( ! ( newArg instanceof QuoteStringDataValue ) ) ||
                             ( ! ( oldArg instanceof QuoteStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: quote string expected.");
                        }
                        break;

                    case TEXT:
                        if ( ( ! ( newArg instanceof TextStringDataValue ) ) ||
                             ( ! ( oldArg instanceof TextStringDataValue ) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: text string expected.");
                        }
                        break;

                    case UNTYPED:
                        if ( ( newArg instanceof TextStringDataValue ) ||
                             ( oldArg instanceof TextStringDataValue ) )
                        {
                            throw new SystemErrorException(mName +
                                    "Type mismatch: Text String can't be " +
                                    "substituted for untyped arguments.");
                        }
                        else if ( ! ((newArg instanceof ColPredDataValue) ||
                                     (newArg instanceof FloatDataValue) ||
                                     (newArg instanceof IntDataValue) ||
                                     (newArg instanceof NominalDataValue) ||
                                     (newArg instanceof PredDataValue) ||
                                     (newArg instanceof TimeStampDataValue) ||
                                     (newArg instanceof QuoteStringDataValue) ||
                                     (newArg instanceof UndefinedDataValue)))
                        {
                            throw new SystemErrorException(mName +
                                    "Unknown subtype of DataValue");
                        }

                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
                             ( newArg.getID() != DBIndex.INVALID_ID ) )
                        {
                            throw new SystemErrorException(mName +
                                    "dv type change and id set");
                        }

                        if ( newArg instanceof ColPredDataValue )
                        {
                            new_cpdv = (ColPredDataValue)newArg;

                            if ( oldArg instanceof ColPredDataValue )
                            {
                                old_cpdv = (ColPredDataValue)oldArg;

                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                                     ( cascadePveMod ) || ( cascadePveDel ) )
                                {
                                    new_cpdv.getItsValue().
                                            validateReplacementColPred(
                                                old_cpdv.getItsValueBlind(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                                else
                                {
                                    new_cpdv.getItsValue().
                                            validateReplacementColPred(
                                                old_cpdv.getItsValue(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                            }
                            else
                            {
                                new_cpdv.getItsValue().
                                        validateColumnPredicate(true);
                            }
                        }
                        else if ( newArg instanceof PredDataValue )
                        {
                            new_pdv = (PredDataValue)newArg;

                            if ( oldArg instanceof PredDataValue )
                            {
                                old_pdv = (PredDataValue)oldArg;

                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                                     ( cascadePveMod ) || ( cascadePveDel ) )
                                {
                                    new_pdv.getItsValue().
                                            validateReplacementPredicate(
                                                old_pdv.getItsValueBlind(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                                else
                                {
                                    new_pdv.getItsValue().
                                            validateReplacementPredicate(
                                                old_pdv.getItsValue(),
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);
                                }
                            }
                            else
                            {
                                new_pdv.getItsValue().
                                        validatePredicate(true);
                            }
                        }
                        break;

                    case UNDEFINED:
                        throw new SystemErrorException(mName +
                                "formal arg type undefined???");
                        /* break statement commented to keep compiler happy */
                        // break;

                    default:
                        throw new SystemErrorException(mName +
                                                    "Unknown Formal Arg Type");
                        /* break statement commented to keep compiler happy */
                        // break;
                }

                i++;

            } /* while */
        }

        return;

    } /* Matrix::validateReplacementMatrix() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Several versions of this class method, all with the objective of
     * constructing instances of Matrix.
     *
     * Returns a reference to the newly constructed matrix if successful.
     * Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0)";
        Matrix m = null;

        m = new Matrix(db, mveID);

        if ( arg0 != null )
        {
            m.replaceArg(0, arg0);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0, arg1)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0);

        if ( arg1 != null )
        {
            m.replaceArg(1, arg1);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0, arg1, arg2)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0, arg1);

        if ( arg2 != null )
        {
            m.replaceArg(2, arg2);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2,
                                   DataValue arg3)
        throws SystemErrorException
    {
        final String mName =
                "Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0, arg1, arg2);

        if ( arg3 != null )
        {
            m.replaceArg(3, arg3);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2,
                                   DataValue arg3,
                                   DataValue arg4)
        throws SystemErrorException
    {
        final String mName =
                "Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0, arg1, arg2, arg3);

        if ( arg4 != null )
        {
            m.replaceArg(4, arg4);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2,
                                   DataValue arg3,
                                   DataValue arg4,
                                   DataValue arg5)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0, arg1, " +
                                                "arg2, arg3, arg4, arg5)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0, arg1, arg2, arg3, arg4);

        if ( arg5 != null )
        {
            m.replaceArg(5, arg5);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5) */


    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2,
                                   DataValue arg3,
                                   DataValue arg4,
                                   DataValue arg5,
                                   DataValue arg6)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0, arg1, " +
                                                "arg2, arg3, arg4, arg5, arg6)";
        Matrix m = null;

        m = Matrix.Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5);

        if ( arg6 != null )
        {
            m.replaceArg(6, arg6);
        }

        return m;

    }

    public static Matrix Construct(Database db,
                                   long mveID,
                                   DataValue arg0,
                                   DataValue arg1,
                                   DataValue arg2,
                                   DataValue arg3,
                                   DataValue arg4,
                                   DataValue arg5,
                                   DataValue arg6,
                                   DataValue arg7)
        throws SystemErrorException
    {
        final String mName = "Matrix::Construct(db, mveID, arg0, arg1, " +
                                          "arg2, arg3, arg4, arg5, arg6, arg7)";
        Matrix m = null;

        m = Matrix
                .Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5, arg6);

        if ( arg7 != null )
        {
            m.replaceArg(7, arg7);
        }

        return m;

    }

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = HashUtils.Long2H(mveID) * Constants.SEED1;
        hash += (HashUtils.Obj2H(argList)) * Constants.SEED2;
        hash += (varLen ? 1 : 0) * Constants.SEED3;

        return hash;
    }

    /**
     * Compares this Matrix against another object.
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
        Matrix m = (Matrix) obj;
        return (mveID == m.mveID)
            && (varLen == m.varLen)
            && (argList == null ? m.argList == null
                                : argList.equals(m.argList));
    }

}
