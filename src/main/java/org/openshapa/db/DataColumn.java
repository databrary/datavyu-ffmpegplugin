/*
 * DataColumn.java
 *
 * Created on December 14, 2006, 7:15 PM
 *
 */
package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.HashSet;
import java.util.Vector;

/**
 * Class DataColumn
 *
 * Instances of DataColumn are used to implement text, nominal, float, integer,
 * predicate, and matrix columns (AKA spreadsheet variables) in the database.
 *
 *                                                  -- 8/29/07
 */
public class DataColumn extends Column
        implements InternalMatrixVocabElementListener {

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /* macshapaColWidth: The width of the column used to display this data
     *      column in MacSHAPA.
     *
     *      This field is never used in OpenSHAPA.  It exists purely to allow
     *      use to store the column width associated with a data column in a
     *      MacSHAPA ODB database for later use should we have to write the
     *      database back out in MacSHAPA ODB format.
     *
     * itsMveID:   Long containing the ID of the matrix vocab element
     *      that defines the format of the cell.  Note that the
     *
     * itsMveType: matrixType indicating the type of the associated
     *      matrix vocab element.
     *
     * itsCells: Reference to a vector of DataCell containing the cells
     *      of the column.  This Vector is created when the column is inserted
     *      into the column list, and not copied in the copy constructor.
     *
     * varLen:  Boolean flag indicating whether the associated matrix is
     *      variable length.
     *
     * listeners: Instance of DataColumnListeners containing references to
     *      internal and external objects that must be notified when the
     *      data column is modified.
     *
     * pending:  During a cascade of changes, this field is used to store
     *      a reference to a modified version of the cannonical instance of
     *      the DataColumn, which will become the cannonical at the end of
     *      the cascade.
     *
     * localVocabIDSet: When writing a MacSHAPA ODB format database file, we
     *      must construct a list of all the predicates and column predicates
     *      that appear in the DataColumn.  This instance of HashSet is used
     *      for this purpose.  The field will always be null unless we are
     *      in the process of writing the contents of the column to a MacSHAPA
     *      ODB file.
     */

    /** Width of the column in a MacSHAPA spreadsheet */
    protected int macshapaColWidth = MacshapaDatabase.DEFAULT_COLUMN_WIDTH;

    /** ID of associated matrix VE */
    private long itsMveID = DBIndex.INVALID_ID;

    /** Type of associated matrix VE */
    private MatrixVocabElement.MatrixType itsMveType =
            MatrixVocabElement.MatrixType.UNDEFINED;

    /** Vector of DataCells for Column */
    private Vector<DataCell> itsCells = null;

    /** Whether arg list is variable length */
    private boolean varLen = false;

    /**
     * reference to instance of DataColumnListeners used to maintain lists of
     * listeners, and notify them as appropriate.
     */
    protected DataColumnListeners listeners = null;
    /**
     * Reference to a modified version of the cannonical version of the
     * DataColumn.  Any such version will be created during a cascade, and
     * will become the cannonical version at the end of the cascade.
     */
    private DataColumn pending = null;

    /**
     * Reference to a set of long used to assemble a list of all the predicates
     * and column predicates that appear in this data column.  This field will
     * always be null except for a brief period while writing the contents of
     * the data column to a MacSHAPA ODB file, and then only if this column
     * is of matrix or predicate type.
     */
    private HashSet<Long> localVocabIDSet = null;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Long2H(itsMveID) * Constants.SEED1;
        hash += itsMveType.hashCode() * Constants.SEED2;
        hash += HashUtils.Obj2H(itsCells) * Constants.SEED3;
        hash += (new Boolean(varLen)).hashCode() * Constants.SEED4;
        hash += HashUtils.Obj2H(listeners) * Constants.SEED5;
        hash += HashUtils.Obj2H(pending) * Constants.SEED6;

        return hash;
    }

    /**
     * Compares this DataColumn against another.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this, false
     * otherwise.
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
        DataColumn dc = (DataColumn) obj;

        return super.equals(obj) && itsMveID == dc.itsMveID && itsMveType == dc.itsMveType && itsCells == null ? dc.itsCells == null
                : itsCells.equals(dc.itsCells) && varLen == dc.varLen && listeners == null ? dc.listeners == null
                : dc.listeners.equals(listeners) && pending == null ? dc.pending == null
                : dc.pending.equals(pending);
    }

    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    /**
     * DataColumn()
     *
     * Constructors for instances of DataColumn.
     *
     * Three versions of this constructor.
     *
     * The first takes only a reference to a database, a name, and a matrixType
     * as its parameters.  This is the constructor that will typically be
     * used when a new column is created in the spreadsheet.  In this case
     * the initial MatrixVocabElement will be created for the DataColumn
     * when it is inserted into the Database.
     *
     * The second takes a reference to a database, a name, initial values
     * for the  hidden and readOnly fields, and a MatrixVocabElement ID.
     * This constructor is intended for use when loading a Database from
     * file.  It presumes that the associated MatrixVocabElement has already
     * been created and inserted in the vocab list.
     *
     *  The third takes an instance of DataColum as its parameter, and returns
     *  a copy.  Note that the itsCells field is NOT copied.
     *
     *                                              -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public DataColumn(Database db,
            String name,
            MatrixVocabElement.MatrixType type)
            throws SystemErrorException {
        super(db);

        final String mName = "DataColumn::DataColumn(db, name, type): ";

        this.setName(name);

        if ((type == MatrixVocabElement.MatrixType.FLOAT) ||
                (type == MatrixVocabElement.MatrixType.INTEGER) ||
                (type == MatrixVocabElement.MatrixType.MATRIX) ||
                (type == MatrixVocabElement.MatrixType.NOMINAL) ||
                (type == MatrixVocabElement.MatrixType.PREDICATE) ||
                (type == MatrixVocabElement.MatrixType.TEXT)) {
            this.itsMveType = type;
        } else {
            throw new SystemErrorException(mName + "invalid type");
        }
    } /* DataColumn::DataColumn(db, name, type) */


    public DataColumn(Database db,
            String name,
            boolean hidden,
            boolean readOnly,
            long mveID)
            throws SystemErrorException {
        super(db);

        final String mName =
                "DataColumn::DataColumn(db, name, hidden, readOnly, mveID): ";
        MatrixVocabElement mve;

        mve = this.lookupMatrixVE(mveID);

        if (name == null) {
            throw new SystemErrorException(mName + "name null on entry.");
        }

        if (name.compareTo(mve.getName()) != 0) {
            throw new SystemErrorException(mName + "name doesn't match mve");
        }

        if (db.cl.inColumnList(name)) {
            throw new SystemErrorException(mName +
                    "name already appears in column list");
        }

        this.itsMveID = mveID;

        this.name = new String(name);

        this.hidden = hidden;

        this.readOnly = readOnly;

        this.itsMveType = mve.getType();

        this.varLen = mve.getVarLen();

    } /* DataColumn::DataColumn(db, name, hidden, readOnly, mveID) */


    public DataColumn(DataColumn dc)
            throws SystemErrorException {
        super((Column) dc);

        // TODO: add sanity checking??
        this.itsCells = null;
        this.itsMveID = dc.itsMveID;
        this.itsMveType = dc.itsMveType;
        this.varLen = dc.varLen;

    } /* DataColumn::DataColumn(dc) */


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
        DataColumn clone = (DataColumn) super.clone();

        try {
            clone = new DataColumn(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/
    /**
     * getItsCells() & setItsCells()
     *
     * Get and set the current value of itsCells.  Note that these methods
     * are protected and should only be called from within the openshapa.db
     * package.  We will use them to transfer the Vector of cells from one
     * incarnation of the ReferenceColumn header to the next.
     *
     * Update numCells in passing.
     *
     *                                              -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected Vector<DataCell> getItsCells() {

        return this.itsCells;

    } /* DataColumn::getItsCells() */


    protected void setItsCells(Vector<DataCell> cells)
    {

        this.itsCells = cells;

        if (this.itsCells == null)
        {
            this.numCells = 0;
        } 
        else
        {
            this.numCells = this.itsCells.size();
        }

    } /* DataColumn::setItsCells(cells) */


    /**
     * getMacshapaColWidth() & setMacshapaColWidth()
     *
     * Get or set the current value of the macshapaColWidth field.
     *
     * Observe that both of these accesors are protected.  For now at least,
     * the macshapaColWidth field is intended only to store the MacSHAPA
     * width of a column loaded from a MacSHAPA ODB file, so the same width
     * can be written out again should we have to write a modified MacSHAPA
     * ODB file.
     *
     * Should this ever change, we will have to integrate macshapaColWidth
     * field into the listeners, so that they will be informed when the
     * field changes.
     *
     *                                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    protected int getMacshapaColWidth()
    {

        return this.macshapaColWidth;

    } /* "DataColumn::getMacshapaColWidth() */


    protected void setMacshapaColWidth(int newColWidth)
        throws SystemErrorException
    {
        final String mName = "DataColumn::setMacshapaColWidth()";

        if ( ( newColWidth < MacshapaDatabase.MIN_COLUMN_WIDTH ) ||
             ( newColWidth > MacshapaDatabase.MAX_COLUMN_WIDTH ) )
        {
            throw new SystemErrorException(mName + "newColWidth out of range.");
        }

        this.macshapaColWidth = newColWidth;

        return;

    } /* DataColumn::setMacshapaColWidth() */


    /**
     * getItsMveID() & setItsMveID()
     *
     * Get or set the current value of the itsMveID field.  Observe that
     * setItsMveID() is protected -- it should only be used within the
     * openshapa.db package.  Also the method can only be called once, and
     * may not be used to set itsMveID to the INVALID_ID.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */
    public long getItsMveID()
    {

        return this.itsMveID;

    } /* DataColumn::getItsMveID() */


    protected void setItsMveID(long mveID)
            throws SystemErrorException
    {
        final String mName = "DataColumn::setItsMveID(): ";
        MatrixVocabElement mve;

        if (this.itsMveType == MatrixVocabElement.MatrixType.UNDEFINED)
        {
            throw new SystemErrorException(mName +
                    "this.itsMveType undefined on entry.");
        }

        if (itsMveID != DBIndex.INVALID_ID)
        {
            throw new SystemErrorException(mName + "itsMveID already set");
        } 
        else if (mveID == DBIndex.INVALID_ID)
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        mve = this.lookupMatrixVE(mveID);

        if (mve.getItsColID() != DBIndex.INVALID_ID)
        {
            throw new SystemErrorException(mName +
                    "target mve already assigned to a column");
        }

        if (mve.getType() != this.itsMveType)
        {
            throw new SystemErrorException(mName +
                    "target mve type doesn't match this.itsMveType");
        }

        this.itsMveID = mveID;
        this.varLen = mve.getVarLen();

        return;

    } /* DataColumn::setItsMveID(mveID) */


    /**
     * getItsMveType
     *
     * Return the current value of the itsMveType field.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */
    public MatrixVocabElement.MatrixType getItsMveType()
    {

        return this.itsMveType;

    } /* DataColumn::getItsMveType() */


    /**
     * getVarLen()
     *
     * Return the current value of the varLen field.
     *
     *                          -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */
    public boolean getVarLen()
    {

        return this.varLen;

    } /* DataColumn::getVarLen() */


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    /**
     * constructItsCells()
     *
     * Allocate the Vector of DataCell used to store cells.  This method
     * should only be called when the DataColumn is being inserted in the
     * column list.
     *
     *                                          -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected void constructItsCells()
            throws SystemErrorException {
        final String mName = "DataColumn::constructItsCells(): ";

        if (this.itsCells != null) {
            throw new SystemErrorException(mName +
                    "itsCells already allocated?");
        }

        // TODO: add more sanity checks?

        this.itsCells = new Vector<DataCell>();

        return;

    } /* DataColumn::constructItsCells() */


    /**
     * deregister()
     *
     * De-register as an internal listener with the associated instance of
     * MatrixVocabElement in the vocab list.  Note that this presumes that
     * this.mveID is defined on entry.
     *
     * This method should be only be called just before a data column is
     * removed from the column list.
     *
     *                                              -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void deregister()
            throws SystemErrorException
    {
        final String mName = "DataColumn::deregister(): ";
        DBElement dbe = null;
        MatrixVocabElement mve;

        if (this.itsMveID == DBIndex.INVALID_ID)
        {
            throw new SystemErrorException(mName +
                    "this.itsMveID is invalid");
        }

        dbe = this.getDB().idx.getElement(this.itsMveID);

        if (dbe == null)
        {
            throw new SystemErrorException(mName +
                    "this.itsMveID has no referent");
        }

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                    "this.itsMveID does not refer to a MatrixVocabElement");
        }

        mve = (MatrixVocabElement) dbe;

        mve.deregisterInternalListener(this.getID());

        return;

    } /* DataColumn::deregister() */


    /**
     * register()
     *
     * Register as an internal listener with the associated instance of
     * MatrixVocabElement in the vocab list.  Note that this presumes that
     * this.mveID is defined on entry.
     *
     * This method should be only be called just after a newly cleared
     * data column is inserted into the column list.
     *
     *                                              -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void register()
            throws SystemErrorException {
        final String mName = "DataColumn::register(): ";
        DBElement dbe = null;
        MatrixVocabElement mve;

        if (this.itsMveID == DBIndex.INVALID_ID) {
            throw new SystemErrorException(mName +
                    "this.itsMveID is invalid");
        }

        dbe = this.getDB().idx.getElement(this.itsMveID);

        if (dbe == null) {
            throw new SystemErrorException(mName +
                    "this.itsMveID has no referent");
        }

        if (!(dbe instanceof MatrixVocabElement)) {
            throw new SystemErrorException(mName +
                    "this.itsMveID does not refer to a MatrixVocabElement");
        }

        mve = (MatrixVocabElement) dbe;

        mve.registerInternalListener(this.getID());

        return;

    } /* DataColumn::register() */


    /**
     * toDBString()
     *
     * Returns a String representation of the DataColumn for comparison
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
    public String toDBString() {
        String s;

        try {
            s = "(DataColumn (name " + this.name +
                    ") (id " + this.getID() +
                    ") (hidden " + this.hidden +
                    ") (readOnly " + this.readOnly +
                    ") (itsMveID " + this.itsMveID +
                    ") (itsMveType " + this.itsMveType +
                    ") (varLen " + this.varLen +
                    ") (numCells " + this.numCells + ") " +
                    this.itsCellsToDBString() + "))";
        } catch (SystemErrorException e) {
            s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* DataColumn::toDBString() */


    /**
     * toString()
     *
     * Returns a String representation of the ReferenceColumn for display.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toString() {
        String s;

        try {
            s = "(" + this.getName() + ", " + this.itsCellsToString() + ")";
        } catch (SystemErrorException e) {
            s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* DataColumn::toString() */


    /*************************************************************************/
    /************************* Cascade Management: ***************************/
    /*************************************************************************/
    /**
     * addPending()
     *
     * Add the specified cell to the pending set.  Note that the instance of
     * Cell MUST be the current cannonical incarnation.  This should be verified
     * by the subclass.
     *
     *                                              -- 3/6/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void addPending(Cell c)
            throws SystemErrorException {
        final String mName = "DataColumn::addPending(c): ";

        if (c == null) {
            throw new SystemErrorException(mName + "c null on entry");
        }

        if (c.getItsColID() != this.getID()) {
            throw new SystemErrorException(mName + "col ID mismatch");
        }

        if (!(c instanceof DataCell)) {
            throw new SystemErrorException(mName + "c not a DataCell");
        }


        if (!this.cascadeInProgress) {
            throw new SystemErrorException(mName +
                    "call to addPending() when cascade not in progress.");
        }

        if (this.getDB() != c.getDB()) {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if (this.getCell(c.getOrd()) != c) {
            throw new SystemErrorException(mName +
                    "c not the cannonical instance(1).");
        }

        if (this.getDB().idx.getElement(c.getID()) != c) {
            throw new SystemErrorException(mName +
                    "c not the cannonical instance(2).");
        }

        if (((DataCell) c).listeners == null) {
            throw new SystemErrorException(mName +
                    "c not the cannonical instance(3).");
        }

        super.addPending(c);

        return;

    } /* DataColumn::addPending() */


    /**
     * cascadeReplaceCell()
     *
     * Replace the supplied old cell with the supplied new cell as the
     * cannonical incarnation of the cell.
     *
     * This method may be called by cells in the pending list in response
     * to an exitCascade call.
     *
     *                                              -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void cascadeReplaceCell(DataCell oldCell,
            DataCell newCell)
            throws SystemErrorException {
        final String mName = "DataColumn::cascadeReplaceCell(): ";
        int ord;
        int i;

        if (!this.cascadeInProgress) {
            throw new SystemErrorException(mName +
                    "cascadeInProgress is false?!?!?");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        if ((oldCell == null) || (newCell == null)) {
            throw new SystemErrorException(mName +
                    "oldCell or newCell null on entry");
        }

        if ((oldCell.getDB() != this.getDB()) ||
                (newCell.getDB() != this.getDB())) {
            throw new SystemErrorException(mName + "db mismatch");
        }

        if (oldCell.getID() != newCell.getID()) {
            throw new SystemErrorException(mName + "cell id mismatch");
        }

        if (this.getDB().idx.getElement(oldCell.getID()) != oldCell) {
            throw new SystemErrorException(mName + "oldCell not cannonical(1)");
        }

        if (oldCell.getListeners() == null) {
            throw new SystemErrorException(mName + "oldCell not cannonical(2)");
        }

        ord = newCell.getOrd();
        if (this.itsCells.get(ord - 1) != oldCell) {
            throw new SystemErrorException(mName +
                    "oldCell not at newCell.getOrd().");
        }

        if (!this.validCell(newCell, false)) {
            throw new SystemErrorException(mName + "invalid cell");
        }

        newCell.validateReplacementCell(oldCell);

        /* Move the listeners from the old incarnation to the new */
        newCell.setListeners(oldCell.getListeners());
        oldCell.setListeners(null);

        /* replace the old incarnation with the new */
        if (oldCell != this.itsCells.set(ord - 1, newCell)) {
            throw new SystemErrorException(mName +
                    "unexpected return from set()");
        }

        // verify ord of new cell
        if (itsCells.get(newCell.getOrd() - 1) != newCell) {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        if ((this.itsMveType == MatrixVocabElement.MatrixType.MATRIX) ||
                (this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE)) {
            oldCell.deregisterPreds();
        }

        // update the index for the new cell value
        newCell.updateIndexForReplacementVal(oldCell);
        getDB().idx.replaceElement(newCell);

        /* Note changes between the old and new incarnations of the
         * data cell, and notify the listeners.
         */
        newCell.noteChange(oldCell, newCell);
        newCell.notifyListenersOfChange();

        if ((this.itsMveType == MatrixVocabElement.MatrixType.MATRIX) ||
                (this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE)) {
            newCell.registerPreds();
        }

        return;

    } /* DataColumn::cascadeReplaceCell() */


    /**
     * endCascade()
     *
     * Needed to implement the InternalCascadeListener interface.
     *
     * Handle the various housekeeping required to process the end
     * of a cascade of changes through the database.  Subclasses will
     * almost always override this method, and then call it from
     * within the override.
     *
     * Verify that this.cascadeInProgress is true.  Throw a system
     * it it isn't.
     *
     * If this.pendingSet is null, throw a system error.
     *
     * Then clear the pending set, set this.cascadeInProgress to false,
     * and exit.
     *
     *                                  -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */
    public void endCascade(Database db)
            throws SystemErrorException {
        final String mName = "DataColumn::endCascade(): ";

        if (this.getDB() != db) {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if (!this.cascadeInProgress) {
            throw new SystemErrorException(mName +
                    "call to endCascade() when this.cascadeInProgress is false?!?");
        }

        if (this.pendingSet == null) {
            throw new SystemErrorException(mName + "this.pendingSet is null?!?");
        }

        /* If temporal ordering, sort cells by onset, and assign new ords
         * as necessary.
         */
        if (this.getDB().temporalOrdering) {
            this.sortItsCells();
        }

        for (Cell c : this.pendingSet) {
            ((DataCell) c).exitCascade();
        }

        if (this.pending != null) {
            this.getDB().cl.replaceDataColumn(this.pending, true);

            this.pending = null;
        }

        super.endCascade(db);

        return;

    } /* column::endCascade() */


    /*************************************************************************/
    /*********************** Listener Manipulation: **************************/
    /*************************************************************************/
    /**
     * deregisterExternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister external change listeners message on to
     * the instance of DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void deregisterExternalListener(ExternalDataColumnListener el)
            throws SystemErrorException {
        final String mName = "DataColumn::deregisterExternalListener()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to add external listener to non-cannonical version.");
        }

        this.listeners.deregisterExternalListener(el);

        return;

    } /* DataColumn::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister internal change listeners message on to
     * the instance of DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void deregisterInternalListener(long id)
            throws SystemErrorException {
        final String mName = "DataColumn::deregisterInternalListener()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to add internal listener to non-cannonical version.");
        }

        this.listeners.deregisterInternalListener(id);

        return;

    } /* DataColumn::deregisterInternalListener() */


    /**
     * getListeners()
     *
     * Return the corrent value of this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected DataColumnListeners getListeners() {

        return this.listeners;

    } /* DataColumn::getListeners() */


    /**
     * noteChange()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass a note changes message on to the instance of
     * DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void noteChange(DataColumn oldDC,
            DataColumn newDC)
            throws SystemErrorException {
        final String mName = "DataColumn::noteChange()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to note changes on non-cannonical version.");
        }

        this.listeners.noteChange(oldDC, newDC);

        return;

    } /* DataColumn::noteChange() */


    /**
     * notifyListenersOfChange()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of changes message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void notifyListenersOfChange()
            throws SystemErrorException {
        final String mName = "DataColumn::notifyListenersOfChange()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to notify listeners of change on non-cannonical version.");
        }

        this.listeners.notifyListenersOfChange();

        return;

    } /* DataColumn::notifyListenersOfChange() */


    /**
     * notifyListenersOfDeletion()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of deletion message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void notifyListenersOfDeletion()
            throws SystemErrorException {
        final String mName = "DataColumn::notifyListenersOfDeletion()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to notify listeners of deletion on " +
                    "non-cannonical version.");
        }

        this.listeners.notifyListenersOfDeletion();

        return;

    } /* DataColumn::notifyListenersOfDeletion() */


    /**
     * registerExternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register external change listeners message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void registerExternalListener(ExternalDataColumnListener el)
            throws SystemErrorException {
        final String mName = "DataColumn::registerExternalListener()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to register external listener to non-cannonical version.");
        }

        this.listeners.registerExternalListener(el);

        return;

    } /* DataColumn::registerExternalChangeListener() */


    /**
     * registerInternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register internal change listeners message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void registerInternalChangeListener(long id)
            throws SystemErrorException {
        final String mName = "DataColumn::registerInternalChangeListener()";

        if (this.listeners == null) {
            throw new SystemErrorException(mName +
                    "Attempt to register internal listener to non-cannonical version.");
        }

        this.listeners.registerInternalListener(id);

        return;

    } /* DataColumn::addInternalChangeListener() */


    /**
     * setListeners()
     *
     * Set the listeners field.  Setting this.listeners to a non-null value
     * signifies that this instance of DataCell is the cannonical current
     * incarnation of the data ce;;.  Setting it back to null indicates
     * that the incarnation has been superceeded.
     *
     * If this.listeners is null, it may be set to reference an instance
     * of DataCellListeners that is associated with this data cell.  If
     * this.listeners is not null, the only permissiable new value is null.
     *
     * In all other cases, throw a system error exception.
     *
     *                                          -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void setListeners(DataColumnListeners listeners)
            throws SystemErrorException {
        final String mName = "DataCell::setListeners()";

        if (this.listeners == null) {
            if (listeners == null) {
                throw new SystemErrorException(mName +
                        ": this.listeners is already null");
            }

            this.listeners = listeners;
            this.listeners.updateItsCol(this);
        } else {
            if (listeners != null) {
                throw new SystemErrorException(mName +
                        ": this.listeners is already non-null.");
            }

            this.listeners = null;
        }

        return;

    } /* DataCell::setListeners() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/
    /**
     * constructInitMatrixVE(DataColumn dc)
     *
     * Construct the initial MatrixVocabElement associated with the DataColumn.
     * This method simply constructs the MVE and returns it.  It does not
     * insert it in the VocabList, but it does verify that the name is valid
     * and not in use.  It also verifies that the id of the column is the
     * INVALID_ID,  and also that the itsMveID field contains the INVALID_ID.
     * The purpose here is to try to verify that this DataColumn doesn't already
     * have an associated MatricVocabElement.
     *
     * This method is intended to assist in the construction of a new
     * DataColumn in response to a user request via the Spreadsheet code.
     *
     * Note that the system flag is never set on the supplied MatrixVocabElement
     * even if it must be eventually, since we can't do this until both are
     * inserted into their respective lists.
     *
     *                                              -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    protected MatrixVocabElement constructInitMatrixVE()
            throws SystemErrorException {
        final String mName = "DataColumn::constructInitMatrixVE(): ";
        FormalArgument fa = null;
        MatrixVocabElement mve = null;

        if (this.getID() != DBIndex.INVALID_ID) {
            throw new SystemErrorException(mName + "dc.id != INVALID_ID");
        }

        if (this.getItsMveID() != DBIndex.INVALID_ID) {
            throw new SystemErrorException(mName + "dc.itsMveID != INVALID_ID");
        }

        if (this.getItsMveType() == MatrixVocabElement.MatrixType.UNDEFINED) {
            throw new SystemErrorException(mName + "dc.itsMveType == UNDEFINED");
        }

        if ((!(this.getDB().IsValidSVarName(this.getName()))) ||
                (this.getDB().vl.inVocabList(this.getName())) ||
                (this.getDB().cl.inColumnList(this.getName()))) {
            throw new SystemErrorException(mName +
                    "Column name invalid or in use");
        }

        mve = new MatrixVocabElement(this.getDB(), this.getName());

        mve.setType(this.getItsMveType());

        /* Construct the initial, singleton formal argument appropriate to
         * the type of the DataColumn.
         */

        if (this.itsMveType == MatrixVocabElement.MatrixType.FLOAT) {
            fa = new FloatFormalArg(this.getDB());
        } else if (this.itsMveType == MatrixVocabElement.MatrixType.INTEGER) {
            fa = new IntFormalArg(this.getDB());
        } else if (this.itsMveType == MatrixVocabElement.MatrixType.MATRIX) {
            fa = new UnTypedFormalArg(this.getDB());
        } else if (this.itsMveType == MatrixVocabElement.MatrixType.NOMINAL) {
            fa = new NominalFormalArg(this.getDB());
        } else if (this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE) {
            fa = new PredFormalArg(this.getDB());
        } else if (this.itsMveType == MatrixVocabElement.MatrixType.TEXT) {
            fa = new TextStringFormalArg(this.getDB());
        } else {
            throw new SystemErrorException(mName + "Unknown matrixType?!?!");
        }

        mve.appendFormalArg(fa);

        /* In the case of the float, integer, nominal, predicate, and text
         * DataColumns, we should set the system flags on the associated
         * MatrixVocabElement entries.  However, we need to set the column
         * ID fields in the MatrixVocabElements first -- which we can't
         * do until the mve has been inserted in the vocab list, and the
         * column has been inserted in the column list.  Don't forget to
         * do this.
         */

        return mve;

    } /* DataColumn::constructInitMatrixVE() */


    /**
     * lookupMatrixVE()
     *
     * Given an ID, attempt to look up the associated MatrixVocabElement
     * in the database associated with the DataColumn.  If there is no such
     * MatrixVocabElement, throw  a system error.
     *                                              -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     */
    protected MatrixVocabElement lookupMatrixVE(long mveID)
            throws SystemErrorException {
        final String mName = "DataColumn::lookupMatrixVE(mveID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if (mveID == DBIndex.INVALID_ID) {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(mveID);

        if (dbe == null) {
            throw new SystemErrorException(mName + "mveID has no referent");
        }

        if (!(dbe instanceof MatrixVocabElement)) {
            throw new SystemErrorException(mName +
                    "mveID doesn't refer to a matrix vocab element");
        }

        mve = (MatrixVocabElement) dbe;

        return mve;

    } /* DataColumn::lookupMatrixVE(mveID) */


    /**
     * itsCellsToDBString()
     *
     * Construct a string containing the values of the cells in a
     * format that displays the full status of the arguments and
     * facilitates debugging.
     *                                          -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    protected String itsCellsToDBString()
            throws SystemErrorException {
        final String mName = "DataColumn::itsCellsToDBString(): ";
        int i = 0;
        String s;

        if ((this.itsCells == null) ||
                (this.numCells == 0)) {
            s = "(itsCells ())";
        } else {
            this.numCells = this.itsCells.size();

            if (this.numCells <= 0) {
                throw new SystemErrorException(mName + "numCells <= 0");
            }

            s = new String("(itsCells (");

            while (i < (this.numCells - 1)) {
                s += this.getCell(i + 1).toDBString() + ", ";
                i++;
            }

            s += this.getCell(i + 1).toDBString();

            s += "))";
        }

        return s;

    } /* DataColumn::itsCellsToDBString() */


    /**
     * itsCellsToString()
     *
     * Construct a string containing the values of the cells in the column.
     *
     *                                          -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    protected String itsCellsToString()
            throws SystemErrorException {
        final String mName = "DataColumn::itsCellsToString(): ";
        int i = 0;
        String s;

        if ((this.itsCells == null) ||
                (this.numCells == 0)) {
            s = "()";
        } else {
            this.numCells = this.itsCells.size();

            if (this.numCells <= 0) {
                throw new SystemErrorException(mName + "numCells <= 0");
            }

            s = new String("(");

            while (i < (numCells - 1)) {
                s += this.getCell(i + 1).toString() + ", ";
                i++;
            }

            s += getCell(i + 1).toString();

            s += ")";
        }

        return s;

    } /* DataColumn::itsCellsToString() */


    /**
     * toMODBFile_colDec()
     *
     * Write the MacSHAPA ODB style declaration of the data column
     * to the supplied file in MacSHAPA ODB file format.  The output of this
     * method is the <s_var_dec> in the grammar defining the MacSHAPA ODB
     * file format.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     * Note that this method throws away a lot of information about each
     * data column element, as this data is not used in MacSHAPA.
     *
     *                                              JRM -- 12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_colDec(java.io.PrintStream output,
                                     String newLine,
                                     String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "DataColumn::toMODBFile_colDec()";
        int i;
        int numFargs;
        MatrixVocabElement mve = null;
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

        if ( this.itsMveType == MatrixVocabElement.MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "itsMveType undefined?!?!");
        }

        mve = this.lookupMatrixVE(this.itsMveID);

        numFargs = mve.getNumFormalArgs();

        if ( numFargs <= 0 )
        {
            /* no formal arguments?? scream and die */
            throw new SystemErrorException(mName + "no formal arguments?!?!");
        }


        // Opening header and parenthesis:

        output.printf("%s( |%s|%s", indent, this.name, newLine);

        output.printf("%s  (%s", indent, newLine);


        // type:

        switch( this.itsMveType )
        {
            case FLOAT:
                output.printf("%s    ( TYPE> <<FLOAT>> )%s", indent, newLine);
                break;

            case INTEGER:
                output.printf("%s    ( TYPE> <<INTEGER>> )%s", indent, newLine);
                break;

            case MATRIX:
                output.printf("%s    ( TYPE> <<MATRIX>> )%s", indent, newLine);
                break;

            case NOMINAL:
                output.printf("%s    ( TYPE> <<NOMINAL>> )%s", indent, newLine);
                break;

            case PREDICATE:
                output.printf("%s    ( TYPE> <<PREDICATE>> )%s",
                              indent, newLine);
                break;

            case TEXT:
                output.printf("%s    ( TYPE> <<TEXT>> )%s", indent, newLine);
                break;

            case UNDEFINED:
                throw new SystemErrorException(mName +
                        "this.itsMveType is undefined?!?");
                // break; /* commented out to keep the compiler happy */

            default:
                throw new SystemErrorException(mName +
                        "this.itsMveType has unknown value?!?");
                // break; /* commented out to keep the compier happy */
        }


        // variable length:

        if ( this.varLen )
        {
            output.printf("%s    ( VARIABLE-LENGTH> TRUE )%s", indent, newLine);
        }
        else
        {
            output.printf("%s    ( VARIABLE-LENGTH> FALSE )%s", indent, newLine);
        }


        // formal argument list:

        output.printf("%s    ( FORMAL-ARG-LIST> ( ", indent);

        i = 0;
        while ( i < numFargs )
        {
            farg = mve.getFormalArg(i);
            output.printf("|%s| ", farg.getFargName());
            i++;
        }

        output.printf(")%s", newLine);


        // column width:

        output.printf("%s    ( COLUMN-WIDTH> %d )%s",
                      indent, this.macshapaColWidth, newLine);


        // closing parentheses:

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);


        return;

    } /* DataColumn::toMODBFile_colDec() */


    /**
     * toMODBFile_colDef()
     *
     * Write the MacSHAPA ODB style definition of the data column
     * to the supplied file in MacSHAPA ODB file format.  The output of this
     * method is the <s_var_def> in the grammar defining the MacSHAPA ODB
     * file format.
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

    protected void toMODBFile_colDef(java.io.PrintStream output,
                                     String newLine,
                                     String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "DataColumn::toMODBFile_colDef()";
        String cellIndent;
        int i;
        MatrixVocabElement mve = null;
        DataCell cell = null;
        Vector<String> lvl = null;

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

        if ( this.itsMveType == MatrixVocabElement.MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "itsMveType undefined?!?!");
        }

        mve = this.lookupMatrixVE(this.itsMveID);


        // Opening header and parenthesis:

        output.printf("%s( |%s|%s", indent, this.name, newLine);

        output.printf("%s  (%s", indent, newLine);


        // cell definitions:

        output.printf("%s    ( CELLS>%s", indent, newLine);

        cellIndent = indent + "      ";
        i = 0;

        while ( i < this.numCells )
        {
            cell = this.getCell(i + 1);
            cell.toMODBFile(mve, output, newLine, cellIndent);
            i++;
        }

        output.printf("%s    )%s", indent, newLine);


        // vocab list if appropriate:

        if ( ( this.itsMveType == MatrixVocabElement.MatrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE ) )
        {
            output.printf("%s    ( VOCAB> ( ", indent);

            lvl = this.toMODBFile_construct_local_vocab_list();

            for ( i = 0; i < lvl.size(); i++ )
            {
                output.printf("|%s| ", lvl.get(i));
            }

            output.printf(") )%s", newLine);
        }



        // closing parentheses:

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);


        return;

    } /* DataColumn::toMODBFile_colDef() */


    /**
     * toMODBFile_construct_local_vocab_list()
     *
     * To write a MacSHAPA ODB format database file, we must constuct
     * a list of the names of the predicate and column predicates that
     * appear in the cells of this DataColumn.
     *
     * This method is the main routine for this task.
     *
     * The cycle of operation is as follows:
     *
     * 1) Construct an empty set of long integers to hold the set of IDs of
     *    predicates and column predicates that appear in the cells of the
     *    data column.
     *
     * 2) Send a toMODBFile_update_local_vocab_list() message to each
     *    cell in the cell in the data column.
     *
     * 3) Upon receipt of the message, each cell passes it to its value matrix.
     *
     * 4) Upon receipt, each matrix scans its arguments to see if any are
     *    either predicates or column predicates.  If so, it passes the
     *    message on to them.
     *
     * 5) Upon receipt, each instance pred or colPred calls the
     *    toMODBFile_update_local_vocab_list() of this instance of DataColumn
     *    with the ID of the PVE or MVE of which it is an instance, and then
     *    passes the toMODBFile_update_local_vocab_list() message on to any
     *    predicates or column predicates wich appear in its argument list.
     *
     * 6) Once all cells have been polled, this method uses the set of mve and
     *    pve IDs to construct a vector of strings containing the names of the
     *    predicates and columns that appear in the data column at this point
     *    in time, and returns that vector.
     *
     *                                              JRM -- 7/20/09
     *
     * Changes:
     *
     *    - Added code to sort the local vocab list before returning it.
     *
     *      This shouldn't be necessary, but different versions of java seem
     *      to be constructing the local vocab list in different orders.  This
     *      isn't a real problem, as neither MacSHAPA nor OpenSHAPA care, but
     *      it does break our tests.
     *                                              JRM - 8/5/09
     */

    private Vector<String>
    toMODBFile_construct_local_vocab_list()
        throws SystemErrorException
    {
        final String mName = 
                "DataColumn::toMODBFile_construct_local_vocab_list(): ";
        int i;
        DataCell cell = null;
        Vector<String> lvl = null;

        if ( ( this.itsMveType != MatrixVocabElement.MatrixType.MATRIX ) &&
             ( this.itsMveType != MatrixVocabElement.MatrixType.PREDICATE ) )
        {
            throw new SystemErrorException(mName +
                    "not a matrix or predicate column?!?");
        }

        if ( this.localVocabIDSet != null )
        {
            throw new SystemErrorException(mName +
                    "this.localVocabIDSet not null on entry?!?");
        }

        this.localVocabIDSet = new HashSet<Long>();

        i = 0;

        while ( i < this.numCells )
        {
            cell = this.getCell(i + 1);
            cell.toMODBFile_update_local_vocab_list(this);
            i++;
        }

        lvl = new Vector<String>();

        if ( ! this.localVocabIDSet.isEmpty() )
        {
            long ID;
            String name;
            java.util.Iterator it = this.localVocabIDSet.iterator();
            DBElement dbe;

            while (it.hasNext())
            {
                ID = (Long)it.next();
                dbe = this.getDB().idx.getElement(ID);

                if ( dbe instanceof VocabElement )
                {
                    VocabElement ve;

                    ve = (VocabElement)dbe;

                    name = ve.getName();

                    lvl.add(name);
                }
                else
                {
                    throw new SystemErrorException(mName +
                            ": localVocabIDSet contains an ID that isn't " +
                            "the ID of a vocab element.");
                }
            }

            // For reason or reason's unknown, the above while loop results
            // in the elements of the vocab list appearing in different order
            // under Linux and MacOS.
            //
            // This shouldn't be, as java is supposed to give the same results
            // on any supported platform.  Thus I'm guessing that this is a
            // bug in either the JVM, or more likely, a library.
            //
            // Be this as it may, it is causing spurious test failures, so we
            // have to enforce consistant order across different platforms.
            //
            // Do this by sorting the lvl vector before returning it.
            //
            //                                          JRM -- 8/5/09

            java.util.Collections.sort(lvl);
        }

        this.localVocabIDSet = null;

        return lvl;

    } /* DataColumn::toMODBFile_construct_local_vocab_list() */


    /**
     * toMODBFile_update_local_vocab_list()
     *
     * Check to see if this.localVocabIDSet contains the supplied ID, and
     * add it to the set if it does not.
     *
     * Thow a sytem error on entry if either this.localVocabIDSet is null,
     * or the supplied ID is invalid.
     *
     *                                      JRM -- 7/22/09
     *
     * Changes:
     *
     *    - None.
     *
     * @param ID
     * @throws org.openshapa.db.SystemErrorException
     */

    protected void
    toMODBFile_update_local_vocab_list(Long ID)
        throws SystemErrorException
    {
        final String mName = "DataColumn::toMODBFile_update_local_vocab_list(): ";

        if ( this.localVocabIDSet == null )
        {
            throw new SystemErrorException(mName +
                    "localVocabIDSet null on entry.");
        }

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID is invalid.");
        }

        if ( ! this.localVocabIDSet.contains(ID) )
        {
            this.localVocabIDSet.add(ID);
        }

        return;
    }

    /*************************************************************************/
    /************************* Cells Management: *****************************/
    /*************************************************************************/
    /**
     * appendCell()
     *
     * Append the supplied DataCell to the end of the vector of cells.
     *
     *                                          -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to allocate a new instance of DataCellListeners, and
     *      assign it to the newly created cell.  Added call to generate
     *      DataColumn cell insertion message to listeners.  Finally, added
     *      calls to mark the beginning and end of any resulting cascade of
     *      changes.
     *
     *                                          -- 2/10/08
     */
    protected void appendCell(DataCell newCell)
        throws SystemErrorException
    {
        final String mName = "DataColumn::appendCell(): ";
        DataCellListeners nl = null;

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        if (!this.validCell(newCell, true)) {
            throw new SystemErrorException(mName + "invalid cell");
        }

        newCell.validateNewCell();

        this.getDB().cascadeStart();

        this.getDB().idx.addElement(newCell);
        newCell.insertValInIndex();

        this.itsCells.add(newCell);
        this.numCells = this.itsCells.size();
        newCell.setOrd(this.numCells);

        if (itsCells.elementAt(newCell.getOrd() - 1) != newCell) {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        nl = new DataCellListeners(getDB(), newCell);
        newCell.setListeners(nl);

        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive an end cascade message.
         */

        this.listeners.notifyListenersOfCellInsertion(newCell.getID());

        if ( ( this.itsMveType == MatrixVocabElement.MatrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE ) )
        {
            newCell.registerPreds();
        }

        this.getDB().cascadeEnd();

        return;

    } /* DataColumn::appendCell(newCell) */


    /**
     * getCell()
     *
     * Get the cell at the specified ord.  Note that this function returns
     * the actual cell -- not a copy.  For almost all purposes, the returned
     * cell should be treated as read only.
     *
     *                                              -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected DataCell getCell(int ord)
            throws SystemErrorException {
        final String mName = "DataColumn::getCell(): ";
        DataCell retVal = null;

        if ((ord < 1) || (ord > this.numCells)) {
            throw new SystemErrorException(mName + "ord out of range");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        retVal = this.itsCells.get(ord - 1);

        if (retVal.getOrd() != ord) {
            throw new SystemErrorException(mName + "unexpected ord: " +
                    retVal.getOrd() + " (" + ord + ")");
        }

        return retVal;

    } /* DataColumn::getCell() */


    /**
     * getCellCopy()
     *
     * Return a copy of the cell at the specified ord.
     *
     *                                          -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected DataCell getCellCopy(int ord)
            throws SystemErrorException {

        return new DataCell(this.getCell(ord));

    } /* DataColumn::getCell() */


    /**
     * insertCell()
     *
     * Insert the supplied DataCell in the indicated location in the vector
     * of DataCells.  Update the ords of the cells after the insertion point.
     *
     *                                              -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to allocate a new instance of DataCellListeners, and
     *      assign it to the newly created cell.  Added call to generate
     *      DataColumn cell insertion message to listeners.  Finally, added
     *      calls to mark the beginning and end of any resulting cascade of
     *      changes.
     *                                              -- 2/10/08
     */
    protected void insertCell(DataCell newCell,
            int ord)
            throws SystemErrorException {
        final String mName = "DataColumn::insertCell(): ";
        int i;
        DataCell dc = null;
        DataCellListeners nl = null;

        if ((ord < 1) || (ord > this.numCells + 1)) {
            throw new SystemErrorException(mName + "ord out of range");
        }

        if (!this.validCell(newCell, true)) {
            throw new SystemErrorException(mName + "invalid cell");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        newCell.validateNewCell();

        this.getDB().cascadeStart();

        // set the new cell's ord
        newCell.setOrd(ord);

        this.getDB().idx.addElement(newCell);
        newCell.insertValInIndex();

        // insert the cell & update numCells
        this.itsCells.insertElementAt(newCell, (ord - 1));
        this.numCells = this.itsCells.size();

        // verify ord of new cell
        if (itsCells.get(newCell.getOrd() - 1) != newCell) {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        nl = new DataCellListeners(getDB(), newCell);
        newCell.setListeners(nl);

        this.listeners.notifyListenersOfCellInsertion(newCell.getID());

        if ((this.itsMveType == MatrixVocabElement.MatrixType.MATRIX) ||
                (this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE)) {
            newCell.registerPreds();
        }

        // Update ords for insertion
        for (i = ord; i < this.numCells; i++) {
            dc = itsCells.elementAt(i);

            if (dc == newCell) {
                throw new SystemErrorException(mName + "scan hit new cell?!?");
            }

            if (dc.cascadeGetOrd() != i) {
                throw new SystemErrorException(mName + "unexpected old ord" + i);
            }

            /* update the ord */
            dc.cascadeSetOrd(i + 1);
        }

        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive an end cascade message.
         */

        this.getDB().cascadeEnd();

        return;

    } /* DataColumn::insertCell(newCell, ord) */


    /**
     * removeCell()
     *
     * Remove the cell indicated by the supplied ord from itsCells.  As a
     * sanity check, verify that the target cell has the indicated ID.
     * After the removal, update the ords of the remaining cells.
     *
     * Return a reference to the DataCell removed from itsCells.
     *
     *                                      -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to notify listeners of deletion, and to remove the
     *      instance of DataCellListeners from the target DataCell
     *      before the actual deletion.  Added call to generate
     *      DataColumn cell deletion message to listeners.  Finally, added
     *      calls to mark the beginning and end of any resulting cascade of
     *      changes.
     */
    protected DataCell removeCell(int targetOrd,
            long targetID)
            throws SystemErrorException {
        final String mName = "DataColumn::removeCell(): ";
        int i;
        DataCell dc = null;
        DataCell retVal = null;


        if ((targetOrd < 1) || (targetOrd > this.numCells)) {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        dc = itsCells.elementAt(targetOrd - 1);

        if (dc == null) {
            throw new SystemErrorException(mName + "can't get target cell");
        }

        if (dc.getID() != targetID) {
            throw new SystemErrorException(mName + "target ID mismatch");
        }

        this.getDB().cascadeStart();

        dc.notifyListenersOfDeletion();
        dc.setListeners(null);

        this.listeners.notifyListenersOfCellDeletion(dc.getID());

        dc.removeValFromIndex();

        if (dc != this.itsCells.remove(targetOrd - 1)) {
            throw new SystemErrorException(mName + "remove failed?!?!");
        }

        retVal = dc;

        this.numCells = this.itsCells.size();

        for (i = targetOrd - 1; i < this.numCells; i++) {
            dc = this.itsCells.get(i);

            if (dc == null) {
                throw new SystemErrorException(mName + "can't get cell" + i);
            }

            if (dc.getOrd() != i + 2) {
                throw new SystemErrorException(mName + "unexpected cell ord " +
                        dc.getOrd() + "(" + (i + 2) + " expected)");
            }

            dc.cascadeSetOrd(i + 1);
        }

        this.getDB().cascadeEnd();

        return retVal;

    } /* DataColumn::removeCell */


    /**
     * replaceCell()
     *
     * Replace the DataCell at targetOrd in this.itsCells with the supplied
     * DataCell.  Return the old DataCell.
     *                                              -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to notify listeners of changes, and to transfer the
     *      instance of DataCellListeners from the old to the new incarnation
     *      of the data cell.  Added calls to mark the beginning and end of
     *      any resulting cascade of changes.
     *                                              -- 2/10/08
     *
     *    - Reworked code to use the cascade mechanism.  In essence, we now
     *      recruit the target cell into the cascade, and set its pending
     *      field to newCell.
     *
     *      This has the advantage of handling multiple replacements more
     *      gracefully, although at present we will throw a system error
     *      exception if we try to replace the same cell twice in the same
     *      cascade.
     */
    protected DataCell replaceCell(DataCell newCell,
            int targetOrd)
            throws SystemErrorException {
        final String mName = "DataColumn::replaceCell(): ";
        int i;
        DataCell oldCell = null;
        DataCell retVal = null;

        if ((targetOrd < 1) || (targetOrd > this.numCells)) {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }

        if (!this.validCell(newCell, false)) {
            throw new SystemErrorException(mName + "invalid cell");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName +
                    "itsCells not initialized?!?");
        }

        oldCell = this.itsCells.get(targetOrd - 1);

        if (oldCell == null) {
            throw new SystemErrorException(mName + "can't get old cell.");
        } else if (this.getDB().idx.getElement(oldCell.getID()) != oldCell) {
            throw new SystemErrorException(mName + "oldCell not in index?!?");
        }

        this.getDB().cascadeStart();

        newCell.setOrd(targetOrd);

        oldCell.cascadeSetPending(newCell);

        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive the end cascade message.
         */

        this.getDB().cascadeEnd();

        retVal = oldCell;

        if (oldCell.getListeners() != null) {
            throw new SystemErrorException(mName +
                    "replacement didn't complete!?!");
        }

        return retVal;

    } /* DataColumn::replaceCell() */


    /**
     * sortCells()
     *
     * Sort the cells in the column by onset.  This implementation does no
     * sanity checking, as java prevents it.  It is also very inefficient,
     * as we must scan the whole column to touch up the ords.
     *
     * Must re-write this method so as to update ords efficiently, avoid java's
     * built in sort() routine, and include suitable sanity checking.
     *
     *                                              -- 1/22/08
     *
     * Changes:
     *
     *    - None.
     */
    // TODO: re-write this method with sanity checking.
    protected void sortCells()
            throws SystemErrorException {
        final String mName = "DataColumn::sortCells(): ";
        int i;
        class dc_onset_comp implements java.util.Comparator<DataCell> {

            public int compare(DataCell dc1, DataCell dc2) {
                int result = 0;

                if (dc1.onset.insane_gt(dc2.onset)) {
                    result = 1;
                } else if (dc1.onset.insane_lt(dc2.onset)) {
                    result = -1;
                }

                return result;
            }
        }
        ;
        dc_onset_comp comp = new dc_onset_comp();

        if (this.itsCells == null) {
            throw new SystemErrorException(mName + "itsCells null on entry");
        }

        if (this.numCells > 0) {
            java.util.Collections.sort(this.itsCells, comp);

            for (i = 0; i < this.numCells; i++) {
                this.itsCells.get(i).setOrd(i + 1);
            }
        }

    } /* DataColumn::sortCells() */


    /**
     * sortItsCells()
     *
     *
     * Sort itsCells by cell onset.  This method should only be called from
     * within a cascade of changes, and will throw a system error exception
     * if this.cascadeInProgress is false on entry.
     *
     * TODO: Must re-write as the current implementation is an abomination.
     * In new version, avoid the inefficientcies of using java's built in
     * sort(), the necessity of touching up all the ords, and the stupidity
     * I had to go though to handle a system error exception.
     *
     *                                              -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    protected void sortItsCells()
            throws SystemErrorException {
        final String mName = "DataColumn::sortItsCells(): ";
        int i;
        class cascade_dc_onset_comp implements java.util.Comparator<DataCell> {

            public int compare(DataCell dc1, DataCell dc2) {
                int result = 0;
                boolean threwSystemErrorException = false;
                String systemErrorExceptionString = null;

                try {
                    if (dc1.cascadeGetOnset().insane_gt(dc2.cascadeGetOnset())) {
                        result = 1;
                    } else if (dc1.cascadeGetOnset().insane_lt(dc2.cascadeGetOnset())) {
                        result = -1;
                    }
                } catch (SystemErrorException e) {
                    threwSystemErrorException = true;
                    systemErrorExceptionString = e.getMessage();
                }

                if (threwSystemErrorException) {
                    System.out.printf(
                            "%s: Caught SystemErrorException \"%s\".\n",
                            mName, systemErrorExceptionString);
                    System.out.flush();

                    // int i = 1/0; // to force an arithmatic exception.
                }

                return result;
            }
        }
        ;

        cascade_dc_onset_comp comp = new cascade_dc_onset_comp();

        if (!this.cascadeInProgress) {
            throw new SystemErrorException(mName + "cascade not in progress?!");
        }

        if (this.itsCells == null) {
            throw new SystemErrorException(mName + "itsCells null on entry");
        }

        if (this.numCells > 0) {
            int oldOrd;
            DataCell c;
            java.util.Collections.sort(this.itsCells, comp);

            for (i = 0; i < this.numCells; i++) {
                c = this.itsCells.get(i);

                if (c.cascadeGetOrd() != i + 1) {
                    c.cascadeSetOrd(i + 1);
                }
            }
        }

    } /* DataColumn::sortItsCells() */


    /**
     * validCell()
     *
     * Verify that a cell has been correctly initialized for insertion into
     * itsCells.  Return true if it has been, and false otherwise.
     *
     *                                              -- 8/30/07
     *
     * Changes:
     *
     *    - Added the newCell parameter that allows us to skip the cell id
     *      check on new cells that haven't been added to the index yet.
     */
    private boolean validCell(DataCell cell,
            boolean newCell)
            throws SystemErrorException {
        final String mName = "DataColumn::validCell(): ";

        if (cell == null) {
            throw new SystemErrorException(mName + "cell null on entry.");
        }

        if (cell.getDB() != this.getDB()) {
            return false;
        }

        if (cell.getItsMveID() != this.itsMveID) {
            return false;
        }

        if (cell.getItsColID() != this.getID()) {
            return false;
        }

        if (cell.getItsMveType() != this.itsMveType) {
            throw new SystemErrorException(mName + "type mismatch");
        }

        if ((!newCell) && (cell.getID() == DBIndex.INVALID_ID)) {
            throw new SystemErrorException(mName + "cell has invalid ID");
        }


        // other sanity checks needed?

        return true;

    } /* DataColumn::validCell() */


    /*************************************************************************/
    /********************* MVE Change Management: ****************************/
    /*************************************************************************/
    /**
     * MVEChanged()
     *
     * Needed to implement the InternalMatrixVocabElementListener interface.
     *
     * Handle the various housekeeping required to process a change in the
     * MatrixVocabElement associated with this DataColumn.
     *
     * Verify that the db and mveID match -- throw system errors if they don't.
     *
     * Verify that this.cascadeInProgress is true.  Throw a system
     * it it isn't.
     *
     *                                  -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    public void MVEChanged(Database db,
            long MVEID,
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
        final String mName = "DataColumn::MVEChanged(): ";

        if (this.getDB() != db) {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if (this.itsMveID != MVEID) {
            throw new SystemErrorException(mName + "mveID mismatch.");
        }

        if (!this.cascadeInProgress) {
            throw new SystemErrorException(mName +
                    "cascade not in progress?!?.");
        }

        if (this.pending != null) {
            throw new SystemErrorException(mName +
                    "this.pending not null on entry?!?!");
        }

        if ((nameChanged) || (varLenChanged)) {
            this.pending = new DataColumn(this);

            if (nameChanged) {
                if (this.name.compareTo(oldName) != 0) {
                    throw new SystemErrorException(mName +
                            "oldName != this.name");
                }

                if (!(this.getDB().IsValidSVarName(newName))) {
                    throw new SystemErrorException(mName +
                            "newName not a valid svar name");
                }

                if (!this.getDB().vl.inVocabList(newName)) {
                    throw new SystemErrorException(mName +
                            "newName not in v?!?");
                }

                if (db.cl.inColumnList(newName)) {
                    throw new SystemErrorException(mName +
                            "newName already appears in column list");
                }

                // set the new name directly in pending, as
                // this.pending.setName() will throw a system error if the
                // new name is already in the  vocab list -- which it already
                // is.
                this.pending.name = new String(newName);
            }

            if (varLenChanged) {
                if (this.varLen != oldVarLen) {
                    throw new SystemErrorException(mName +
                            "oldVarLen != this.varLen");
                }
                this.pending.varLen = newVarLen;
            }
        }

        for (Cell c : this.itsCells)
        {
            ((DataCell) c).cascadeUpdateForMVEDefChange(db,
                                                        MVEID,
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

        // TODO: Delete this if all goes well
//        if (fargListChanged) {
//            for (Cell c : this.itsCells) {
//                ((DataCell) c).cascadeUpdateForFargListChange(n2o,
//                        o2n,
//                        fargNameChanged,
//                        fargSubRangeChanged,
//                        fargRangeChanged,
//                        fargDeleted,
//                        fargInserted,
//                        oldFargList,
//                        newFargList,
//                        cpn2o,
//                        cpo2n,
//                        cpFargNameChanged,
//                        cpFargSubRangeChanged,
//                        cpFargRangeChanged,
//                        cpFargDeleted,
//                        cpFargInserted,
//                        oldCPFargList,
//                        newCPFargList);
//            }
//        }

        return;

    } /* DataColumn::MVEChanged() */


    /**
     * VEDeleted()
     *
     * Needed to implement the InternalMatrixVocabElementListener interface.
     *
     * This method should never be called, as the DataColumn should have
     * de-registered before the MatrixVocabElement is deleted.
     *
     * Throw a system error if the method is ever called.
     *
     *                                  -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    public void MVEDeleted(Database db,
                           long MVEID)
        throws SystemErrorException
    {
        final String mName = "DataColumn::MVEDeleted(): ";

        throw new SystemErrorException(mName + "should be un-reachable");

    } /* DataColumn::VEDeleted() */

    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /* None */
//  /**
//   * Sets the type of cells in this column
//   * @param type the type of cells
//   */
//  public void setType(int type)
//  {
//    this.type = type;
//
//    // Notify listeners that the column definition has been modified
//    for (int i=0; i<this.changeListeners.size(); i++) {
//      ((ColumnChangeListener)this.changeListeners.elementAt(i)).ColumnDefChanged(this);
//    }
//} //End of setType() method
} //End of DataColumn class definition
