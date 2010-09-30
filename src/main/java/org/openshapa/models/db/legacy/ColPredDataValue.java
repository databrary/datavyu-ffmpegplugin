package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;

/**
 * An instance of ColPredDataValue is used to store a column predicate data
 * value that has been assigned to a formal argument.
 *
 * Recall that a column predicate is the predicate implied by a
 * MatrixVocabElement.
 */
public final class ColPredDataValue extends DataValue {

    /** Instance of ColPred containing a representation of the current value
     * assigned to the predicate. */
    protected ColPred itsValue = null;

    /*
     * minVal & maxVal don't appear in PredDataValue as a subrange of predicates
     * is expressed as a set of allowed predicates.  Given the potential size of
     * this set, we don't keep a copy of it here -- referring directly to the
     * associated formal argument when needed instead.
     */

    /**
     * Creates an empty ColPredDataValue.
     *
     * @param db The parent database for this datavalue.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create the
     * ColPredDataValue
     *
     * @date 2008/08/10
     */
    public ColPredDataValue(Database db) throws SystemErrorException {
        super(db);

        this.setItsValue(null);

    }

    /**
     * Creates an empty ColPredDataValue.
     *
     * @param db The parent database that this data value resides within.
     * @param fargID The ID of formal argument that this data value belongs too.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create the
     * ColPredDataValue
     *
     * @date 2008/08/10
     */
    public ColPredDataValue(Database db, long fargID)
    throws SystemErrorException {
        super(db);

        this.setItsValue(null);

        this.setItsFargID(fargID);

    }

    /**
     * Creates a ColPredDataValue.
     *
     * @param db The parent database that this data value resides within.
     * @param fargID The ID of formal argument that this data value belongs too.
     * @param value The value to use for this new ColPredDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create the
     * ColPredDataValue.
     *
     * @date 2008/08/10
     */
    public ColPredDataValue(Database db, long fargID, ColPred value)
    throws SystemErrorException {
        super(db);

        this.setItsValue(value);

        this.setItsFargID(fargID);

    }

