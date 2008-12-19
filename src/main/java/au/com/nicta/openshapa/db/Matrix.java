/*
 * Matrix.java
 *
 * Created on August 29, 2007, 6:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

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
 *                                                  JRM -- 8/19/07
 *
 * @author mainzer
 */
public class Matrix
{

    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/

    /**
     * expectedResult:  Private enumerated type used to specify the expected
     *      result of a test.
     */

    private enum expectedResult
        {succeed, system_error, return_null};

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

    /** Database containing the Matrix */
    Database db = null;

    /** ID of the represented Matrix */
    protected long mveID = DBIndex.INVALID_ID;

    /** Argument list of the Matrix */
    protected Vector<DataValue> argList = null;

    /** Whether the Matrix has a variable length argument list */
    protected boolean varLen = false;



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
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public Matrix(Database db,
                  long mveID)
        throws SystemErrorException
    {
        super();

        final String mName = "Matrix::Matrix(db, matrixID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if ( ( db == null ) ||
             ( ! ( db instanceof Database ) ) )
        {
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

        if ( ( db == null ) ||
             ( ! ( db instanceof Database ) ) )
        {
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


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getDB()
     *
     * Return the current value of the db field.
     *
     *                          JRM -- 8/23/07
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
     *                          JRM -- 8/23/07
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
     *                          JRM -- 8/23/07
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
     *                                          JRM -- 8/23/07
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
                throw new SystemErrorException(mName + "argList unitialized?!?!");
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
     *                                          JRM -- 8/23/07
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
        String s;

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            s = "()";
        }
        else
        {
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

            s = new String("(");

            while ( i < (numArgs - 1) )
            {
                s += this.getArg(i).toString() + ", ";
                i++;
            }

            s += getArg(i).toString();

            s += ")";
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
     *                                              JRM -- 2/19/08
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
     *                                              JRM -- 8/20/07
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
     *                                              JRM -- 2/19/08
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
    public String toString()
    {
        String s;

        try
        {
            s = this.argListToString();
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
     *                                              JRM -- 4/6/08
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

            cdv = DataValue.Copy(dv, true);

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
     *                                              JRM -- 8/20/07
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

        if ( ! ( mve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + "mve not a matrixVE");
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
     *                                              JRM -- 8/20/07
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
                    if ( dv instanceof FloatDataValue )
                    {
                        cdv = new FloatDataValue((FloatDataValue)dv);
                    }
                    else if ( dv instanceof IntDataValue )
                    {
                        cdv = new IntDataValue((IntDataValue)dv);
                    }
                    else if ( dv instanceof NominalDataValue )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( dv instanceof PredDataValue )
                    {
                        cdv = new PredDataValue((PredDataValue)dv);
                    }
                    else if ( dv instanceof TimeStampDataValue )
                    {
                        cdv = new TimeStampDataValue((TimeStampDataValue)dv);
                    }
                    else if ( dv instanceof QuoteStringDataValue )
                    {
                        cdv = new QuoteStringDataValue((QuoteStringDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( dv instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "Text String(s) can't be " +
                                "substituted for untyped arguments.");
                    }
                    else
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
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - Modified method to work with column predicates as well.
     *
     *                                              JRM -- 8/31/08
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
     *                                      JRM -- 8/23/07
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
     *                                      JRM -- 5/23/08
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
            argCopy = DataValue.Copy(arg, false);
        }

        return argCopy;

    } /* Matrix::getArgCopy() */


    /**
     * getNumArgs()
     *
     * Return the number of arguments.  Return 0 if the predID hasn't been
     * specified yet.
     *                                      JRM -- 8/23/07
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
                throw new SystemErrorException(mName + "argList unitialized?!?!");
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
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - Modified to work with column predicates as well.
     *
     *                                              JRM -- 8/31/08
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
     *                                              JRM -- 8/23/07
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
     *                                          JRM -- 8/26/08
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
     *                                          JRM -- 8/26/08
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
                    if ( cpdv.getItsFargType() == FormalArgument.fArgType.UNTYPED )
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
                                    "mveID doesn't refer to a Matrix vocab element");
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
                              FormalArgument.fArgType.COL_PREDICATE )
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
     *                                          JRM -- 3/23/08
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
     *                                          JRM -- 3/23/08
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
                    if ( dv.getItsFargType() == FormalArgument.fArgType.UNTYPED )
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
                                    "mveID doesn't refer to a Matrix vocab element");
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
                    else if ( dv.getItsFargType() == FormalArgument.fArgType.PREDICATE )
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
     *                                      JRM -- 2/20/08
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
                            new_cpdv.getItsValue().validateColumnPredicate(true);
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
                            new_cpdv.getItsValue().validateColumnPredicate(true);
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
     *                                              JRM -- 2/19/08
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
     *                                              JRM -- 2/19/08
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
     *                                              JRM -- 2/19/08
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
                            new_cpdv.getItsValue().validateColumnPredicate(true);
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

                // get the i'th arguments from the old and new argument lists.
                // Again, these are the actual arguments -- must be careful not to
                // modify them in any way.
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
                            new_cpdv.getItsValue().validateColumnPredicate(true);
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
                            if ( ( tsfa.getMinVal().gt(new_tsdv.getItsValue()) ) ||
                                 ( tsfa.getMaxVal().lt(new_tsdv.getItsValue()) ) )
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
                        /* break statement commented out to keep the compiler happy */
                        // break;

                    default:
                        throw new SystemErrorException(mName +
                                                       "Unknown Formal Arg Type");
                        /* break statement commented out to keep the compiler happy */
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
     *                                              JRM -- 3/31/08
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

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5, arg6) */


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

        m = Matrix.Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5, arg6);

        if ( arg7 != null )
        {
            m.replaceArg(7, arg7);
        }

        return m;

    } /* Matrix::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) */


    /**
     * MatriciesAreLogicallyEqual()
     *
     * Given two instances of Matrix, return true if they contain identical
     * data, and false otherwise.
     *                                              JRM -- 2/7/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean MatriciesAreLogicallyEqual(Matrix m0,
                                                     Matrix m1)
        throws SystemErrorException
    {
        final String mName = "Matrix::MatriciesAreLogicallyEqual()";
        boolean matriciesAreEqual = true;

        if ( ( m0 == null ) || ( m1 == null ) )
        {
            throw new SystemErrorException(mName + ": m0 or m1 null on entry.");
        }

        if ( m0 != m1 )
        {
            if ( ( m0.db != m1.db ) ||
                 ( m0.mveID != m1.mveID ) ||
                 ( m0.varLen != m1.varLen ) )
            {
                matriciesAreEqual = false;
            }

            if ( ( m0.argList == null ) ||
                 ( m1.argList == null ) )
            {
                throw new SystemErrorException(mName +
                        ": m0.argList and/or m1.argList is null.");
            }

            if ( ( matriciesAreEqual ) &&
                 ( m0.argList != m1.argList ) )
            {
                if ( m0.argList.size() != m1.argList.size() )
                {
                    matriciesAreEqual = false;
                }
                else
                {
                    int i = 0;

                    while ( ( i < m0.argList.size() ) &&
                            ( matriciesAreEqual ) )
                    {
                        matriciesAreEqual =
                                DataValue.DataValuesAreLogicallyEqual
                                         (m0.argList.get(i),
                                          m1.argList.get(i));

                        i++;
                    }
                }
            }
        }

        return matriciesAreEqual;

    } /* Matrix::MatriciesAreLogicallyEqual() */


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) Two argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element)  Insert the
     *         mve into the database, and make note of the IDs assigned to them.
     *
     *         Construct a matrix instantiating the mve by passing a reference
     *         to the database and the id of the mve.  Verify that:
     *
     *              matrix.db matches the suplied value
     *              matrix.mveID matches the supplied value
     *              matrix.argList reflects the formal argument list of the mve
     *              matrix.varLen matches the varLen field of the mve.
     *
     *         Do this with mve's of all types (FLOAT, INTEGER, NOMINAL,
     *         PREDICATE, TEXT, and MATRIX) and in the case of mve's of type
     *         MATRIX, with a selection of single entry and a multi-entry
     *         mve's, and with both a fixed length and variable length mve's.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 2) Three argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element) and such
     *         pve's (predicate vocab elements) as are necessary.  Insert the
     *         mve (and pve's if present) int the database, and make note of
     *         the IDs assigned to them.
     *
     *         Construct two argument lists with values and length matching the
     *         argument list of the mve.  In one arg list, assign fargIDs, in
     *         the other, leave the fargID set to INVALID_ID.
     *
     *         Using the above arg lists, construct two marticies, passing the
     *         db, the ID of the mve, and one of the arg list to the
     *         constructor.
     *
     *         Verify that:
     *
     *              matrix.db matches the suplied value
     *              matrix.mveID matches the supplied value
     *              matrix.argList reflects both the formal argument list of
     *                  the mve and the supplied argument list.
     *              matrix.varLen matches the varLen field of the mve.
     *
     *         Do this with mve's of all types (FLOAT, INTEGER, NOMINAL,
     *         PREDICATE, TEXT, and MATRIX) and in the case of mve's of type
     *         MATRIX, with a selection of single entry and a multi-entry
     *         mve's, and with both a fixed length and variable length mve's.
     *
     *      b) Verify that the constructor fails when passed an invalid db,
     *         an invalid mve id, or an invalid argument list.  Note that
     *         we must test argument lists that are null, too short, too long,
     *         and which contain type and fargID mis-matches.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element) and such
     *         pve's (predicate vocab elements) as are necessary.  Insert the
     *         mve (and pve's if present) int the database, and make note of
     *         the IDs assigned to them.
     *
     *         Construct an argument lists with values and length matching the
     *         argument list of the mve.
     *
     *         Using the above arg lists, construct a martix, passing the
     *         db, the ID of the mve, and the arg list to the constructor.
     *         Similarly, construct and empty matrix, passing only the db and
     *         the ID of the mve to the constructor.
     *
     *         Now, using the copy constructor, construct copies of these two
     *         matricies.  Verify that the copies are correct.
     *
     *         Do this with mve's of all types (FLOAT, INTEGER, NOMINAL,
     *         PREDICATE, TEXT, and MATRIX) and in the case of mve's of type
     *         MATRIX, with a selection of single entry and a multi-entry
     *         mve's, and with both a fixed length and variable length mve's.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getMveID(), getDB(), getNumArgs(), and
     *      getVarLen() methods perform correctly.
     *
     *      Do this by creating a database and a selection of predicate vocab
     *      elements.  Then create a selection of predicates, and verify that
     *      get methods return the expected values.
     *
     *      lookupMatrixVE() is an internal method that has been exercised
     *      already.  Verify that it fails on invalid input.
     *
     * 6) ArgList management:
     *
     *      Verify that the getArg() and replaceArg() methods perform as
     *      expected.  Verify that replaceArg() fails on all type mismatches.
     *
     *      Verify that getArg() and replaceArg() methods fail on invalid
     *      input.
     *
     * 7) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/


    /**
     * TestClassMatrix()
     *
     * Main routine for tests of class Matrix.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassMatrix(java.io.PrintStream outStream,
                                          boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class Matrix:\n");

        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test3ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestArgListManagement(outStream, verbose) )
        {
            failures++;
        }

        // TODO:  Add test for validateMatrix

        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class Matrix.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class Matrix.\n\n");
        }

        return pass;

    } /* Matrix::TestClassMatrix() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 2 argument constructor for class Matrix                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        Matrix float_matrix = null;
        Matrix int_matrix = null;
        Matrix matrix_matrix0 = null;
        Matrix matrix_matrix1 = null;
        Matrix matrix_matrix2 = null;
        Matrix nominal_matrix = null;
        Matrix pred_matrix = null;
        Matrix text_matrix = null;
        Matrix m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.
        if ( failures == 0 )
        {
            String float_matrix_string = "(0.0)";
            String int_matrix_string = "(0)";
            String matrix_matrix0_string =
                    "(0.0, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String matrix_matrix1_string = "(<arg1>, <arg2>, <arg3>)";
            String matrix_matrix2_string = "(<arg1>)";
            String nominal_matrix_string = "()";
            String pred_matrix_string = "(())";
            String text_matrix_string = "()";
            String float_matrix_DBstring =
                    "(Matrix (mveID 1) " +
                            "(varLen false) " +
                            "(argList ((FloatDataValue (id 0) " +
                                                      "(itsFargID 2) " +
                                                      "(itsFargType FLOAT) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 0.0) " +
                                                      "(subRange false) " +
                                                      "(minVal 0.0) " +
                                                      "(maxVal 0.0))))))";
            String int_matrix_DBstring =
                    "(Matrix (mveID 7) " +
                            "(varLen false) " +
                            "(argList ((IntDataValue (id 0) " +
                                                    "(itsFargID 8) " +
                                                    "(itsFargType INTEGER) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue 0) " +
                                                    "(subRange false) " +
                                                    "(minVal 0) " +
                                                    "(maxVal 0))))))";
            String matrix_matrix0_DBstring =
                    "(Matrix (mveID 13) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 14) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 15) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 16) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 17) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 18) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:00:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String matrix_matrix1_DBstring =
                    "(Matrix (mveID 31) " +
                            "(varLen false) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 32) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg2>) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 34) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg3>) " +
                                    "(subRange false))))))";
            String matrix_matrix2_DBstring =
                    "(Matrix (mveID 41) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 42) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String nominal_matrix_DBstring =
                    "(Matrix (mveID 47) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 48) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))";
            String pred_matrix_DBstring =
                    "(Matrix (mveID 53) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 54) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))";
            String text_matrix_DBstring =
                    "(Matrix (mveID 59) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 60) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_matrix = new Matrix(db, float_mve_ID);
                int_matrix = new Matrix(db, int_mve_ID);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID);
                nominal_matrix = new Matrix(db, nominal_mve_ID);
                pred_matrix = new Matrix(db, pred_mve_ID);
                text_matrix = new Matrix(db, text_mve_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_matrix == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix == null )
                    {
                        outStream.printf("allocation of float_matrix failed.\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf("allocation of int_matrix failed.\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed.\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed.\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed.\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf("allocation of nominal_matrix failed.\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf("allocation of pred_matrix failed.\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf("allocation of text_matrix failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString(): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString(): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString(): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString(): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString(): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString(): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString(): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString(): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString(): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString(): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString(): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString(): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString(): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString(): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString(): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString(): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        m0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            m0 = new Matrix(null, float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( m0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( m0 != null )
                {
                    outStream.print(
                            "\"new Matrix(null, float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print(
                            "\"new Matrix(null, float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Matrix(null, float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */

        m0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            m0 = new Matrix(new ODBCDatabase(), float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( m0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( m0 != null )
                {
                    outStream.print("new Matrix(new ODBCDatabase(), " +
                                    "float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new Matrix(new ODBCDatabase(), " +
                                    "float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new Matrix(new ODBCDatabase(), float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test3ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 3 argument constructor for class Matrix                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_matrix_arg_list = null;
        Vector<DataValue> float_matrix_arg_list1 = null;
        Vector<DataValue> int_matrix_arg_list = null;
        Vector<DataValue> int_matrix_arg_list1 = null;
        Vector<DataValue> matrix_matrix0_arg_list = null;
        Vector<DataValue> matrix_matrix0_arg_list1 = null;
        Vector<DataValue> matrix_matrix1_arg_list = null;
        Vector<DataValue> matrix_matrix1_arg_list1 = null;
        Vector<DataValue> matrix_matrix2_arg_list = null;
        Vector<DataValue> matrix_matrix2_arg_list1 = null;
        Vector<DataValue> nominal_matrix_arg_list = null;
        Vector<DataValue> nominal_matrix_arg_list1 = null;
        Vector<DataValue> pred_matrix_arg_list = null;
        Vector<DataValue> pred_matrix_arg_list1 = null;
        Vector<DataValue> text_matrix_arg_list = null;
        Vector<DataValue> text_matrix_arg_list1 = null;
        Vector<DataValue> quote_string_arg_list = null;
        Matrix float_matrix = null;
        Matrix int_matrix = null;
        Matrix matrix_matrix0 = null;
        Matrix matrix_matrix1 = null;
        Matrix matrix_matrix2 = null;
        Matrix nominal_matrix = null;
        Matrix pred_matrix = null;
        Matrix text_matrix = null;
        Matrix m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_matrix_string = "(11.0)";
            String int_matrix_string = "(22)";
            String matrix_matrix0_string = "(1.0, 2, a_nominal, " +
                                           "pve0(<arg1>, <arg2>), " +
                                           "\"q-string\", 00:00:01:000, " +
                                           "<untyped>)";
            String matrix_matrix1_string = "(\" a q string \", <arg2>, 88)";
            String matrix_matrix2_string = "(<arg1>)";
            String nominal_matrix_string = "(another_nominal)";
            String pred_matrix_string = "(pve0(<arg1>, <arg2>))";
            String text_matrix_string = "(a text string)";
            String float_matrix_DBstring =
                    "(Matrix (mveID 4) " +
                            "(varLen false) " +
                            "(argList ((FloatDataValue (id 0) " +
                                                      "(itsFargID 5) " +
                                                      "(itsFargType FLOAT) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 11.0) " +
                                                      "(subRange false) " +
                                                      "(minVal 0.0) " +
                                                      "(maxVal 0.0))))))";
            String int_matrix_DBstring =
                    "(Matrix (mveID 10) " +
                            "(varLen false) " +
                            "(argList ((IntDataValue (id 0) " +
                                                    "(itsFargID 11) " +
                                                    "(itsFargType INTEGER) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue 22) " +
                                                    "(subRange false) " +
                                                    "(minVal 0) " +
                                                    "(maxVal 0))))))";
            String matrix_matrix0_DBstring =
                    "(Matrix (mveID 16) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 17) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 1.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 18) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 2) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 21) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue q-string) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 22) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:01:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 23) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String matrix_matrix1_DBstring =
                    "(Matrix (mveID 34) " +
                            "(varLen false) " +
                            "(argList " +
                                "((QuoteStringDataValue (id 0) " +
                                    "(itsFargID 35) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue  a q string ) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 36) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg2>) " +
                                    "(subRange false)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 37) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 88) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String matrix_matrix2_DBstring =
                    "(Matrix (mveID 44) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 45) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String nominal_matrix_DBstring =
                    "(Matrix (mveID 50) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 51) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue another_nominal) " +
                                    "(subRange false))))))";
            String pred_matrix_DBstring =
                    "(Matrix (mveID 56) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 57) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false))))))";
            String text_matrix_DBstring =
                    "(Matrix (mveID 62) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 63) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_matrix_arg_list = new Vector<DataValue>();
                fargID = float_mve.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_matrix_arg_list.add(arg);
                float_matrix = new Matrix(db, float_mve_ID,
                                          float_matrix_arg_list);


                int_matrix_arg_list = new Vector<DataValue>();
                fargID = int_mve.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_matrix_arg_list.add(arg);
                int_matrix = new Matrix(db, int_mve_ID, int_matrix_arg_list);


                matrix_matrix0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_matrix0_arg_list.add(arg);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID,
                                            matrix_matrix0_arg_list);


                matrix_matrix1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getFormalArg(0).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(1).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(2).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_matrix1_arg_list.add(arg);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID,
                                            matrix_matrix1_arg_list);


                matrix_matrix2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getFormalArg(0).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_matrix2_arg_list.add(arg);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID,
                                            matrix_matrix2_arg_list);


                nominal_matrix_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getFormalArg(0).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_matrix_arg_list.add(arg);
                nominal_matrix = new Matrix(db, nominal_mve_ID,
                                            nominal_matrix_arg_list);


                pred_matrix_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getFormalArg(0).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_matrix_arg_list.add(arg);
                pred_matrix = new Matrix(db, pred_mve_ID, pred_matrix_arg_list);


                text_matrix_arg_list = new Vector<DataValue>();
                fargID = text_mve.getFormalArg(0).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_matrix_arg_list.add(arg);
                text_matrix = new Matrix(db, text_mve_ID, text_matrix_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_matrix_arg_list == null ) ||
                 ( float_matrix == null ) ||
                 ( int_matrix_arg_list == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0_arg_list == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1_arg_list == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2_arg_list == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix_arg_list == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix_arg_list == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix_arg_list == null ) ||
                 ( text_matrix == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_matrix_arg_list failed.\n");
                    }

                    if ( float_matrix == null )
                    {
                        outStream.printf("allocation of float_matrix failed.\n");
                    }

                    if ( int_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_matrix_arg_list failed.\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf("allocation of int_matrix failed.\n");
                    }

                    if ( matrix_matrix0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix0_arg_list failed.\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed.\n");
                    }

                    if ( matrix_matrix1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix1_arg_list failed.\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed.\n");
                    }

                    if ( matrix_matrix2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix2_arg_list failed.\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed.\n");
                    }

                    if ( nominal_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_matrix_arg_list failed.\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of nominal_matrix failed.\n");
                    }

                    if ( pred_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_matrix_arg_list failed.\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf("allocation of pred_matrix failed.\n");
                    }

                    if ( text_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_matrix_arg_list failed.\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf("allocation of text_matrix failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString(): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString(): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString(): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString(): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString(): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString(): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString(): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString(): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString(): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString(): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString(): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString(): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString(): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString(): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString(): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString(): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }

            /* Now repeat the above test, only without setting the fargIDs
             * on the entries in the argument list passed to the constructor.
             */
            float_matrix_arg_list1 = null;
            float_matrix = null;
            int_matrix_arg_list1 = null;
            int_matrix = null;
            matrix_matrix0_arg_list1 = null;
            matrix_matrix0 = null;
            matrix_matrix1_arg_list1 = null;
            matrix_matrix1 = null;
            matrix_matrix2_arg_list1 = null;
            matrix_matrix2 = null;
            nominal_matrix_arg_list1 = null;
            nominal_matrix = null;
            pred_matrix_arg_list1 = null;
            pred_matrix = null;
            text_matrix_arg_list1 = null;
            text_matrix = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_matrix_arg_list1 = new Vector<DataValue>();
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(11.0);
                float_matrix_arg_list1.add(arg);
                float_matrix = new Matrix(db, float_mve_ID,
                                          float_matrix_arg_list1);


                int_matrix_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(22);
                int_matrix_arg_list1.add(arg);
                int_matrix = new Matrix(db, int_mve_ID, int_matrix_arg_list1);


                matrix_matrix0_arg_list1 = new Vector<DataValue>();
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(1.0);
                matrix_matrix0_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(2);
                matrix_matrix0_arg_list1.add(arg);
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("a_nominal");
                matrix_matrix0_arg_list1.add(arg);
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                matrix_matrix0_arg_list1.add(arg);
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue("q-string");
                matrix_matrix0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                                             new TimeStamp(db.getTicks(), 60));
                matrix_matrix0_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_matrix0_arg_list1.add(arg);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID,
                                            matrix_matrix0_arg_list1);


                matrix_matrix1_arg_list1 = new Vector<DataValue>();
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue(" a q string ");
                matrix_matrix1_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_matrix1_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(88);
                matrix_matrix1_arg_list1.add(arg);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID,
                                            matrix_matrix1_arg_list1);


                matrix_matrix2_arg_list1 = new Vector<DataValue>();
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_matrix2_arg_list1.add(arg);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID,
                                            matrix_matrix2_arg_list1);


                nominal_matrix_arg_list1 = new Vector<DataValue>();
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("another_nominal");
                nominal_matrix_arg_list1.add(arg);
                nominal_matrix = new Matrix(db, nominal_mve_ID,
                                            nominal_matrix_arg_list1);


                pred_matrix_arg_list1 = new Vector<DataValue>();
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                pred_matrix_arg_list1.add(arg);
                pred_matrix = new Matrix(db, pred_mve_ID, pred_matrix_arg_list1);


                text_matrix_arg_list1 = new Vector<DataValue>();
                arg = new TextStringDataValue(db);
                ((TextStringDataValue)arg).setItsValue("a text string");
                text_matrix_arg_list1.add(arg);
                text_matrix = new Matrix(db, text_mve_ID, text_matrix_arg_list1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_matrix_arg_list1 == null ) ||
                 ( float_matrix == null ) ||
                 ( int_matrix_arg_list1 == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0_arg_list1 == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1_arg_list1 == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2_arg_list1 == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix_arg_list1 == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix_arg_list1 == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix_arg_list1 == null ) ||
                 ( text_matrix == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of float_matrix_arg_list failed(2).\n");
                    }

                    if ( float_matrix == null )
                    {
                        outStream.printf(
                                "allocation of float_matrix failed(2).\n");
                    }

                    if ( int_matrix_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of int_matrix_arg_list failed(2).\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf(
                                "allocation of int_matrix failed(2).\n");
                    }

                    if ( matrix_matrix0_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_matrix0_arg_list failed(2).\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed(2).\n");
                    }

                    if ( matrix_matrix1_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_matrix1_arg_list failed(2).\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed(2).\n");
                    }

                    if ( matrix_matrix2_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_matrix2_arg_list failed(2).\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed(2).\n");
                    }

                    if ( nominal_matrix_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "nominal_matrix_arg_list failed(2).\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of nominal_matrix failed(2).\n");
                    }

                    if ( pred_matrix_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "pred_matrix_arg_list failed(2).\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf(
                                "allocation of pred_matrix failed(2).\n");
                    }

                    if ( text_matrix_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "text_matrix_arg_list failed(2).\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf(
                                "allocation of text_matrix failed(2).\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test matricies " +
                                        "failed to complete(2).");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("matrix creation threw a " +
                                "SystemErrorException(2): %s.\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString()(2): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString()(2): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString()(2): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString()(2): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString()(2): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString()(2): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString()(2): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString()(2): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString()(2): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString()(2): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString()(2): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString()(2): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString()(2): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString()(2): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString()(2): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString()(2): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        failures += Verify3ArgConstructorFailure(null,
                                                 float_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "null",
                                                 "float_mve_ID",
                                                 "float_matrix_arg_list");

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */
        failures += Verify3ArgConstructorFailure(new ODBCDatabase(),
                                                 float_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "new ODBCDatabase()",
                                                 "float_mve_ID",
                                                 "float_matrix_arg_list");



        // finally, verify that the constructor fails when passed an invalid
        // arg list.  Many possibilities...
        //
        // In the following, we do the obvious and try to construct instances
        // of all the mve's defined above, but using all the wrong arg lists.
        // All these attempts should fail when the farg ID mis-matches are
        // detected.
        //
        // In theory, there is also the possiblility of a type mis-match
        // between the formal argument and a datavalue in the argument list.
        // However, the datavalues should throw a system error if a datavalue
        // is created for a formal argument that doesn't match the type of that
        // formarl argument.
        //
        // Even with this, one could suppose that an datavalue was created,
        // and then the type of the formal argument was changed out from under
        // it.  However, in this case, we should be assigning a new ID to the
        // formal argument, causing a farg ID mismatch failure.
        //
        // Assuming we do our part in the rest of the library, the following
        // tests should be sufficient.
        //
        // Start with a float mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose an int mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose a 7 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "quote_string_arg_list");


        // Now choose a 3 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "quote_string_arg_list");


        // Now choose a 1 argument matrix mve as the target.  Since its only
        // argument is untyped, one would expect few possible failures.
        // However in this case, the farg IDs don't match, so we get the
        // usual failures.
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "quote_string_arg_list");


        // Now choose a nominal mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a predicate mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 text_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "text_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a text mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 float_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "float_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 int_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "int_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_matrix0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_matrix0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_matrix1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_matrix1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_matrix2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_matrix2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 nominal_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "nominal_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 pred_matrix_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "pred_matrix_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "quote_string_arg_list");

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class Matrix accessors                                   ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_matrix_arg_list = null;
        Vector<DataValue> int_matrix_arg_list = null;
        Vector<DataValue> matrix_matrix0_arg_list = null;
        Vector<DataValue> matrix_matrix1_arg_list = null;
        Vector<DataValue> matrix_matrix2_arg_list = null;
        Vector<DataValue> nominal_matrix_arg_list = null;
        Vector<DataValue> pred_matrix_arg_list = null;
        Vector<DataValue> text_matrix_arg_list = null;
        Vector<DataValue> quote_string_arg_list = null;
        Matrix float_matrix = null;
        Matrix int_matrix = null;
        Matrix matrix_matrix0 = null;
        Matrix matrix_matrix1 = null;
        Matrix matrix_matrix2 = null;
        Matrix nominal_matrix = null;
        Matrix pred_matrix = null;
        Matrix text_matrix = null;
        Matrix m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // having set up a selection of test mve's, now allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_matrix_string = "(11.0)";
            String int_matrix_string = "(22)";
            String matrix_matrix0_string = "(1.0, 2, a_nominal, " +
                                           "pve0(<arg1>, <arg2>), " +
                                           "\"q-string\", 00:00:01:000, " +
                                           "<untyped>)";
            String matrix_matrix1_string = "(\" a q string \", <arg2>, 88)";
            String matrix_matrix2_string = "(<arg1>)";
            String nominal_matrix_string = "(another_nominal)";
            String pred_matrix_string = "(pve0(<arg1>, <arg2>))";
            String text_matrix_string = "(a text string)";
            String float_matrix_DBstring =
                    "(Matrix (mveID 4) " +
                            "(varLen false) " +
                            "(argList ((FloatDataValue (id 0) " +
                                                      "(itsFargID 5) " +
                                                      "(itsFargType FLOAT) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 11.0) " +
                                                      "(subRange false) " +
                                                      "(minVal 0.0) " +
                                                      "(maxVal 0.0))))))";
            String int_matrix_DBstring =
                    "(Matrix (mveID 10) " +
                            "(varLen false) " +
                            "(argList " +
                                "((IntDataValue (id 0) " +
                                    "(itsFargID 11) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 22) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String matrix_matrix0_DBstring =
                    "(Matrix (mveID 16) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 17) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 1.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 18) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 2) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 21) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue q-string) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 22) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:01:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 23) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String matrix_matrix1_DBstring =
                    "(Matrix (mveID 34) " +
                            "(varLen false) " +
                            "(argList " +
                                "((QuoteStringDataValue (id 0) " +
                                "(itsFargID 35) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 0) " +
                                "(itsValue  a q string ) " +
                                "(subRange false)), " +
                            "(UndefinedDataValue (id 0) " +
                                "(itsFargID 36) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 0) " +
                                "(itsValue <arg2>) " +
                                "(subRange false)), " +
                            "(IntDataValue (id 0) " +
                                "(itsFargID 37) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 0) " +
                                "(itsValue 88) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0))))))";
            String matrix_matrix2_DBstring =
                    "(Matrix (mveID 44) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 45) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String nominal_matrix_DBstring =
                    "(Matrix (mveID 50) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 51) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue another_nominal) " +
                                    "(subRange false))))))";
            String pred_matrix_DBstring =
                    "(Matrix (mveID 56) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 57) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false))))))";
            String text_matrix_DBstring =
                    "(Matrix (mveID 62) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 63) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_matrix_arg_list = new Vector<DataValue>();
                fargID = float_mve.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_matrix_arg_list.add(arg);
                float_matrix = new Matrix(db, float_mve_ID,
                                          float_matrix_arg_list);


                int_matrix_arg_list = new Vector<DataValue>();
                fargID = int_mve.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_matrix_arg_list.add(arg);
                int_matrix = new Matrix(db, int_mve_ID, int_matrix_arg_list);


                matrix_matrix0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_matrix0_arg_list.add(arg);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID,
                                            matrix_matrix0_arg_list);


                matrix_matrix1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getFormalArg(0).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(1).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(2).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_matrix1_arg_list.add(arg);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID,
                                            matrix_matrix1_arg_list);


                matrix_matrix2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getFormalArg(0).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_matrix2_arg_list.add(arg);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID,
                                            matrix_matrix2_arg_list);


                nominal_matrix_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getFormalArg(0).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_matrix_arg_list.add(arg);
                nominal_matrix = new Matrix(db, nominal_mve_ID,
                                            nominal_matrix_arg_list);


                pred_matrix_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getFormalArg(0).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_matrix_arg_list.add(arg);
                pred_matrix = new Matrix(db, pred_mve_ID, pred_matrix_arg_list);


                text_matrix_arg_list = new Vector<DataValue>();
                fargID = text_mve.getFormalArg(0).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_matrix_arg_list.add(arg);
                text_matrix = new Matrix(db, text_mve_ID, text_matrix_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_matrix_arg_list == null ) ||
                 ( float_matrix == null ) ||
                 ( int_matrix_arg_list == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0_arg_list == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1_arg_list == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2_arg_list == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix_arg_list == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix_arg_list == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix_arg_list == null ) ||
                 ( text_matrix == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_matrix_arg_list failed.\n");
                    }

                    if ( float_matrix == null )
                    {
                        outStream.printf("allocation of float_matrix failed.\n");
                    }

                    if ( int_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_matrix_arg_list failed.\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf("allocation of int_matrix failed.\n");
                    }

                    if ( matrix_matrix0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix0_arg_list failed.\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed.\n");
                    }

                    if ( matrix_matrix1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix1_arg_list failed.\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed.\n");
                    }

                    if ( matrix_matrix2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix2_arg_list failed.\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed.\n");
                    }

                    if ( nominal_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_matrix_arg_list failed.\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of nominal_matrix failed.\n");
                    }

                    if ( pred_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_matrix_arg_list failed.\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf("allocation of pred_matrix failed.\n");
                    }

                    if ( text_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_matrix_arg_list failed.\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf("allocation of text_matrix failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString(): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString(): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString(): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString(): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString(): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString(): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString(): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString(): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString(): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString(): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString(): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString(): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString(): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString(): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString(): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString(): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }
        }


        // Verify that getDB() works as expected.  There is not much to
        // do here, as the db field is set on creation and never changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_matrix.getDB() != db ) ||
                 ( int_matrix.getDB() != db ) ||
                 ( matrix_matrix0.getDB() != db ) ||
                 ( matrix_matrix1.getDB() != db ) ||
                 ( matrix_matrix2.getDB() != db ) ||
                 ( nominal_matrix.getDB() != db ) ||
                 ( pred_matrix.getDB() != db ) ||
                 ( text_matrix.getDB() != db ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "*_matrix.getDB() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getMveID() works as expected.  There is not much to
        // do here either, as the mveID field is set on creation and never
        // changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_matrix.getMveID() != float_mve_ID ) ||
                 ( int_matrix.getMveID() != int_mve_ID ) ||
                 ( matrix_matrix0.getMveID() != matrix_mve0_ID ) ||
                 ( matrix_matrix1.getMveID() != matrix_mve1_ID ) ||
                 ( matrix_matrix2.getMveID() != matrix_mve2_ID ) ||
                 ( nominal_matrix.getMveID() != nominal_mve_ID ) ||
                 ( pred_matrix.getMveID() != pred_mve_ID ) ||
                 ( text_matrix.getMveID() != text_mve_ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_matrix.getMveID() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getNumArgs() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_matrix.getNumArgs() != 1 ) ||
                 ( int_matrix.getNumArgs() != 1 ) ||
                 ( matrix_matrix0.getNumArgs() != 7 ) ||
                 ( matrix_matrix1.getNumArgs() != 3 ) ||
                 ( matrix_matrix2.getNumArgs() != 1 ) ||
                 ( nominal_matrix.getNumArgs() != 1 ) ||
                 ( pred_matrix.getNumArgs() != 1 ) ||
                 ( text_matrix.getNumArgs() != 1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_matrix.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getVarLen() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_matrix.getVarLen() != false ) ||
                 ( int_matrix.getVarLen() != false ) ||
                 ( matrix_matrix0.getVarLen() != false ) ||
                 ( matrix_matrix1.getVarLen() != false ) ||
                 ( matrix_matrix2.getVarLen() != true ) ||
                 ( nominal_matrix.getVarLen() != false ) ||
                 ( pred_matrix.getVarLen() != false ) ||
                 ( text_matrix.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_matrix.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // finally, verify that lookupMatrixVE() throws a system error on
        // invalid input.  Start with the valid id that does not refer to a
        // matrix vocab element

        threwSystemErrorException = false;
        completed = false;
        fargID = DBIndex.INVALID_ID;
        mve = null;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

                mve = float_matrix.lookupMatrixVE(fargID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (2).\n");
                    }

                    if ( mve != null )
                    {
                        outStream.printf("mve != null (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "float_matrix.lookupMatrixVE(fargID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "float_matrix.lookupPredicateVE(fargID) " +
                                "failed to thow a system error.\n");
                    }
                }
            }
        }

        // now try an unused ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                mve = int_matrix.lookupMatrixVE(500);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "int_matrix.lookupMatrixVE(500) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("int_matrix.lookupMatrixVE(500) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, try the invalid ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                mve = matrix_matrix0.lookupMatrixVE(DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (3)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("matrix_matrix0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("matrix_matrix0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::TestAccessors() */


    /**
     * Verify3ArgConstructorFailure()
     *
     * Verify that the three argument constructor for this class fails with
     * a system error when supplied the given parameters.
     *
     * Return 0 if the constructor fails as expected, and 1 if it does not.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int Verify3ArgConstructorFailure(Database db,
                                                   long mve_id,
                                                   Vector<DataValue> arg_list,
                                                   java.io.PrintStream outStream,
                                                   boolean verbose,
                                                   String db_desc,
                                                   String mve_id_desc,
                                                   String arg_list_desc)
    {
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Matrix m0 = null;

        try
        {
            m0 = new Matrix(db, mve_id, arg_list);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( m0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( m0 != null )
                {
                    outStream.printf("new Matrix(%s, %s, %s) != null.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( completed )
                {
                    outStream.printf("new Matrix(%s, %s, %s) completed.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("new Matrix(%s, %s, %s) didn't throw " +
                                     "a SystemErrorException.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }
            }
        }

        return failures;

    } /* Matrix::Verify3ArgConstructorFailure() */


    /**
     * TestGetArgCopy()
     *
     * Given a matrix, and an argument number, verify that getArgCopy()
     * returns a copy of the target argument if the argNum parameter refers
     * to a parameter, returns null if argNum is greater than the number
     * of parameters, and fails with a system error is argNum is negative.
     *
     * Return the number of failures detected.
     *
     *                                              JRM -- 5/24/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestGetArgCopy(Matrix m,
                                     int argNum,
                                     int testNum,
                                     expectedResult er,
                                     String mName,
                                     java.io.PrintStream outStream,
                                     boolean verbose)
        throws SystemErrorException
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue copy = null;

        try
        {
            copy = m.getArgCopy(argNum);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( argNum < 0 )
        {
            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) completed.\n",
                                         testNum, mName, argNum);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) failed to throw " +
                                "a system error exception.\n",
                                testNum, mName, argNum);
                    }
                }
            }
            else if ( er != expectedResult.system_error )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.system_error.toString());
                }
            }
        }
        else if ( argNum >= m.getNumArgs() )
        {
            if ( ( copy != null ) ||
                 ( ! completed  ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copy != null )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to return null.\n",
                                testNum, mName, argNum);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to completed.\n",
                                testNum, mName, argNum);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "%d: %s.getArgCopy(%d >= numArgs) threw " +
                            "an unexpected system error exception: \"%s\".\n",
                            testNum, mName, argNum, systemErrorExceptionString);
                    }
                }
            }
            else if ( er != expectedResult.return_null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.return_null.toString());
                }
            }
        }
        else
        {
            failures += DataValue.VerifyDVCopy(m.argList.get(argNum),
                                               copy,
                                               outStream,
                                               verbose,
                                               mName + "(" + argNum + ")",
                                               mName + "(" + argNum + ") copy");

            if ( er != expectedResult.succeed )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.succeed.toString());
                }
            }
        }





        return failures;

    } /* Matrix::TestGetArgCopy() */


    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the arg list management facilities.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class Matrix argument list management                    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue goodArg = null;
        DataValue badArg = null;
        FloatDataValue floatArg = null;
        IntDataValue intArg = null;
        NominalDataValue nomArg = null;
        PredDataValue predArg = null;
        QuoteStringDataValue qsArg = null;
        TextStringDataValue textArg = null;
        TimeStampDataValue tsArg = null;
        UndefinedDataValue undefArg = null;
        Vector<DataValue> float_matrix_arg_list = null;
        Vector<DataValue> int_matrix_arg_list = null;
        Vector<DataValue> matrix_matrix0_arg_list = null;
        Vector<DataValue> matrix_matrix1_arg_list = null;
        Vector<DataValue> matrix_matrix2_arg_list = null;
        Vector<DataValue> nominal_matrix_arg_list = null;
        Vector<DataValue> pred_matrix_arg_list = null;
        Vector<DataValue> text_matrix_arg_list = null;
        Matrix float_matrix = null;
        Matrix int_matrix = null;
        Matrix matrix_matrix0 = null;
        Matrix matrix_matrix1 = null;
        Matrix matrix_matrix2 = null;
        Matrix nominal_matrix = null;
        Matrix pred_matrix = null;
        Matrix text_matrix = null;
        Matrix m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_matrix_string = "(11.0)";
            String int_matrix_string = "(22)";
            String matrix_matrix0_string = "(1.0, 2, a_nominal, " +
                                           "pve0(<arg>), " +
                                           "\"q-string\", 00:00:01:000, " +
                                           "<untyped>)";
            String matrix_matrix1_string = "(\" a q string \", <arg2>, 88)";
            String matrix_matrix2_string = "(<arg1>)";
            String nominal_matrix_string = "(another_nominal)";
            String pred_matrix_string = "(pve0(<arg>))";
            String text_matrix_string = "(a text string)";
            String float_matrix_DBstring =
                    "(Matrix (mveID 6) " +
                            "(varLen false) " +
                            "(argList ((FloatDataValue (id 0) " +
                                                      "(itsFargID 7) " +
                                                      "(itsFargType FLOAT) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 11.0) " +
                                                      "(subRange false) " +
                                                      "(minVal 0.0) " +
                                                      "(maxVal 0.0))))))";
            String int_matrix_DBstring =
                    "(Matrix (mveID 12) " +
                            "(varLen false) " +
                            "(argList ((IntDataValue (id 0) " +
                                                    "(itsFargID 13) " +
                                                    "(itsFargType INTEGER) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue 22) " +
                                                    "(subRange false) " +
                                                    "(minVal 0) " +
                                                    "(maxVal 0))))))";
            String matrix_matrix0_DBstring =
                    "(Matrix (mveID 18) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 1.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 2) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 21) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 22) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 23) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue q-string) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 24) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:01:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 25) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String matrix_matrix1_DBstring =
                    "(Matrix (mveID 36) " +
                            "(varLen false) " +
                            "(argList " +
                                "((QuoteStringDataValue (id 0) " +
                                    "(itsFargID 37) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue  a q string ) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 38) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg2>) " +
                                    "(subRange false)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 39) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 88) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String matrix_matrix2_DBstring =
                    "(Matrix (mveID 46) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String nominal_matrix_DBstring =
                    "(Matrix (mveID 52) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 53) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue another_nominal) " +
                                    "(subRange false))))))";
            String pred_matrix_DBstring =
                    "(Matrix (mveID 58) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 59) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false))))))";
            String text_matrix_DBstring =
                    "(Matrix (mveID 64) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 65) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_matrix_arg_list = new Vector<DataValue>();
                fargID = float_mve.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_matrix_arg_list.add(arg);
                float_matrix = new Matrix(db, float_mve_ID,
                                          float_matrix_arg_list);


                int_matrix_arg_list = new Vector<DataValue>();
                fargID = int_mve.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_matrix_arg_list.add(arg);
                int_matrix = new Matrix(db, int_mve_ID, int_matrix_arg_list);


                matrix_matrix0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_matrix0_arg_list.add(arg);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID,
                                            matrix_matrix0_arg_list);


                matrix_matrix1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getFormalArg(0).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(1).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(2).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_matrix1_arg_list.add(arg);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID,
                                            matrix_matrix1_arg_list);


                matrix_matrix2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getFormalArg(0).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_matrix2_arg_list.add(arg);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID,
                                            matrix_matrix2_arg_list);


                nominal_matrix_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getFormalArg(0).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_matrix_arg_list.add(arg);
                nominal_matrix = new Matrix(db, nominal_mve_ID,
                                            nominal_matrix_arg_list);


                pred_matrix_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getFormalArg(0).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_matrix_arg_list.add(arg);
                pred_matrix = new Matrix(db, pred_mve_ID, pred_matrix_arg_list);


                text_matrix_arg_list = new Vector<DataValue>();
                fargID = text_mve.getFormalArg(0).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_matrix_arg_list.add(arg);
                text_matrix = new Matrix(db, text_mve_ID, text_matrix_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_matrix_arg_list == null ) ||
                 ( float_matrix == null ) ||
                 ( int_matrix_arg_list == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0_arg_list == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1_arg_list == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2_arg_list == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix_arg_list == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix_arg_list == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix_arg_list == null ) ||
                 ( text_matrix == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_matrix_arg_list failed.\n");
                    }

                    if ( float_matrix == null )
                    {
                        outStream.printf("allocation of float_matrix failed.\n");
                    }

                    if ( int_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_matrix_arg_list failed.\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf("allocation of int_matrix failed.\n");
                    }

                    if ( matrix_matrix0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix0_arg_list failed.\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed.\n");
                    }

                    if ( matrix_matrix1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix1_arg_list failed.\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed.\n");
                    }

                    if ( matrix_matrix2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix2_arg_list failed.\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed.\n");
                    }

                    if ( nominal_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_matrix_arg_list failed.\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of nominal_matrix failed.\n");
                    }

                    if ( pred_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_matrix_arg_list failed.\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf("allocation of pred_matrix failed.\n");
                    }

                    if ( text_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_matrix_arg_list failed.\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf("allocation of text_matrix failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString(): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString(): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString(): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString(): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString(): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString(): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString(): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString(): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString(): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString(): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString(): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString(): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString(): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString(): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString(): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString(): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }
        }

        /* test is now set up.
         *
         * Begin with a battery of tests of getArgCopy() -- objective is to
         * verify that output of getArgCopy() is a valid copy of the target
         * argument, or that the method fails appropriately if the target
         * doesn't exist.
         */

        failures += Matrix.TestGetArgCopy(float_matrix, -1, 1,
                expectedResult.system_error, "float_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(float_matrix,  0, 1,
                expectedResult.succeed, "float_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(float_matrix,  1, 3,
                expectedResult.return_null, "float_matrix", outStream, verbose);

        failures += Matrix.TestGetArgCopy(int_matrix, -1, 10,
                expectedResult.system_error, "int_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(int_matrix,  0, 11,
                expectedResult.succeed, "int_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(int_matrix,  1, 12,
                expectedResult.return_null, "int_matrix", outStream, verbose);

        failures += Matrix.TestGetArgCopy(matrix_matrix0, -1, 20,
                expectedResult.system_error, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  0, 21,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  1, 22,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  2, 23,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  3, 24,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  4, 25,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  5, 26,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  6, 27,
                expectedResult.succeed, "matrix_matrix0", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix0,  7, 28,
                expectedResult.return_null, "matrix_matrix0", outStream, verbose);

        failures += Matrix.TestGetArgCopy(matrix_matrix1, -1, 30,
                expectedResult.system_error, "matrix_matrix1", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix1,  0, 31,
                expectedResult.succeed, "matrix_matrix1", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix1,  1, 32,
                expectedResult.succeed, "matrix_matrix1", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix1,  2, 32,
                expectedResult.succeed, "matrix_matrix1", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix1,  3, 32,
                expectedResult.return_null, "matrix_matrix1", outStream, verbose);

        failures += Matrix.TestGetArgCopy(matrix_matrix2, -1, 40,
                expectedResult.system_error, "matrix_matrix2", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix2,  0, 41,
                expectedResult.succeed, "matrix_matrix2", outStream, verbose);
        failures += Matrix.TestGetArgCopy(matrix_matrix2,  1, 42,
                expectedResult.return_null, "matrix_matrix2", outStream, verbose);

        failures += Matrix.TestGetArgCopy(nominal_matrix, -1, 50,
                expectedResult.system_error, "nominal_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(nominal_matrix,  0, 51,
                expectedResult.succeed, "nominal_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(nominal_matrix,  1, 52,
                expectedResult.return_null, "nominal_matrix", outStream, verbose);

        failures += Matrix.TestGetArgCopy(pred_matrix, -1, 50,
                expectedResult.system_error, "pred_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(pred_matrix,  0, 51,
                expectedResult.succeed, "pred_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(pred_matrix,  1, 52,
                expectedResult.return_null, "pred_matrix", outStream, verbose);

        failures += Matrix.TestGetArgCopy(text_matrix, -1, 50,
                expectedResult.system_error, "text_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(text_matrix,  0, 51,
                expectedResult.succeed, "text_matrix", outStream, verbose);
        failures += Matrix.TestGetArgCopy(text_matrix,  1, 52,
                expectedResult.return_null, "text_matrix", outStream, verbose);


        /* begin with tests of a float matrix
         */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = float_mve.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 0.0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(float_matrix,
                                                    floatArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "float_matrix",
                                                    "floatArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      intArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      nomArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      predArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      qsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(float_matrix,
                                                      tsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "float_matrix",
                                                      "tsArg");

                failures += VerifyArgListAssignment(float_matrix,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "float_matrix",
                                                    "arg");
            }
        }


        /* now an int matrix */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = int_mve.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(idv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      floatArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "floatArg");

                failures += VerifyArgListAssignment(int_matrix,
                                                    intArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "int_matrix",
                                                    "intArg");

                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      nomArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      predArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      qsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(int_matrix,
                                                      tsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "int_matrix",
                                                      "tsArg");

                failures += VerifyArgListAssignment(int_matrix,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "int_matrix",
                                                    "arg");
            }
        }


        /* now an nominal matrix */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = nominal_mve.getFormalArg(0).getID();
                arg = new NominalDataValue(db, fargID, "whatever");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(ndv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      floatArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      intArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "intArg");

                failures += VerifyArgListAssignment(nominal_matrix,
                                                    nomArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "nominal_matrix",
                                                    "nomArg");

                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      predArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      qsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(nominal_matrix,
                                                      tsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "nominal_matrix",
                                                      "tsArg");

                failures += VerifyArgListAssignment(nominal_matrix,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "nominal_matrix",
                                                    "arg");
            }
        }


        /* now a predicate matrix */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pred_mve.getFormalArg(0).getID();
                arg = new PredDataValue(db, fargID,
                        new Predicate(db, pve0_ID));
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(pdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      floatArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      intArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      nomArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "nomArg");

                failures += VerifyArgListAssignment(pred_matrix,
                                                    predArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred_matrix",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      qsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(pred_matrix,
                                                      tsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred_matrix",
                                                      "tsArg");

                failures += VerifyArgListAssignment(pred_matrix,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred_matrix",
                                                    "arg");
            }
        }


        /* now a text matrix */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = text_mve.getFormalArg(0).getID();
                arg = new TextStringDataValue(db, fargID,
                                              "yet another text string");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(tsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      floatArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      intArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      nomArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      predArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "predArg");

                failures += VerifyArgListAssignment(text_matrix,
                                                    textArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "text_matrix",
                                                    "textArg");

                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      qsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(text_matrix,
                                                      tsArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "tsArg");

                failures += VerifyArgListAssignment(text_matrix,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "text_matrix",
                                                    "arg");
            }
        }


        /* we have save matrix matricies for last -- in theory
         * only need to test the single entry case below.  However,
         * we will start with that, and then do some spot checks on
         * multi-entry matrix matrix.
         *
         * First use new args without fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    floatArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    intArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    nomArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    predArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_matrix2,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    qsArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    tsArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "tsArg");
            }
        }

        /* repeat the above test, only with fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve2.getFormalArg(0).getID();

                floatArg = new FloatDataValue(db, fargID, 1066.0);
                intArg = new IntDataValue(db, fargID, 1903);
                nomArg = new NominalDataValue(db, fargID, "yan");
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db, fargID, "yats");
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 60));
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve2.getFormalArg(0).getFargName());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    floatArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    intArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    nomArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    predArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_matrix2,
                                                      textArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "matrix_matrix2",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    qsArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_matrix2,
                                                    tsArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix2",
                                                    "tsArg");
            }
        }


        /* finally, do some spot checks of replaceArg()/getArg() on matricies
         * of length greater than one -- in the first pass, we will not assign
         * fargIDs.
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    floatArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    intArg,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    nomArg,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_matrix0,
                                                      textArg,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "text_matrix",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    qsArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    tsArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "tsArg");
            }
        }

        /* and a simlar test, with fargIDs set */

        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve0.getFormalArg(0).getID();
                floatArg = new FloatDataValue(db, fargID, 1066.0);

                fargID = matrix_mve0.getFormalArg(1).getID();
                intArg = new IntDataValue(db, fargID, 1903);

                fargID = matrix_mve0.getFormalArg(2).getID();
                nomArg = new NominalDataValue(db, fargID, "yan");

                fargID = matrix_mve0.getFormalArg(3).getID();
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));

                fargID = matrix_mve0.getFormalArg(6).getID();
                textArg = new TextStringDataValue(db, fargID, "yats");

                fargID = matrix_mve0.getFormalArg(4).getID();
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");

                fargID = matrix_mve0.getFormalArg(5).getID();
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 360));

                fargID = matrix_mve0.getFormalArg(6).getID();
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve0.getFormalArg(6).getFargName());

                fargID = matrix_mve0.getFormalArg(6).getID();
                arg = new FloatDataValue(db, fargID, 10.0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( arg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    floatArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    intArg,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix1",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    nomArg,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_matrix0,
                                                      textArg,
                                                      6,
                                                      outStream,
                                                      verbose,
                                                      "matrix_matrix0",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    qsArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    tsArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "tsArg");

                failures += VerifyArgListAssignment(matrix_matrix0,
                                                    arg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix0",
                                                    "arg");
            }
        }

        /* we have now tested replaceArg() and getArg() against all
         * type combinations.  Must now go through the rest of the
         * cases in which failures are expected.
         */

        /* verify failure on a farg ID mismatch. */

        if ( failures == 0 )
        {
            goodArg = null;
            badArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve1.getFormalArg(0).getID();
                goodArg = new NominalDataValue(db, fargID, "good_fargID");

                fargID = matrix_mve1.getFormalArg(1).getID();
                badArg = new NominalDataValue(db, fargID, "bad_fargID");

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( badArg == null ) ||
                 ( goodArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fID)";

                    if ( goodArg == null )
                    {
                        outStream.printf("%s: Allocation of goodArg failed.\n",
                                         testTag);
                    }

                    if ( badArg == null )
                    {
                        outStream.printf("%s: Allocation of badArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_matrix1,
                                                    goodArg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix1",
                                                    "goodArg");

                failures += VerifyArgListAsgnmntFails(matrix_matrix1,
                                                      badArg,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "matrix_matrix1",
                                                      "badArg");

                failures += VerifyArgListAssignment(matrix_matrix1,
                                                    badArg,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "matrix_matrix1",
                                                    "badArg");
            }
        }


        /* next, verify that getArg() and replaceArg() fail when supplied
         * invalid indexes.
         */
        /* replaceArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                float_matrix.replaceArg(-1, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx0)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: float_matrix.replaceArg(-1, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: float_matrix.replaceArg(-1, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* replaceArg() with index too big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                matrix_matrix1.replaceArg(3, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx1)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_matrix1.replaceArg(3, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_matrix1.replaceArg(3, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = matrix_matrix1.getArg(-1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx2)";

                    if ( arg != null )
                    {
                        outStream.printf(
                                "%s: matrix_matrix1.getArg(-1) returned.\n",
                                testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_matrix1.getArg(-1) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_matrix1.getArg(-1) " +
                                "failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with index to big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = float_matrix.getArg(1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx3)";

                    if ( arg != null )
                    {
                        outStream.printf("%s: float_matrix.getArg(1) " +
                                "returned non-null.\n", testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                            "%s: float_matrix.getArg(1) failed to complete.\n",
                            testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s: float_matrix.getArg(1) " +
                                "threw a system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::TestArgListManagement() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing copy constructor for class Matrix                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> float_matrix_arg_list = null;
        Vector<DataValue> int_matrix_arg_list = null;
        Vector<DataValue> matrix_matrix0_arg_list = null;
        Vector<DataValue> matrix_matrix1_arg_list = null;
        Vector<DataValue> matrix_matrix2_arg_list = null;
        Vector<DataValue> nominal_matrix_arg_list = null;
        Vector<DataValue> pred_matrix_arg_list = null;
        Vector<DataValue> text_matrix_arg_list = null;
        Matrix float_matrix = null;
        Matrix float_matrix_copy = null;
        Matrix empty_float_matrix = null;
        Matrix empty_float_matrix_copy = null;
        Matrix int_matrix = null;
        Matrix int_matrix_copy = null;
        Matrix empty_int_matrix = null;
        Matrix empty_int_matrix_copy = null;
        Matrix matrix_matrix0 = null;
        Matrix matrix_matrix0_copy = null;
        Matrix empty_matrix_matrix0 = null;
        Matrix empty_matrix_matrix0_copy = null;
        Matrix matrix_matrix1 = null;
        Matrix matrix_matrix1_copy = null;
        Matrix empty_matrix_matrix1 = null;
        Matrix empty_matrix_matrix1_copy = null;
        Matrix matrix_matrix2 = null;
        Matrix matrix_matrix2_copy = null;
        Matrix empty_matrix_matrix2 = null;
        Matrix empty_matrix_matrix2_copy = null;
        Matrix nominal_matrix = null;
        Matrix nominal_matrix_copy = null;
        Matrix empty_nominal_matrix = null;
        Matrix empty_nominal_matrix_copy = null;
        Matrix pred_matrix = null;
        Matrix pred_matrix_copy = null;
        Matrix empty_pred_matrix = null;
        Matrix empty_pred_matrix_copy = null;
        Matrix text_matrix = null;
        Matrix text_matrix_copy = null;
        Matrix empty_text_matrix = null;
        Matrix empty_text_matrix_copy = null;
        Matrix m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_matrix_string = "(11.0)";
            String int_matrix_string = "(22)";
            String matrix_matrix0_string = "(1.0, 2, a_nominal, " +
                                           "pve0(<arg1>, <arg2>), " +
                                           "\"q-string\", 00:00:01:000, " +
                                           "<untyped>)";
            String matrix_matrix1_string = "(\" a q string \", <arg2>, 88)";
            String matrix_matrix2_string = "(<arg1>)";
            String nominal_matrix_string = "(another_nominal)";
            String pred_matrix_string = "(pve0(<arg1>, <arg2>))";
            String text_matrix_string = "(a text string)";
            String float_matrix_DBstring =
                    "(Matrix (mveID 4) " +
                            "(varLen false) " +
                            "(argList ((FloatDataValue (id 0) " +
                                                      "(itsFargID 5) " +
                                                      "(itsFargType FLOAT) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 11.0) " +
                                                      "(subRange false) " +
                                                      "(minVal 0.0) " +
                                                      "(maxVal 0.0))))))";
            String int_matrix_DBstring =
                    "(Matrix (mveID 10) " +
                            "(varLen false) " +
                            "(argList " +
                                "((IntDataValue (id 0) " +
                                    "(itsFargID 11) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 22) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String matrix_matrix0_DBstring =
                    "(Matrix (mveID 16) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 17) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 1.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 18) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 2) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                            "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 21) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue q-string) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 22) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:01:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 23) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String matrix_matrix1_DBstring =
                    "(Matrix (mveID 34) " +
                            "(varLen false) " +
                            "(argList " +
                                "((QuoteStringDataValue (id 0) " +
                                    "(itsFargID 35) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue  a q string ) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 36) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg2>) " +
                                    "(subRange false)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 37) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 88) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String matrix_matrix2_DBstring =
                    "(Matrix (mveID 44) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 45) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String nominal_matrix_DBstring =
                    "(Matrix (mveID 50) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 51) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue another_nominal) " +
                                    "(subRange false))))))";
            String pred_matrix_DBstring =
                    "(Matrix (mveID 56) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 57) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false))))))";
            String text_matrix_DBstring =
                    "(Matrix (mveID 62) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 63) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))";
            String empty_float_matrix_string = "(0.0)";
            String empty_int_matrix_string = "(0)";
            String empty_matrix_matrix0_string =
                    "(0.0, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String empty_matrix_matrix1_string = "(<arg1>, <arg2>, <arg3>)";
            String empty_matrix_matrix2_string = "(<arg1>)";
            String empty_nominal_matrix_string = "()";
            String empty_pred_matrix_string = "(())";
            String empty_text_matrix_string = "()";
            String empty_float_matrix_DBstring =
                    "(Matrix (mveID 4) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 5) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0))))))";
            String empty_int_matrix_DBstring =
                    "(Matrix (mveID 10) " +
                            "(varLen false) " +
                            "(argList " +
                                "((IntDataValue (id 0) " +
                                    "(itsFargID 11) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0))))))";
            String empty_matrix_matrix0_DBstring =
                    "(Matrix (mveID 16) " +
                            "(varLen false) " +
                            "(argList " +
                                "((FloatDataValue (id 0) " +
                                    "(itsFargID 17) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0)), " +
                                "(IntDataValue (id 0) " +
                                    "(itsFargID 18) " +
                                    "(itsFargType INTEGER) " +
                                    "(itsCellID 0) " +
                                    "(itsValue 0) " +
                                    "(subRange false) " +
                                    "(minVal 0) " +
                                    "(maxVal 0)), " +
                                "(NominalDataValue (id 0) " +
                                    "(itsFargID 19) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false)), " +
                                "(PredDataValue (id 0) " +
                                    "(itsFargID 20) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false)), " +
                                "(QuoteStringDataValue (id 0) " +
                                    "(itsFargID 21) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false)), " +
                                "(TimeStampDataValue (id 0) " +
                                    "(itsFargID 22) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 0) " +
                                    "(itsValue (60,00:00:00:000)) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 23) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <untyped>) " +
                                    "(subRange false))))))";
            String empty_matrix_matrix1_DBstring =
                    "(Matrix (mveID 34) " +
                            "(varLen false) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 35) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 36) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg2>) " +
                                    "(subRange false)), " +
                                "(UndefinedDataValue (id 0) " +
                                    "(itsFargID 37) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg3>) " +
                                    "(subRange false))))))";
            String empty_matrix_matrix2_DBstring =
                    "(Matrix (mveID 44) " +
                            "(varLen true) " +
                            "(argList " +
                                "((UndefinedDataValue (id 0) " +
                                    "(itsFargID 45) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <arg1>) " +
                                    "(subRange false))))))";
            String empty_nominal_matrix_DBstring =
                    "(Matrix (mveID 50) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 51) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))";
            String empty_pred_matrix_DBstring =
                    "(Matrix (mveID 56) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 57) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))";
            String empty_text_matrix_DBstring =
                    "(Matrix (mveID 62) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 63) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_matrix_arg_list = new Vector<DataValue>();
                fargID = float_mve.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_matrix_arg_list.add(arg);
                float_matrix = new Matrix(db, float_mve_ID,
                                          float_matrix_arg_list);


                int_matrix_arg_list = new Vector<DataValue>();
                fargID = int_mve.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_matrix_arg_list.add(arg);
                int_matrix = new Matrix(db, int_mve_ID, int_matrix_arg_list);


                matrix_matrix0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_matrix0_arg_list.add(arg);
                fargID = matrix_mve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_matrix0_arg_list.add(arg);
                matrix_matrix0 = new Matrix(db, matrix_mve0_ID,
                                            matrix_matrix0_arg_list);


                matrix_matrix1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getFormalArg(0).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(1).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_matrix1_arg_list.add(arg);
                fargID = matrix_mve1.getFormalArg(2).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_matrix1_arg_list.add(arg);
                matrix_matrix1 = new Matrix(db, matrix_mve1_ID,
                                            matrix_matrix1_arg_list);


                matrix_matrix2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getFormalArg(0).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_matrix2_arg_list.add(arg);
                matrix_matrix2 = new Matrix(db, matrix_mve2_ID,
                                            matrix_matrix2_arg_list);


                nominal_matrix_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getFormalArg(0).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_matrix_arg_list.add(arg);
                nominal_matrix = new Matrix(db, nominal_mve_ID,
                                            nominal_matrix_arg_list);


                pred_matrix_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getFormalArg(0).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_matrix_arg_list.add(arg);
                pred_matrix = new Matrix(db, pred_mve_ID, pred_matrix_arg_list);


                text_matrix_arg_list = new Vector<DataValue>();
                fargID = text_mve.getFormalArg(0).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_matrix_arg_list.add(arg);
                text_matrix = new Matrix(db, text_mve_ID, text_matrix_arg_list);


                empty_float_matrix   = new Matrix(db, float_mve_ID);
                empty_int_matrix     = new Matrix(db, int_mve_ID);
                empty_matrix_matrix0 = new Matrix(db, matrix_mve0_ID);
                empty_matrix_matrix1 = new Matrix(db, matrix_mve1_ID);
                empty_matrix_matrix2 = new Matrix(db, matrix_mve2_ID);
                empty_nominal_matrix = new Matrix(db, nominal_mve_ID);
                empty_pred_matrix    = new Matrix(db, pred_mve_ID);
                empty_text_matrix    = new Matrix(db, text_mve_ID);


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_matrix_arg_list == null ) ||
                 ( float_matrix == null ) ||
                 ( int_matrix_arg_list == null ) ||
                 ( int_matrix == null ) ||
                 ( matrix_matrix0_arg_list == null ) ||
                 ( matrix_matrix0 == null ) ||
                 ( matrix_matrix1_arg_list == null ) ||
                 ( matrix_matrix1 == null ) ||
                 ( matrix_matrix2_arg_list == null ) ||
                 ( matrix_matrix2 == null ) ||
                 ( nominal_matrix_arg_list == null ) ||
                 ( nominal_matrix == null ) ||
                 ( pred_matrix_arg_list == null ) ||
                 ( pred_matrix == null ) ||
                 ( text_matrix_arg_list == null ) ||
                 ( text_matrix == null ) ||
                 ( empty_float_matrix == null ) ||
                 ( empty_int_matrix == null ) ||
                 ( empty_matrix_matrix0 == null ) ||
                 ( empty_matrix_matrix1 == null ) ||
                 ( empty_matrix_matrix2 == null ) ||
                 ( empty_nominal_matrix == null ) ||
                 ( empty_pred_matrix == null ) ||
                 ( empty_text_matrix == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_matrix_arg_list failed.\n");
                    }

                    if ( float_matrix == null )
                    {
                        outStream.printf("allocation of float_matrix failed.\n");
                    }

                    if ( int_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_matrix_arg_list failed.\n");
                    }

                    if ( int_matrix == null )
                    {
                        outStream.printf("allocation of int_matrix failed.\n");
                    }

                    if ( matrix_matrix0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix0_arg_list failed.\n");
                    }

                    if ( matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix0 failed.\n");
                    }

                    if ( matrix_matrix1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix1_arg_list failed.\n");
                    }

                    if ( matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix1 failed.\n");
                    }

                    if ( matrix_matrix2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_matrix2_arg_list failed.\n");
                    }

                    if ( matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_matrix2 failed.\n");
                    }

                    if ( nominal_matrix_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_matrix_arg_list failed.\n");
                    }

                    if ( nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of nominal_matrix failed.\n");
                    }

                    if ( pred_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_matrix_arg_list failed.\n");
                    }

                    if ( pred_matrix == null )
                    {
                        outStream.printf("allocation of pred_matrix failed.\n");
                    }

                    if ( text_matrix_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_matrix_arg_list failed.\n");
                    }

                    if ( text_matrix == null )
                    {
                        outStream.printf("allocation of text_matrix failed.\n");
                    }

                    if ( empty_float_matrix == null )
                    {
                        outStream.printf(
                                "allocation of empty_float_matrix failed.\n");
                    }

                    if ( empty_int_matrix == null )
                    {
                        outStream.printf(
                                "allocation of empty_int_matrix failed.\n");
                    }

                    if ( empty_matrix_matrix0 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_matrix0 failed.\n");
                    }

                    if ( empty_matrix_matrix1 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_matrix1 failed.\n");
                    }

                    if ( empty_matrix_matrix2 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_matrix2 failed.\n");
                    }

                    if ( empty_nominal_matrix == null )
                    {
                        outStream.printf(
                                "allocation of empty_nominal_matrix failed.\n");
                    }

                    if ( empty_pred_matrix == null )
                    {
                        outStream.printf(
                                "allocation of empty_pred_matrix failed.\n");
                    }

                    if ( empty_text_matrix == null )
                    {
                        outStream.printf(
                                "allocation of empty_text_matrix failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_matrix.toString().
                        compareTo(float_matrix_string) != 0 ) ||
                      ( int_matrix.toString().
                        compareTo(int_matrix_string) != 0 ) ||
                      ( matrix_matrix0.toString().
                        compareTo(matrix_matrix0_string) != 0 ) ||
                      ( matrix_matrix1.toString().
                        compareTo(matrix_matrix1_string) != 0 ) ||
                      ( matrix_matrix2.toString().
                        compareTo(matrix_matrix2_string) != 0 ) ||
                      ( nominal_matrix.toString().
                        compareTo(nominal_matrix_string) != 0 ) ||
                      ( pred_matrix.toString().
                        compareTo(pred_matrix_string) != 0 ) ||
                      ( text_matrix.toString().
                        compareTo(text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toString().
                         compareTo(float_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toString(): %s\n",
                                float_matrix.toString());
                    }

                    if ( int_matrix.toString().
                         compareTo(int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toString(): %s\n",
                                int_matrix.toString());
                    }

                    if ( matrix_matrix0.toString().
                         compareTo(matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toString(): %s\n",
                                matrix_matrix0.toString());
                    }

                    if ( matrix_matrix1.toString().
                         compareTo(matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toString(): %s\n",
                                matrix_matrix1.toString());
                    }

                    if ( matrix_matrix2.toString().
                         compareTo(matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toString(): %s\n",
                                matrix_matrix2.toString());
                    }

                    if ( nominal_matrix.toString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toString(): %s\n",
                                nominal_matrix.toString());
                    }

                    if ( pred_matrix.toString().
                         compareTo(pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toString(): %s\n",
                                pred_matrix.toString());
                    }

                    if ( text_matrix.toString().
                         compareTo(text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toString(): %s\n",
                                text_matrix.toString());
                    }
                }
            }
            else if ( ( float_matrix.toDBString().
                        compareTo(float_matrix_DBstring) != 0 ) ||
                      ( int_matrix.toDBString().
                        compareTo(int_matrix_DBstring) != 0 ) ||
                      ( matrix_matrix0.toDBString().
                        compareTo(matrix_matrix0_DBstring) != 0 ) ||
                      ( matrix_matrix1.toDBString().
                        compareTo(matrix_matrix1_DBstring) != 0 ) ||
                      ( matrix_matrix2.toDBString().
                        compareTo(matrix_matrix2_DBstring) != 0 ) ||
                      ( nominal_matrix.toDBString().
                        compareTo(nominal_matrix_DBstring) != 0 ) ||
                      ( pred_matrix.toDBString().
                        compareTo(pred_matrix_DBstring) != 0 ) ||
                      ( text_matrix.toDBString().
                        compareTo(text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_matrix.toDBString().
                         compareTo(float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_matrix.toDBString(): %s\n",
                                float_matrix.toDBString());
                    }

                    if ( int_matrix.toDBString().
                         compareTo(int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_matrix.toDBString(): %s\n",
                                int_matrix.toDBString());
                    }

                    if ( matrix_matrix0.toDBString().
                         compareTo(matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix0.toDBString(): %s\n",
                                matrix_matrix0.toDBString());
                    }

                    if ( matrix_matrix1.toDBString().
                         compareTo(matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix1.toDBString(): %s\n",
                                matrix_matrix1.toDBString());
                    }

                    if ( matrix_matrix2.toDBString().
                         compareTo(matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_matrix2.toDBString(): %s\n",
                                matrix_matrix2.toDBString());
                    }

                    if ( nominal_matrix.toDBString().
                         compareTo(nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_matrix.toDBString(): %s\n",
                                nominal_matrix.toDBString());
                    }

                    if ( pred_matrix.toDBString().
                         compareTo(pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_matrix.toDBString(): %s\n",
                                pred_matrix.toDBString());
                    }

                    if ( text_matrix.toDBString().
                         compareTo(text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_matrix.toDBString(): %s\n",
                                text_matrix.toDBString());
                    }
                }
            }
            else if ( ( empty_float_matrix.toString().
                        compareTo(empty_float_matrix_string) != 0 ) ||
                      ( empty_int_matrix.toString().
                        compareTo(empty_int_matrix_string) != 0 ) ||
                      ( empty_matrix_matrix0.toString().
                        compareTo(empty_matrix_matrix0_string) != 0 ) ||
                      ( empty_matrix_matrix1.toString().
                        compareTo(empty_matrix_matrix1_string) != 0 ) ||
                      ( empty_matrix_matrix2.toString().
                        compareTo(empty_matrix_matrix2_string) != 0 ) ||
                      ( empty_nominal_matrix.toString().
                        compareTo(empty_nominal_matrix_string) != 0 ) ||
                      ( empty_pred_matrix.toString().
                        compareTo(empty_pred_matrix_string) != 0 ) ||
                      ( empty_text_matrix.toString().
                        compareTo(empty_text_matrix_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_matrix.toString().
                         compareTo(empty_float_matrix_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_matrix.toString(): %s\n",
                            empty_float_matrix.toString());
                    }

                    if ( empty_int_matrix.toString().
                         compareTo(empty_int_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_matrix.toString(): %s\n",
                                empty_int_matrix.toString());
                    }

                    if ( empty_matrix_matrix0.toString().
                         compareTo(empty_matrix_matrix0_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix0.toString(): %s\n",
                            empty_matrix_matrix0.toString());
                    }

                    if ( empty_matrix_matrix1.toString().
                         compareTo(empty_matrix_matrix1_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix1.toString(): %s\n",
                            empty_matrix_matrix1.toString());
                    }

                    if ( empty_matrix_matrix2.toString().
                         compareTo(empty_matrix_matrix2_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix2.toString(): %s\n",
                            empty_matrix_matrix2.toString());
                    }

                    if ( empty_nominal_matrix.toString().
                         compareTo(empty_nominal_matrix_string) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_matrix.toString(): %s\n",
                             empty_nominal_matrix.toString());
                    }

                    if ( empty_pred_matrix.toString().
                         compareTo(empty_pred_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_matrix.toString(): %s\n",
                                empty_pred_matrix.toString());
                    }

                    if ( empty_text_matrix.toString().
                         compareTo(empty_text_matrix_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_matrix.toString(): %s\n",
                                empty_text_matrix.toString());
                    }
                }
            }
            else if ( ( empty_float_matrix.toDBString().
                        compareTo(empty_float_matrix_DBstring) != 0 ) ||
                      ( empty_int_matrix.toDBString().
                        compareTo(empty_int_matrix_DBstring) != 0 ) ||
                      ( empty_matrix_matrix0.toDBString().
                        compareTo(empty_matrix_matrix0_DBstring) != 0 ) ||
                      ( empty_matrix_matrix1.toDBString().
                        compareTo(empty_matrix_matrix1_DBstring) != 0 ) ||
                      ( empty_matrix_matrix2.toDBString().
                        compareTo(empty_matrix_matrix2_DBstring) != 0 ) ||
                      ( empty_nominal_matrix.toDBString().
                        compareTo(empty_nominal_matrix_DBstring) != 0 ) ||
                      ( empty_pred_matrix.toDBString().
                        compareTo(empty_pred_matrix_DBstring) != 0 ) ||
                      ( empty_text_matrix.toDBString().
                        compareTo(empty_text_matrix_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_matrix.toDBString().
                         compareTo(empty_float_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_matrix.toDBString(): %s\n",
                            empty_float_matrix.toDBString());
                    }

                    if ( empty_int_matrix.toDBString().
                         compareTo(empty_int_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_matrix.toDBString(): %s\n",
                                empty_int_matrix.toDBString());
                    }

                    if ( empty_matrix_matrix0.toDBString().
                         compareTo(empty_matrix_matrix0_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix0.toDBString(): %s\n",
                            empty_matrix_matrix0.toDBString());
                    }

                    if ( empty_matrix_matrix1.toDBString().
                         compareTo(empty_matrix_matrix1_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix1.toDBString(): %s\n",
                            empty_matrix_matrix1.toDBString());
                    }

                    if ( empty_matrix_matrix2.toDBString().
                         compareTo(empty_matrix_matrix2_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_matrix2.toDBString(): %s\n",
                            empty_matrix_matrix2.toDBString());
                    }

                    if ( empty_nominal_matrix.toDBString().
                         compareTo(empty_nominal_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_matrix.toDBString(): %s\n",
                             empty_nominal_matrix.toDBString());
                    }

                    if ( empty_pred_matrix.toDBString().
                         compareTo(empty_pred_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_matrix.toDBString(): %s\n",
                                empty_pred_matrix.toDBString());
                    }

                    if ( empty_text_matrix.toDBString().
                         compareTo(empty_text_matrix_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_matrix.toDBString(): %s\n",
                                empty_text_matrix.toDBString());
                    }
                }
            }
        }

        // setup is complete -- now try to make the copies
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_float_matrix_copy   = new Matrix(empty_float_matrix);
                empty_int_matrix_copy     = new Matrix(empty_int_matrix);
                empty_matrix_matrix0_copy = new Matrix(empty_matrix_matrix0);
                empty_matrix_matrix1_copy = new Matrix(empty_matrix_matrix1);
                empty_matrix_matrix2_copy = new Matrix(empty_matrix_matrix2);
                empty_nominal_matrix_copy = new Matrix(empty_nominal_matrix);
                empty_pred_matrix_copy    = new Matrix(empty_pred_matrix);
                empty_text_matrix_copy    = new Matrix(empty_text_matrix);

                float_matrix_copy   = new Matrix(float_matrix);
                int_matrix_copy     = new Matrix(int_matrix);
                matrix_matrix0_copy = new Matrix(matrix_matrix0);
                matrix_matrix1_copy = new Matrix(matrix_matrix1);
                matrix_matrix2_copy = new Matrix(matrix_matrix2);
                nominal_matrix_copy = new Matrix(nominal_matrix);
                pred_matrix_copy    = new Matrix(pred_matrix);
                text_matrix_copy    = new Matrix(text_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_float_matrix_copy == null ) ||
                 ( empty_int_matrix_copy == null ) ||
                 ( empty_matrix_matrix0_copy == null ) ||
                 ( empty_matrix_matrix1_copy == null ) ||
                 ( empty_matrix_matrix2_copy == null ) ||
                 ( empty_nominal_matrix_copy == null ) ||
                 ( empty_pred_matrix_copy == null ) ||
                 ( empty_text_matrix_copy == null ) ||
                 ( float_matrix_copy == null ) ||
                 ( int_matrix_copy == null ) ||
                 ( matrix_matrix0_copy == null ) ||
                 ( matrix_matrix1_copy == null ) ||
                 ( matrix_matrix2_copy == null ) ||
                 ( nominal_matrix_copy == null ) ||
                 ( pred_matrix_copy == null ) ||
                 ( text_matrix_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( empty_float_matrix_copy == null )
                {
                    outStream.printf(
                            "empty_float_matrix_copy allocation failed.\n");
                }

                if ( empty_int_matrix_copy == null )
                {
                    outStream.printf(
                            "empty_int_matrix_copy allocation failed.\n");
                }

                if ( empty_matrix_matrix0_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_matrix0_copy allocation failed.\n");
                }

                if ( empty_matrix_matrix1_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_matrix1_copy allocation failed.\n");
                }

                if ( empty_matrix_matrix2_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_matrix2_copy allocation failed.\n");
                }

                if ( empty_nominal_matrix_copy == null )
                {
                    outStream.printf(
                            "empty_nominal_matrix_copy allocation failed.\n");
                }

                if ( empty_pred_matrix_copy == null )
                {
                    outStream.printf(
                            "empty_pred_matrix_copy allocation failed.\n");
                }

                if ( empty_text_matrix_copy == null )
                {
                    outStream.printf(
                            "empty_text_matrix_copy allocation failed.\n");
                }

                if ( float_matrix_copy == null )
                {
                    outStream.printf(
                            "float_matrix_copy allocation failed.\n");
                }

                if ( int_matrix_copy == null )
                {
                    outStream.printf(
                            "int_matrix_copy allocation failed.\n");
                }

                if ( matrix_matrix0_copy == null )
                {
                    outStream.printf(
                            "matrix_matrix0_copy allocation failed.\n");
                }

                if ( matrix_matrix1_copy == null )
                {
                    outStream.printf(
                            "matrix_matrix1_copy allocation failed.\n");
                }

                if ( matrix_matrix2_copy == null )
                {
                    outStream.printf(
                            "matrix_matrix2_copy allocation failed.\n");
                }

                if ( nominal_matrix_copy == null )
                {
                    outStream.printf(
                            "nominal_matrix_copy allocation failed.\n");
                }

                if ( pred_matrix_copy == null )
                {
                    outStream.printf(
                            "pred_matrix_copy allocation failed.\n");
                }

                if ( text_matrix_copy == null )
                {
                    outStream.printf(
                            "text_matrix_copy allocation failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of copies failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "matrix copy threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // if failures == 0, check to see if the copies are valid */
        if ( failures == 0 )
        {
            failures += VerifyMatrixCopy(empty_float_matrix,
                                         empty_float_matrix_copy,
                                         outStream,
                                         verbose,
                                         "empty_float_matrix",
                                         "empty_float_matrix_copy");

            failures += VerifyMatrixCopy(empty_int_matrix,
                                         empty_int_matrix_copy,
                                         outStream,
                                         verbose,
                                         "empty_int_matrix",
                                         "empty_int_matrix_copy");

            failures += VerifyMatrixCopy(empty_matrix_matrix0,
                                         empty_matrix_matrix0_copy,
                                         outStream,
                                         verbose,
                                         "empty_matrix_matrix0",
                                         "empty_matrix_matrix0_copy");

            failures += VerifyMatrixCopy(empty_matrix_matrix1,
                                         empty_matrix_matrix1_copy,
                                         outStream,
                                         verbose,
                                         "empty_matrix_matrix1",
                                         "empty_matrix_matrix1_copy");

            failures += VerifyMatrixCopy(empty_matrix_matrix2,
                                         empty_matrix_matrix2_copy,
                                         outStream,
                                         verbose,
                                         "empty_matrix_matrix2",
                                         "empty_matrix_matrix2_copy");

            failures += VerifyMatrixCopy(empty_nominal_matrix,
                                         empty_nominal_matrix_copy,
                                         outStream,
                                         verbose,
                                         "empty_nominal_matrix",
                                         "empty_nominal_matrix_copy");

            failures += VerifyMatrixCopy(empty_pred_matrix,
                                         empty_pred_matrix_copy,
                                         outStream,
                                         verbose,
                                         "empty_pred_matrix",
                                         "empty_pred_matrix_copy");

            failures += VerifyMatrixCopy(empty_text_matrix,
                                         empty_text_matrix_copy,
                                         outStream,
                                         verbose,
                                         "empty_text_matrix",
                                         "empty_text_matrix_copy");

            failures += VerifyMatrixCopy(float_matrix,
                                         float_matrix_copy,
                                         outStream,
                                         verbose,
                                         "float_matrix",
                                         "float_matrix_copy");

            failures += VerifyMatrixCopy(int_matrix,
                                         int_matrix_copy,
                                         outStream,
                                         verbose,
                                         "int_matrix",
                                         "int_matrix_copy");

            failures += VerifyMatrixCopy(matrix_matrix0,
                                         matrix_matrix0_copy,
                                         outStream,
                                         verbose,
                                         "matrix_matrix0",
                                         "matrix_matrix0_copy");

            failures += VerifyMatrixCopy(matrix_matrix1,
                                         matrix_matrix1_copy,
                                         outStream,
                                         verbose,
                                         "matrix_matrix1",
                                         "matrix_matrix1_copy");

            failures += VerifyMatrixCopy(matrix_matrix2,
                                         matrix_matrix2_copy,
                                         outStream,
                                         verbose,
                                         "matrix_matrix2",
                                         "matrix_matrix2_copy");

            failures += VerifyMatrixCopy(nominal_matrix,
                                         nominal_matrix_copy,
                                         outStream,
                                         verbose,
                                         "nominal_matrix",
                                         "nominal_matrix_copy");

            failures += VerifyMatrixCopy(pred_matrix,
                                         pred_matrix_copy,
                                         outStream,
                                         verbose,
                                         "pred_matrix",
                                         "pred_matrix_copy");

            failures += VerifyMatrixCopy(text_matrix,
                                         text_matrix_copy,
                                         outStream,
                                         verbose,
                                         "text_matrix",
                                         "text_matrix_copy");
        }

        /* now verify that the copy constructor fails when passed an invalid
         * reference to a Matrix.  For now, this just means passing in a
         * null.
         */
        m0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            m0 = new Matrix((Matrix)null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( m0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( m0 != null )
                {
                    outStream.print("new Matrix(null) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new Matrix(null) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new Matrix(null) " +
                                    "didn't throw a SystemErrorException.\n");
                }
            }
        }


        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the to string methods for this
     * class.
     *
     *                                              JRM -- 10/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures                 = 0;
        Database db                  = null;
        PredicateVocabElement pve    = null;
        MatrixVocabElement mve0      = null;
        MatrixVocabElement mve1      = null;
        long fargID                  = DBIndex.INVALID_ID;
        long pveID                   = DBIndex.INVALID_ID;
        long mve0ID                  = DBIndex.INVALID_ID;
        long mve1ID                  = DBIndex.INVALID_ID;
        FormalArgument farg          = null;
        Vector<DataValue> argList0   = null;
        Vector<DataValue> argList1   = null;
        DataValue arg                = null;
        FloatDataValue floatArg0     = null;
        FloatDataValue floatArg1     = null;
        IntDataValue intArg0         = null;
        IntDataValue intArg1         = null;
        IntDataValue intArg2         = null;
        NominalDataValue nominalArg0 = null;
        NominalDataValue nominalArg1 = null;
        PredDataValue predArg0       = null;
        PredDataValue predArg1       = null;
        QuoteStringDataValue qsArg0  = null;
        QuoteStringDataValue qsArg1  = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        Matrix m0                    = null;
        Matrix m1                    = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve = new PredicateVocabElement(db, "pve");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve.appendFormalArg(farg);
            pveID = db.addPredVE(pve);

            // get a copy of the databases version of pve with ids assigned
            pve = db.getPredVE(pveID);

            mve0 = new MatrixVocabElement(db, "mve0");
            mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            mve0.appendFormalArg(farg);
            mve0.setVarLen(true);
            db.vl.addElement(mve0);
            mve0ID = mve0.getID();


            mve1 = new MatrixVocabElement(db, "mve1");
            mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db, "<arg>");
            mve1.appendFormalArg(farg);
            db.vl.addElement(mve1);
            mve1ID = mve1.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve == null ) ||
             ( pveID == DBIndex.INVALID_ID ) ||
             ( mve0 == null ) ||
             ( mve0ID == DBIndex.INVALID_ID ) ||
             ( mve1 == null ) ||
             ( mve1ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve == null )
                {
                    outStream.print("creation of pve failed.\n");
                }

                if ( pveID == DBIndex.INVALID_ID )
                {
                    outStream.print("pveID not initialized.\n");
                }

                if ( mve0 == null )
                {
                    outStream.print("creation of mve0 failed.\n");
                }

                if ( mve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve0ID not initialized.\n");
                }

                if ( mve1 == null )
                {
                    outStream.print("creation of mve1 failed.\n");
                }

                if ( mve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve1ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("mve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Setup the matricies that we will used for the toString and
        // toDBString tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 = "(1.0, 2, a_nominal, pve(<arg>), " +
                                 "\"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                "(Matrix (mveID 3) " +
                        "(varLen true) " +
                        "(argList " +
                            "((FloatDataValue (id 100) " +
                                "(itsFargID 4) " +
                                "(itsFargType FLOAT) " +
                                "(itsCellID 500) " +
                                "(itsValue 1.0) " +
                                "(subRange false) " +
                                "(minVal 0.0) " +
                                "(maxVal 0.0)), " +
                            "(IntDataValue (id 101) " +
                                "(itsFargID 5) " +
                                "(itsFargType INTEGER) " +
                                "(itsCellID 500) " +
                                "(itsValue 2) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0)), " +
                            "(NominalDataValue (id 102) " +
                                "(itsFargID 6) " +
                                "(itsFargType NOMINAL) " +
                                "(itsCellID 500) " +
                                "(itsValue a_nominal) " +
                                "(subRange false)), " +
                            "(PredDataValue (id 103) " +
                                "(itsFargID 7) " +
                                "(itsFargType PREDICATE) " +
                                "(itsCellID 500) " +
                                "(itsValue " +
                                    "(predicate (id 0) " +
                                        "(predID 1) " +
                                        "(predName pve) " +
                                        "(varLen false) " +
                                        "(argList " +
                                            "((UndefinedDataValue (id 0) " +
                                                "(itsFargID 2) " +
                                                "(itsFargType UNTYPED) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <arg>) " +
                                                "(subRange false))))))) " +
                                "(subRange false)), " +
                            "(QuoteStringDataValue (id 104) " +
                                "(itsFargID 8) " +
                                "(itsFargType QUOTE_STRING) " +
                                "(itsCellID 500) " +
                                "(itsValue q-string) " +
                                "(subRange false)), " +
                            "(TimeStampDataValue (id 105) " +
                                "(itsFargID 9) " +
                                "(itsFargType TIME_STAMP) " +
                                "(itsCellID 500) " +
                                "(itsValue (60,00:00:00:000)) " +
                                "(subRange false)), " +
                            "(UndefinedDataValue (id 106) " +
                                "(itsFargID 10) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 500) " +
                                "(itsValue <untyped>) " +
                                "(subRange false))))))";

            String testString1 = "(99)";
            String testDBString1 =
                "(Matrix (mveID 21) " +
                        "(varLen false) " +
                        "(argList " +
                            "((IntDataValue (id 107) " +
                                "(itsFargID 22) " +
                                "(itsFargType INTEGER) " +
                                "(itsCellID 501) " +
                                "(itsValue 99) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = mve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = mve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = mve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = mve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pveID));
                argList0.add(arg);
                fargID = mve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = mve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = mve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                             mve0.getFormalArg(6).getFargName());
                argList0.add(arg);

                m0 = new Matrix(db, mve0ID, argList0);

                // set argument IDs to dummy values to test toDBString()
                m0.argList.get(0).setID(100);
                m0.argList.get(1).setID(101);
                m0.argList.get(2).setID(102);
                m0.argList.get(3).setID(103);
                m0.argList.get(4).setID(104);
                m0.argList.get(5).setID(105);
                m0.argList.get(6).setID(106);

                // set argument cellIDs to dummy values to test toDBString()
                m0.argList.get(0).itsCellID = 500;
                m0.argList.get(1).itsCellID = 500;
                m0.argList.get(2).itsCellID = 500;
                m0.argList.get(3).itsCellID = 500;
                m0.argList.get(4).itsCellID = 500;
                m0.argList.get(5).itsCellID = 500;
                m0.argList.get(6).itsCellID = 500;

                argList1 = new Vector<DataValue>();

                fargID = mve1.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 99);
                argList1.add(arg);

                m1 = new Matrix(db, mve1ID, argList1);

                // set argument IDs to dummy values to test toDBString()
                m1.argList.get(0).setID(107);

                // set argument cellIDs to dummy values to test toDBString()
                m1.argList.get(0).itsCellID = 501;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 7 ) ||
                 ( m0 == null ) ||
                 ( argList1 == null ) ||
                 ( argList1.size() != 1 ) ||
                 ( m1 == null ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 7 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (7).\n",
                                         argList0.size());
                    }

                    if ( argList1 == null )
                    {
                        outStream.print("argList1 allocation failed.\n");
                    }
                    else if ( argList1.size() != 1 )
                    {
                        outStream.printf("unexpected argList1.size(): %d (1).\n",
                                         argList1.size());
                    }

                    if ( ( m0 == null ) ||
                         ( m1 == null ) )
                    {
                        outStream.print("one or more Matrix allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test predicate allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test matrix allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( m0.argList.get(0).getID() != 100 ) ||
                      ( m0.argList.get(1).getID() != 101 ) ||
                      ( m0.argList.get(2).getID() != 102 ) ||
                      ( m0.argList.get(3).getID() != 103 ) ||
                      ( m0.argList.get(4).getID() != 104 ) ||
                      ( m0.argList.get(5).getID() != 105 ) ||
                      ( m0.argList.get(6).getID() != 106 ) ||
                      ( m1.argList.get(0).getID() != 107 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected m?.argList arg ID(s): " +
                            "%d %d %d %d %d %d %d - %d\n",
                            m0.argList.get(0).getID(),
                            m0.argList.get(1).getID(),
                            m0.argList.get(2).getID(),
                            m0.argList.get(3).getID(),
                            m0.argList.get(4).getID(),
                            m0.argList.get(5).getID(),
                            m0.argList.get(6).getID(),
                            m1.argList.get(0).getID());
                }
            }
            else if ( ( m0.toString().compareTo(testString0) != 0 ) ||
                      ( m1.toString().compareTo(testString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( m0.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected m0.toString)(): \"%s\"\n",
                                         m0.toString());
                    }

                    if ( m1.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf("Unexpected m1.toString)(): \"%s\"\n",
                                         m1.toString());
                    }
                }
            }
            else if ( ( m0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( m1.toDBString().compareTo(testDBString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( m0.toDBString().compareTo(testDBString0) != 0 )
                    {
                       outStream.printf(
                               "Unexpected m0.toDBString)(): \"%s\"\n",
                               m0.toDBString());
                    }

                    if ( m1.toDBString().compareTo(testDBString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected m1.toDBString)(): \"%s\"\n",
                               m1.toDBString());
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* Matrix::TestToStringMethods() */


    /**
     * VerifyArgListAssignment()
     *
     * Verify that the specified replacement of an argument list
     * entry succeeds.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyArgListAssignment(Matrix target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( old_dv.getItsFargID() == DBIndex.INVALID_ID ) ||
             ( new_dv == null ) ||
             ( new_dv != newArg ) ||
             ( old_dv.getItsFargID() != new_dv.getItsFargID() ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "initial %s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( old_dv.getItsFargID() == DBIndex.INVALID_ID )
                {
                    outStream.printf("initial %s.getArg(%d).getItsFargID() " +
                            "returned INVALID_ID.\n",
                            targetDesc, idx);
                }

                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.replaceArg(%d, %s) failed to complete.\n",
                            targetDesc, idx, newArgDesc);
                }

                if ( new_dv != newArg )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( old_dv.getItsFargID() != new_dv.getItsFargID() )
                {
                    outStream.printf("unexpected itsFargID after %s.replace" +
                            "Arg(%d, %s). old = %d, new = %d\n",
                            targetDesc, idx, newArgDesc,
                            old_dv.getItsFargID(), new_dv.getItsFargID());
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test failed to complete.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                            "system error(1): \"%s\"\n",
                            targetDesc, idx, newArgDesc,
                            systemErrorExceptionString);

                }
            }
        }

        if ( new_dv instanceof UndefinedDataValue )
        {
            long target_mve_ID = DBIndex.INVALID_ID;
            String old_dv_val = null;
            String new_dv_val = null;
            String farg_name = null;
            MatrixVocabElement target_mve = null;

            try
            {
                if ( old_dv instanceof UndefinedDataValue )
                {
                    old_dv_val = ((UndefinedDataValue)old_dv).getItsValue();
                }
                new_dv_val = ((UndefinedDataValue)new_dv).getItsValue();
                target_mve_ID = target.getMveID();
                target_mve = target.db.getMatrixVE(target_mve_ID);
                farg_name = target_mve.getFormalArg(idx).getFargName();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                                "system error(2): \"%s\"\n",
                                targetDesc, idx, newArgDesc,
                                systemErrorExceptionString);
            }

            if ( ( old_dv instanceof UndefinedDataValue ) &&
                 ( old_dv_val == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( new_dv_val == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) && ( old_dv_val == new_dv_val ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args sharing a string.\n",
                        targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) &&
                 ( old_dv_val.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args with different " +
                        "values: \"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, old_dv_val, new_dv_val);
                }
            }

            if ( farg_name == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test couldn't " +
                            "get untyped arg name.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( farg_name != null ) &&
                 ( old_dv_val != null ) &&
                 ( farg_name.compareTo(old_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, old_dv_val);
                }
            }

            if ( ( farg_name != null ) &&
                 ( farg_name.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, new_dv_val);
                }
            }
        }

        return failures;

    } /* VerifyArgListAssignment() */


    /**
     * VerifyArgListAsgnmntFails()
     *
     * Verify that the specified replacement of an argument list
     * entry fails.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyArgListAsgnmntFails(Matrix target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test completed.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test " +
                            "failed to throw a system error.\n",
                            targetDesc, idx, newArgDesc);

                }
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( new_dv == null ) ||
             ( new_dv != old_dv ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( new_dv != old_dv )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.getArg(%d) test failed to complete.\n",
                        targetDesc, idx);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.getArg(%d) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx,
                            systemErrorExceptionString);

                }
            }
        }

        return failures;

    } /* Matrix::VerifyArgListAsgnmntFails() */


    /**
     * VerifyMatrixCopy()
     *
     * Verify that the supplied instances of Matrix are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyMatrixCopy(Matrix base,
                                       Matrix copy,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String baseDesc,
                                       String copyDesc)
    {
        int failures = 0;
        int i;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyMatrixCopy: %s null on entry.\n", baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyMatrixCopy: %s null on entry.\n", copyDesc);
        }
        else if ( base == copy )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.db != copy.db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.mveID != copy.mveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.mveID != %s.mveID.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.varLen != copy.varLen )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.varLen != %s.varLen.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.argList == copy.argList )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList == %s.argList.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.argList == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList == null.\n", baseDesc);
            }
        }
        else if ( copy.argList == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList == null.\n", copyDesc);
            }
        }
        else if ( base.argList.size() != copy.argList.size() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList.size() == %s.argList.size().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
            }
        }
        else
        {
            i = 0;
            while ( ( i < base.argList.size() ) && ( failures == 0 ) )
            {
                failures += DataValue.VerifyDVCopy(base.argList.get(i),
                                          copy.argList.get(i),
                                          outStream,
                                          verbose,
                                          baseDesc + ".argList.get(" + i + ")",
                                          copyDesc + ".argList.get(" + i + ")");
                i++;
            }
        }

        return failures;

    } /* Matrix::VerifyMatrixCopy() */

} /* class Matrix */