    /**
     * Copy constructor.
     *
     * @param dv The ColPredDataValue to create a copy of.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create the
     * ColPredDataValue.
     *
     * @date 2008/08/10
     */
    public ColPredDataValue(ColPredDataValue dv) throws SystemErrorException {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new ColPred(dv.itsValue);
        }

    }

    /**
     * Copy constructor,
     *
     * @param dv The ColPredDataValue to create a copy of.
     * @param blindCopy If true, the PredDataValue is copied without reference
     * to the pve's underlying any predicates.  This is necessary if a pve has
     * changed, and we need a copy of the old predicate so we can touch it up
     * for changes in the associated pve.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create the
     * ColPredDataValue
     *
     * @date 2008/08/10
     */
    protected ColPredDataValue(ColPredDataValue dv, boolean blindCopy)
    throws SystemErrorException {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new ColPred(dv.itsValue, blindCopy);
        }

    }

    /**
     * Creates a new copy of the object.
     *
     * @return A duplicate of this object.
     *
     * @throws java.lang.CloneNotSupportedException If the clone interface has
     * not been implemented.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ColPredDataValue clone;
        try {
            clone = new ColPredDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /**
     * @return A copy of the current value of the data value.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to get the
     * ColPred of this ColPredDataValue.
     *
     * @date 2008/08/16
     */
    public ColPred getItsValue() throws SystemErrorException {
        return new ColPred(this.itsValue);
    }

    /**
     * @return A blind copy of the current value of the data value. This is
     * necessary if we are adjusting for a change in the definition of a
     * predicate, as a change in a predicate's argument list may cause the
     * normal sanity checks to fail.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to create a blind
     * copy of the underlying ColPred.
     *
     * @date 2007/08/16
     */
    protected ColPred getItsValueBlind() throws SystemErrorException {
        return new ColPred(this.itsValue, true);
    }

    /**
     * @return The matrix vocab element ID of the underlying col pred value.
     *
     *
     * @throws org.openshapa.db.SystemErrorException If unable to get the matrix
     * vocab element id of the value.
     *
     * @date 2008/08/10
     */
    protected long getItsValueMveID() throws SystemErrorException {
        long pveID = DBIndex.INVALID_ID;

        if ( this.itsValue != null )
        {
            pveID = this.itsValue.getMveID();
        }

        return pveID;

    }

    /**
     * Sets the value of the ColPredDataValue to the supplied value.
     *
     * @param value The new value to use for this ColPredDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to set the new
     * value for this ColPredDataValue.
     */
    public void setItsValue(ColPred value) throws SystemErrorException {
        final String mName = "ColPredDataValue::setItsValue(): ";

        if ( ( value != null ) &&
             ( value.getDB() != this.getDB() ) )
        {
            throw new SystemErrorException(mName + "value.getDB() != this.db");
        }

        if ( ( value != null ) &&
             ( value.getMveID() != DBIndex.INVALID_ID ) &&
             ( ! getDB().vl.matrixInVocabList(value.getMveID()) ) )
        {
            throw new SystemErrorException(mName +
                    "! db.vl.matrixInVocabList(value.getMatrixID())");
        }

        if ( ( value == null ) ||
             ( value.getMveID() == DBIndex.INVALID_ID ) )
        {
            this.itsValue = new ColPred(this.getDB());
        }
        else
        {
            this.itsValue = new ColPred(value);
        }

        this.valueSet();
        return;

    }

    /**
     * @return true if the value equals the default value
     */
    public boolean isDefault() {
        return itsValue == null;
    }

    /**
     * Clears the ID of this ColPredDataValue (resets it to INVALID_ID).
     *
     * @throws org.openshapa.db.SystemErrorException If unable to clear the ID
     * of this ColPredDataValue
     *
     * @date 2008/02/19
     */
    @Override
    protected void clearID() throws SystemErrorException {
        super.clearID();

        if ( this.itsValue != null )
        {
            this.itsValue.clearID();
        }

        return;

    }

    /**
     * Inserts this ColPredDataValue into nomindated data cell.
     *
     * @param DCID The ID of the data cell which you wish to insert this
     * ColPredDataValue into.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to insert this
     * ColPredDataValue into a data cell.
     *
     * @date 2008/08/10
     */
    @Override
    protected void insertInIndex(long DCID) throws SystemErrorException {
        final String mName = "ColPredDataValue::insertInIndex(): ";

        super.insertInIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.insertInIndex(DCID);

        return;

    }

    /**
     * Removes this ColPredDataValue from its parent Data cell.
     *
     * @param DCID The ID of the data cell which you wish to remove this
     * ColPredDataValue from.
     *
     * @throws org.openshapa.db.SystemErrorException If unable to remove this
     * from its parent data cell.
     *
     * @date 2008/08/10
     */
    @Override
    protected void removeFromIndex(long DCID) throws SystemErrorException {
        final String mName = "ColPredDataValue::removeFromIndex(): ";

        super.removeFromIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.removeFromIndex(DCID);

        return;

    }


    /**
     * replaceInIndex()
     *
     * Call the super, and then pass an update index for replacement message
     * down to the predicate.
     *
     *                                           -- 2/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void replaceInIndex(DataValue old_dv,
                                  long DCID,
                                  boolean cascadeMveMod,
                                  boolean cascadeMveDel,
                                  long cascadeMveID,
                                  boolean cascadePveMod,
                                  boolean cascadePveDel,
                                  long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::replaceInIndex(): ";
        ColPredDataValue old_cpdv;

        super.replaceInIndex(old_dv,
                             DCID,
                             cascadeMveMod,
                             cascadeMveDel,
                             cascadeMveID,
                             cascadePveMod,
                             cascadePveDel,
                             cascadePveID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        if ( ! ( old_dv instanceof ColPredDataValue ) )
        {
            throw new SystemErrorException(mName + "old_dv not a pred dv?!?");
        }

        old_cpdv = (ColPredDataValue)old_dv;

        if ( old_cpdv.itsValue == null )
        {
            throw new SystemErrorException(mName +
                    "old_cpdv.itsValue is null?!?");
        }

        this.itsValue.updateIndexForReplacement(old_cpdv.itsValue,
                                                DCID,
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);

        return;

    }

    /**
     * @return A string representation of the ColPredDataValue.
     *
     * @date 2008/08/10
     */
    public String toString() {
        if ( this.itsValue == null )
        {
            return("()");
        }
        else
        {
            return (this.itsValue.toString());
        }
    }

    public String toEscapedString() {
        if (this.itsValue == null) {
            return "()";
        } else {
            return StringUtils.escapeCSV(this.itsValue.toString());
        }
    }

    /**
     * @return A database string representation of the DBValue for comparision
     * against the databass's expected value (for debugging purposes).
     *
     * @date 2008/08/15
     */
    public String toDBString() {
        final String mName = "ColPredDataValue::toDBString(): ";

        if ( ( this.itsValue == null ) ||
             ( this.itsValue.getMveID() == DBIndex.INVALID_ID ) )
        {
            return ("(ColPredDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue ()" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(ColPredDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + this.itsValue.toDBString() +
                    ") (subRange " + this.subRange + "))");
        }

    } /* ColPredDataValue::toDBString() */


    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will an instantiation of <pred_cell_value>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     *                                              1/30/09
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(java.io.PrintStream output)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "ColPredDataValue::toMODBFile()";
        DBElement dbe = null;
        FormalArgument farg = null;
        VocabElement ve = null;
        MatrixVocabElement mve = null;
        StringBuilder tmp = new StringBuilder("");

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        // In the context of MacSHAPA ODB files, column predicates are
        // interchangeable with regular predicates, and empty predicates only
        // appear as the values of cells in a predicate type -- where they are
        // represented as "()".  At there are no typed formal arguments in
        // MacSHAPA, an empty predicate or column predicate is simply replaced
        // with the MacSHAPA equivalent of an undefined data value.
        //
        // Thus, since OpenSHAPA does not allow column predicates to appear
        // as the values of predicate cells, and since there should be no
        // column predicate formal arguments appearing in this context, we
        // really should be able to just throw a system error exception if
        // we ever see an empty column predicate data value.
        //
        // However, for a short time, we have been pressured into allowing
        // typed formal arguments in predicate and MatrixVocabElemenst of
        // type matrix -- which raises the possibility of undefined predicates
        // appearing in these cases.
        //
        // Where such behaviour is allowed, we must handle it gracefully,
        // replacing the undefined predicates with the formal argument name
        // in MacSHAPA ODB files.
        //
        // Where such behaviour is not allowed, we must flag an error.
        //
        // This matter is complicated by the fact that empty column predicates
        // can be represented two ways -- either by setting this.itsValue to
        // null in the predicate vocab element, or by setting pveID to the
        // invalid index in the target predicate.
        //
        // However, it is a bit simpler than the predicate data value case, as
        // there are no "column predicate columns", and thus we always represent
        // the empty column predicate with the name of the formal argument it
        // is replacing.

        if ( this.itsFargID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                  "itsFargID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(this.itsFargID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName +
                                           "itsFargID has no referent");
        }

        if ( ! ( dbe instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName + "itsFargID doesn't " +
                                           "refer to a formal argument");
        }

        farg = (FormalArgument)dbe;

        if ( ( farg.getFargType() != FormalArgument.FArgType.UNTYPED ) &&
             ( farg.getFargType() != FormalArgument.FArgType.COL_PREDICATE ) )
        {
            throw new SystemErrorException(mName + "Encountered " +
                    "argument / formal argument type mismatch");
        }

        // We can make this test here, since there are no column predicate
        // columns in OpenSHAPA.  Needless to say, that will cease to be the
        // case should we ever implement them.
        if ( ( ! this.getDB().typedFormalArgsSupported() ) &&
             ( farg.getFargType() != FormalArgument.FArgType.COL_PREDICATE ) )
        {
            throw new SystemErrorException(mName + "Encountered typed formal" +
                    "argument in a matrix or column predicate in a database " +
                    "in which typed formal arguments are not supported.");
        }

        if ( this.itsValue != null )
        {
            this.itsValue.toMODBFile(output, farg.getFargName());
        }
        else
        {
            output.printf("|%s| ", farg.getFargName());
        }

        return;

    } /* ColPredDataValue::toMODBFile() */


    /**
     * toMODBFile_update_local_vocab_list() -- OVERRIDE
     *
     * If the column predicate data value is defined, pass the
     * toMODBFile_update_local_vocab_list() message on to the instance of
     * colPred.  Otherwise do nothing.
     *
     *                                      7/2/09
     *
     * Changes;
     *
     *    - None.
     *
     * @param dc
     * @throws org.openshapa.db.SystemErrorException
     */

    protected void
    toMODBFile_update_local_vocab_list(DataColumn dc)
        throws SystemErrorException
    {
        final String mName =
                "ColPredDataValue::toMODBFile_update_local_vocab_list(): ";

        if ( this.itsValue != null )
        {
            this.itsValue.toMODBFile_update_local_vocab_list(dc);
        }

        return;

    } /* ColPredDataValue::toMODBFile_update_local_vocab_list() */


    /**
     * updateForMVEDefChange()
     *
     * If the associated column predicate is defined, pass an update for
     * matrix vocab element definition change message to it.
     *
     *                                           -- 8/10/08
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
        final String mName = "ColPredDataValue::updateForMVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        dbe = this.getDB().idx.getElement(mveID);

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "mveID doesn't refer to a mve.");
        }


        if ( this.itsValue != null )
        {
            this.itsValue.updateForMVEDefChange(db,
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

        return;

    } /* ColPredDataValue::updateForPVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * If the associated column predicate is defined, pass an update for
     * matrix vocab element deletion message to it.
     *
     *                                           -- 8/10/08
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

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        if ( this.itsValue != null )
        {
            this.itsValue.updateForMVEDeletion(db, mveID);
        }

        return;

    } /* ColPredDataValue::updateForMVEDeletion() */


    /**
     * updateForMVEDefChange()
     *
     * If the associated column predicate is defined, pass an update for
     * predicate vocab element definition change message to it.
     *
     *                                           -- 8/10/08
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
        final String mName = "ColPredDataValue::updateForPVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        dbe = this.getDB().idx.getElement(pveID);

        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "pveID doesn't refer to a pve.");
        }


        if ( this.itsValue != null )
        {
            this.itsValue.updateForPVEDefChange(db,
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

        return;

    } /* ColPredDataValue::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * If the associated column predicate is defined, pass an update for
     * predicate vocab element deletion message to it.
     *
     *                                           -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long pveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForMVEDeletion(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        if ( this.itsValue != null )
        {
            this.itsValue.updateForPVEDeletion(db, pveID);
        }

        return;

    } /* ColPredDataValue::updateForPVEDeletion() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                           -- 3/22/08
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
        final String mName = "ColPredDataValue::updateForFargChange(): ";

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

    } /* ColPredDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if any) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.  For now, we simply
     * coerce to the empty predicate if subrange is true and the current
     * value isn't in the list of approved predicates.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                           -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof ColPredFormalArg )
        {
            this.subRange = false;
        }
        else if ( fa instanceof UnTypedFormalArg )
        {
            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* ColPredDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *                                               -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public ColPred coerceToRange(ColPred value)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::coerceToRange(): ";
        DBElement dbe;
        ColPredFormalArg pfa;
        ColPred retVal;

        if ( value == null )
        {
            retVal = new ColPred(this.getDB());
        }
        else if ( value.getMveID() == DBIndex.INVALID_ID )
        {
            retVal = value;
        }
        else
        {
            retVal = value;
        }

        return retVal;

    } /* ColPredDataValue::coerceToRange() */


    /**
     * deregisterPreds()
     *
     * If this.itsValue isn't null, call its deregisterWithMve() method to
     * cause it to deregister with its associated MVE (if any), and pass
     * the message down to any column predicates or predicates that may
     * appear in its argument list.
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterPreds(boolean cascadeMveDel,
                                   long cascadeMveID,
                                   boolean cascadePveDel,
                                   long cascadePveID)
        throws SystemErrorException
    {
        if ( this.itsValue != null )
        {
            this.itsValue.deregisterWithMve(cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveDel,
                                            cascadePveID);
        }

        return;

    } /* ColPredDataValue::deregisterPreds() */


    /**
     * registerPreds()
     *
     * If this.itsValue isn't null, call its registerWithMve() method to
     * cause it to register with its associated MVE (if any), and pass
     * the message down to any column predicates or predicates that may
     * appear in its argument list.
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerPreds()
        throws SystemErrorException
    {
        if ( this.itsValue != null )
        {
            this.itsValue.registerWithMve();
        }

        return;

    } /* ColPredDataValue::registerPreds() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

        return hash;
    }

    /**
     * Compares this ColPredDataValue against another object.
     * Assumption: ColPredDataValues are not equal just because their id fields
     * match. This function will test that db, id and lastModUID all match.
     * If id can be proved to be enough for testing equality we should
     * implement a simpler, faster version.
     *
     * @param obj The object to compare this against.
     * @return true if the Object obj is logically equal to this.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        ColPredDataValue c = (ColPredDataValue) obj;
        return super.equals(obj)
            && (this.itsValue == null ? c.itsValue == null
                                      : this.itsValue.equals(c.itsValue));
    }

    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of ColPredDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed ColPredDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static ColPredDataValue Construct(Database db,
                                             ColPred cp)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::Construct(db, cp)";
        ColPredDataValue cpdv = null;

        cpdv = new ColPredDataValue(db);

        if ( cpdv != null )
        {
            cpdv.setItsValue(cp);
        }

        return cpdv;

    } /* ColPredDataValue::Construct(db, cp) */

} /* ColPredDataValue */
