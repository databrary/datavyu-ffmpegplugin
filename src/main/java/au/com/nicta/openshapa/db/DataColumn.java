/*
 * DataColumn.java
 *
 * Created on December 14, 2006, 7:15 PM
 *
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;

/**
 * Class DataColumn
 *
 * Instances of DataColumn are used to implement text, nominal, float, integer,
 * predicate, and matrix columns (AKA spreadsheet variables) in the database.
 *
 *                                                  JRM -- 8/29/07
 *
 * @author FGA
 */
public class DataColumn extends Column 
        implements InternalVocabElementListener
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
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
     */
    
    /** ID of associated matrix VE */
    private long itsMveID = DBIndex.INVALID_ID;
    
    /** Type of associated matrix VE */
    private MatrixVocabElement.matrixType itsMveType = 
            MatrixVocabElement.matrixType.UNDEFINED;
    
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
     *                                              JRM -- 8/29/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public DataColumn(Database db,
                      String name,
                      MatrixVocabElement.matrixType type)
        throws SystemErrorException
    {
        super(db);

        final String mName = "DataColumn::DataColumn(db, name, type): ";
        
        this.setName(name);
        
        if ( ( type == MatrixVocabElement.matrixType.FLOAT ) ||
             ( type == MatrixVocabElement.matrixType.INTEGER ) ||
             ( type == MatrixVocabElement.matrixType.MATRIX ) ||
             ( type == MatrixVocabElement.matrixType.NOMINAL ) ||
             ( type == MatrixVocabElement.matrixType.PREDICATE ) ||
             ( type == MatrixVocabElement.matrixType.TEXT ) )
        {
            this.itsMveType = type;
        }
        else
        {
            throw new SystemErrorException(mName + "invalid type");
        }
    } /* DataColumn::DataColumn(db, name, type) */
    
    public DataColumn(Database db,
                      String name,
                      boolean hidden,
                      boolean readOnly,
                      long mveID)
        throws SystemErrorException
    {
        super(db);

        final String mName = 
                "DataColumn::DataColumn(db, name, hidden, readOnly, mveID): ";
        MatrixVocabElement mve;
        
        mve = this.lookupMatrixVE(mveID);
        
        if ( name == null )
        {
            throw new SystemErrorException(mName + "name null on entry.");
        }
        
        if ( name.compareTo(mve.getName()) != 0 )
        {
            throw new SystemErrorException(mName + "name doesn't match mve");
        }
        
        if ( db.cl.inColumnList(name) )
        {
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
        throws SystemErrorException
    {
        super((Column)dc);
        
        // TODO: add sanity checking??
        this.itsCells = null;
        this.itsMveID = dc.itsMveID;
        this.itsMveType = dc.itsMveType;
        this.varLen = dc.varLen;
        
    } /* DataColumn::DataColumn(dc) */
    
        
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
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected Vector<DataCell> getItsCells()
    {
        
        return this.itsCells;
        
    } /* DataColumn::getItsCells() */
    
    protected void setItsCells(Vector<DataCell> cells)
    {
        
        this.itsCells = cells;
        
        if ( this.itsCells == null )
        {
            this.numCells = 0;
        }
        else
        {
            this.numCells = this.itsCells.size();
        }
        
    } /* DataColumn::setItsCells(cells) */
    
    
    /**
     * getItsMveID() & setItsMveID()
     *
     * Get or set the current value of the itsMveID field.  Observe that
     * setItsMveID() is protected -- it should only be used within the 
     * openshapa.db package.  Also the method can only be called once, and 
     * may not be used to set itsMveID to the INVALID_ID.
     *
     *                                      JRM -- 8/29/07
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
        
        if ( this.itsMveType == MatrixVocabElement.matrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "this.itsMveType undefined on entry.");
        }
        
        if ( itsMveID != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "itsMveID already set");
        }
        else if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        
        mve = this.lookupMatrixVE(mveID);
        
        if ( mve.getItsColID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                    "target mve already assigned to a column");
        }
        
        if ( mve.getType() != this.itsMveType )
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
     *                                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public MatrixVocabElement.matrixType getItsMveType()
    {
        
        return this.itsMveType;
        
    } /* DataColumn::getItsMveType() */
    
    
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
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void constructItsCells()
        throws SystemErrorException
    {
        final String mName = "DataColumn::constructItsCells(): ";
        
        if ( this.itsCells != null )
        {
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
     *                                              JRM -- 3/23/08
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
        
        if ( this.itsMveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "this.itsMveID is invalid");
        }

        dbe = this.db.idx.getElement(this.itsMveID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + 
                    "this.itsMveID has no referent");
        }

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "this.itsMveID does not refer to a MatrixVocabElement");
        }

        mve = (MatrixVocabElement)dbe;
        
        mve.deregisterInternalListener(this.id);
        
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
     *                                              JRM -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void register()
        throws SystemErrorException
    {
        final String mName = "DataColumn::register(): ";
        DBElement dbe = null;
        MatrixVocabElement mve;
        
        if ( this.itsMveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "this.itsMveID is invalid");
        }

        dbe = this.db.idx.getElement(this.itsMveID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + 
                    "this.itsMveID has no referent");
        }

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "this.itsMveID does not refer to a MatrixVocabElement");
        }

        mve = (MatrixVocabElement)dbe;
        
        mve.registerInternalListener(this.id);
        
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
    
    public String toDBString()
    {
        String s;
        
        try
        {
            s = "(DataColumn (name " + this.name +
                ") (id " + this.getID() +
                ") (hidden " + this.hidden + 
                ") (readOnly " + this.readOnly +
                ") (itsMveID " + this.itsMveID +
                ") (itsMveType " + this.itsMveType +
                ") (varLen " + this.varLen +
                ") (numCells " + this.numCells + ") " +
                this.itsCellsToDBString() +  "))";
        }
        
        catch (SystemErrorException e)
        {
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
    public String toString() 
    {
        String s;
        
        try
        {
            s = "(" + this.getName()  + ", " + this.itsCellsToString() + ")";
        }

        catch (SystemErrorException e)
        {
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
     *                                              JRM -- 3/6/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void addPending(Cell c)
        throws SystemErrorException
    {
        final String mName = "DataColumn::addPending(c): ";

        if ( c == null )
        {
            throw new SystemErrorException(mName + "c null on entry");
        }
        
        if ( c.getItsColID() != this.id )
        {
            throw new SystemErrorException(mName + "col ID mismatch");
        }
        
        if ( ! ( c instanceof DataCell ) )
        {
            throw new SystemErrorException(mName + "c not a DataCell");
        }
        
        
        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName + 
                    "call to addPending() when cascade not in progress.");
        }
        
        if ( this.db != c.getDB() )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( this.getCell(c.getOrd()) != c )
        {
            throw new SystemErrorException(mName + 
                    "c not the cannonical instance(1).");
        }
        
        if ( this.db.idx.getElement(c.getID()) != c )
        {
            throw new SystemErrorException(mName + 
                    "c not the cannonical instance(2).");
        }
        
        if ( ((DataCell)c).listeners == null )
        {
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
     *                                              JRM -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void cascadeReplaceCell(DataCell oldCell,
                                      DataCell newCell)
        throws SystemErrorException
    {
        final String mName = "DataColumn::cascadeReplaceCell(): ";
        int ord;
        int i;
        
        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName + 
                                           "cascadeInProgress is false?!?!?");
        }
        
        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }
        
        if ( ( oldCell == null ) || ( newCell == null ) )
        {
            throw new SystemErrorException(mName + 
                                           "oldCell or newCell null on entry");
        }
        
        if ( ( oldCell.getDB() != this.db ) ||
             ( newCell.getDB() != this.db ) )
        {
            throw new SystemErrorException(mName + "db mismatch");
        }
        
        if ( oldCell.getID() != newCell.getID() )
        {
            throw new SystemErrorException(mName + "cell id mismatch");
        }
        
        if ( this.db.idx.getElement(oldCell.getID()) != oldCell )
        {
            throw new SystemErrorException(mName + "oldCell not cannonical(1)");
        }
        
        if ( oldCell.getListeners() == null )
        {
            throw new SystemErrorException(mName + "oldCell not cannonical(2)");
        }
        
        ord = newCell.getOrd();
        if ( this.itsCells.get(ord - 1) != oldCell )
        {
            throw new SystemErrorException(mName +
                                           "oldCell not at newCell.getOrd().");
        }
        
        if ( ! this.validCell(newCell, false) )
        {
            throw new SystemErrorException(mName + "invalid cell");
        }
        
        newCell.validateReplacementCell(oldCell);
                
        /* Move the listeners from the old incarnation to the new */
        newCell.setListeners(oldCell.getListeners());
        oldCell.setListeners(null);
        
        /* replace the old incarnation with the new */
        if ( oldCell != this.itsCells.set(ord - 1, newCell) )
        {
            throw new SystemErrorException(mName + 
                                           "unexpected return from set()");
        }
        
        // verify ord of new cell
        if ( itsCells.get(newCell.getOrd() - 1) != newCell )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }
        
        if ( ( this.itsMveType == MatrixVocabElement.matrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.matrixType.PREDICATE ) )
        {
            oldCell.deregisterPreds();
        }
        
        // update the index for the new cell value
        newCell.updateIndexForReplacementVal(oldCell);
        db.idx.replaceElement(newCell);
        
        /* Note changes between the old and new incarnations of the 
         * data cell, and notify the listeners.
         */
        newCell.noteChange(oldCell, newCell);
        newCell.notifyListenersOfChange();
        
        if ( ( this.itsMveType == MatrixVocabElement.matrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.matrixType.PREDICATE ) )
        {
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
     *                                  JRM -- 3/15/08 
     *
     * Changes:
     *
     *    - None.
     */
    
    public void endCascade(Database db)
        throws SystemErrorException
    {
        final String mName = "Column::endCascade(): ";
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName + 
                "call to endCascade() when this.cascadeInProgress is false?!?");
        }
        
        if ( this.pendingSet == null )
        {
            throw new SystemErrorException(mName + "this.pendingSet is null?!?");
        }
        
        for ( Cell c : this.pendingSet )
        {
            ((DataCell)c).exitCascade();
        }
        
        /* If temporal ordering, sort cells by onset, and assign new ords 
         * as necessary.
         */
        if ( this.db.temporalOrdering )
        {
            this.sortItsCells();
        }
        
        if ( this.pending != null )
        {
            this.db.cl.replaceDataColumn(this.pending);
            
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterExternalListener(ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "DataColumn::deregisterExternalListener()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "DataColumn::deregisterInternalListener()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected DataColumnListeners getListeners()
    {
        
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void noteChange(DataColumn oldDC,
                              DataColumn newDC)
        throws SystemErrorException
    {
        final String mName = "DataColumn::noteChange()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void notifyListenersOfChange()
        throws SystemErrorException
    {
        final String mName = "DataColumn::notifyListenersOfChange()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void notifyListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName = "DataColumn::notifyListenersOfDeletion()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerExternalListener(ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "DataColumn::registerExternalListener()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerInternalChangeListener(long id)
        throws SystemErrorException
    {
        final String mName = "DataColumn::registerInternalChangeListener()";
        
        if ( this.listeners == null )
        {
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
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void setListeners(DataColumnListeners listeners)
        throws SystemErrorException
    {
        final String mName = "DataCell::setListeners()";
        
        if ( this.listeners == null )
        {
            if ( listeners == null )
            {
                throw new SystemErrorException(mName + 
                        ": this.listeners is already null");
            }
            
            this.listeners = listeners;
            this.listeners.updateItsCol(this);
        }
        else
        {
            if ( listeners != null )
            {
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
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected MatrixVocabElement constructInitMatrixVE()
        throws SystemErrorException
    {
        final String mName = "DataColumn::constructInitMatrixVE(): ";
        FormalArgument fa = null;
        MatrixVocabElement mve = null;
        
        if ( this.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "dc.id != INVALID_ID");
        }
         
        if ( this.getItsMveID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "dc.itsMveID != INVALID_ID");
        }
        
        if ( this.getItsMveType() == MatrixVocabElement.matrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "dc.itsMveType == UNDEFINED");
        }
        
        if ( ( ! ( this.db.IsValidSVarName(this.getName()) ) ) ||
             ( this.db.vl.inVocabList(this.getName()) ) ||
             ( this.db.cl.inColumnList(this.getName()) ) )
        {
            throw new SystemErrorException(mName +
                    "Column name invalid or in use");
        }
        
        mve = new MatrixVocabElement(this.db, this.getName());
        
        mve.setType(this.getItsMveType());
        
        /* Construct the initial, singleton formal argument appropriate to
         * the type of the DataColumn.
         */
        
        if ( this.itsMveType == MatrixVocabElement.matrixType.FLOAT )
        {
            fa = new FloatFormalArg(this.db);
        }
        else if ( this.itsMveType == MatrixVocabElement.matrixType.INTEGER )
        {
            fa = new IntFormalArg(this.db);
        }
        else if ( this.itsMveType == MatrixVocabElement.matrixType.MATRIX )
        {
            fa = new UnTypedFormalArg(this.db);
        }
        else if ( this.itsMveType == MatrixVocabElement.matrixType.NOMINAL )
        {
            fa = new NominalFormalArg(this.db);
        }
        else if ( this.itsMveType == MatrixVocabElement.matrixType.PREDICATE )
        {
            fa = new PredFormalArg(this.db);
        }
        else if ( this.itsMveType == MatrixVocabElement.matrixType.TEXT )
        {
            fa = new TextStringFormalArg(this.db);
        }
        else
        {
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
     *                                              JRM -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     */
    
    private MatrixVocabElement lookupMatrixVE(long mveID)
        throws SystemErrorException
    {
        final String mName = "DataColumn::lookupMatrixVE(mveID): ";
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
                    "mveID doesn't refer to a matrix vocab element");
        }

        mve = (MatrixVocabElement)dbe;
        
        return mve;
        
    } /* DataColumn::lookupMatrixVE(mveID) */

    
    /**
     * itsCellsToDBString()
     *
     * Construct a string containing the values of the cells in a
     * format that displays the full status of the arguments and 
     * facilitates debugging.  
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    protected String itsCellsToDBString()
        throws SystemErrorException
    {
        final String mName = "DataColumn::itsCellsToDBString(): ";
        int i = 0;
        String s;

        if ( ( this.itsCells == null ) ||
             ( this.numCells == 0 ) )
        {
            s = "(itsCells ())";
        }
        else
        {
            this.numCells = this.itsCells.size();

            if ( this.numCells <= 0 )
            {
                throw new SystemErrorException(mName + "numCells <= 0");
            }

            s = new String("(itsCells (");
                
            while ( i < (this.numCells - 1) )
            {
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
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    protected String itsCellsToString()
        throws SystemErrorException
    {
        final String mName = "DataColumn::itsCellsToString(): ";
        int i = 0;
        String s;

        if ( ( this.itsCells == null ) ||
             ( this.numCells == 0 ) )
        {
            s = "()";
        }
        else
        {
            this.numCells = this.itsCells.size();

            if ( this.numCells <= 0 )
            {
                throw new SystemErrorException(mName + "numCells <= 0");
            }
            
            s = new String("(");
        
            while ( i < (numCells - 1) )
            {
                s += this.getCell(i + 1).toString() + ", ";
                i++;
            }
            
            s += getCell(i + 1).toString();

            s += ")";
        }
        
        return s;
        
    } /* DataColumn::itsCellsToString() */
    
  
    /*************************************************************************/
    /************************* Cells Management: *****************************/
    /*************************************************************************/
    
    /**
     * appendCell()
     *
     * Append the supplied DataCell to the end of the vector of cells.
     *
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to allocate a new instance of DataCellListeners, and
     *      assign it to the newly created cell.  Added call to generate
     *      DataColumn cell insertion message to listeners.  Finally, added
     *      calls to mark the beginning and end of any resulting cascade of 
     *      changes.
     *      
     *                                          JRM -- 2/10/08
     */
    
    protected void appendCell(DataCell newCell)
        throws SystemErrorException
    {
        final String mName = "DataColumn::appendCell(): ";
        DataCellListeners nl = null;
        
        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }
        
        if ( ! this.validCell(newCell, true) )
        {
            throw new SystemErrorException(mName + "invalid cell");
        }
        
        newCell.validateNewCell();
        
        this.db.cascadeStart();
            
        this.db.idx.addElement(newCell);
        newCell.insertValInIndex();
        
        this.itsCells.add(newCell);
        this.numCells = this.itsCells.size();
        newCell.setOrd(this.numCells);
        
        if ( itsCells.elementAt(newCell.getOrd() - 1) != newCell )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }
        
        nl = new DataCellListeners(db, newCell);
        newCell.setListeners(nl);
        
        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive an end cascade message.
         */
        
        this.listeners.notifyListenersOfCellInsertion(newCell.getID());
        
        if ( ( this.itsMveType == MatrixVocabElement.matrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.matrixType.PREDICATE ) )
        {
            newCell.registerPreds();
        }
        
        this.db.cascadeEnd();
        
        return;
        
    } /* DataColumn::appendCell(newCell) */

    
    /**
     * getCell()
     *
     * Get the cell at the specified ord.  Note that this function returns
     * the actual cell -- not a copy.  For almost all purposes, the returned
     * cell should be treated as read only.
     *
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected DataCell getCell(int ord)
        throws SystemErrorException
    {
        final String mName = "DataColumn::getCell(): ";
        DataCell retVal = null;
        
        if ( ( ord < 1 ) || ( ord > this.numCells ) )
        {
            throw new SystemErrorException(mName + "ord out of range");
        }
        
        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }
        
        retVal = this.itsCells.get(ord - 1);
        
        if ( retVal.getOrd() != ord )
        {
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
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected DataCell getCellCopy(int ord)
        throws SystemErrorException
    {
        
        return new DataCell(this.getCell(ord));
        
    } /* DataColumn::getCell() */
    
    
    /**
     * insertCell()
     *
     * Insert the supplied DataCell in the indicated location in the vector
     * of DataCells.  Update the ords of the cells after the insertion point.
     *
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to allocate a new instance of DataCellListeners, and
     *      assign it to the newly created cell.  Added call to generate
     *      DataColumn cell insertion message to listeners.  Finally, added
     *      calls to mark the beginning and end of any resulting cascade of 
     *      changes.
     *                                              JRM -- 2/10/08
     */
    
    protected void insertCell(DataCell newCell,
                              int ord)
       throws SystemErrorException
    {
        final String mName = "DataColumn::insertCell(): ";
        int i;
        DataCell dc = null;
        DataCellListeners nl = null;
        
        if ( ( ord < 1 ) || ( ord > this.numCells + 1) )
        {
            throw new SystemErrorException(mName + "ord out of range");
        }
        
        if ( ! this.validCell(newCell, true) )
        {
            throw new SystemErrorException(mName + "invalid cell");
        }
        
        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }
        
        newCell.validateNewCell();
        
        this.db.cascadeStart();
        
        // set the replacement cell's ord
        newCell.setOrd(ord);
            
        this.db.idx.addElement(newCell);
        newCell.insertValInIndex();
        
        // insert the cell & update numCells
        this.itsCells.insertElementAt(newCell, (ord - 1));
        this.numCells = this.itsCells.size();
        
        // verify ord of new cell
        if ( itsCells.get(newCell.getOrd() - 1) != newCell )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }
        
        nl = new DataCellListeners(db, newCell);
        newCell.setListeners(nl);
        
        this.listeners.notifyListenersOfCellInsertion(newCell.getID());

        if ( ( this.itsMveType == MatrixVocabElement.matrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.matrixType.PREDICATE ) )
        {
            newCell.registerPreds();
        }
        
        // Update ords for insertion
        for ( i = ord; i < this.numCells; i++ )
        {
            dc = itsCells.elementAt(i);
            
            if ( dc.cascadeGetOrd() != i )
            {
                throw new SystemErrorException(mName + "unexpected old ord" + i);
            }
            
            /* update the ord */
            dc.cascadeSetOrd(i + 1);
        }

        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive an end cascade message.
         */
        
        this.db.cascadeEnd();
        
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
     *                                      JRM -- 8/30/07
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
        throws SystemErrorException
    {
        final String mName = "DataColumn::removeCell(): ";
        int i;
        DataCell dc = null;
        DataCell retVal = null;
        
        
        if ( ( targetOrd < 1 ) || ( targetOrd > this.numCells ) )
        {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }

        dc = itsCells.elementAt(targetOrd - 1);
         
        if ( dc == null )
        {
            throw new SystemErrorException(mName + "can't get target cell");
        }
        
        if ( dc.getID() != targetID )
        {
            throw new SystemErrorException(mName + "target ID mismatch");
        }
        
        this.db.cascadeStart();
        
        dc.notifyListenersOfDeletion();
        dc.setListeners(null);

        this.listeners.notifyListenersOfCellDeletion(dc.getID());
        
        dc.removeValFromIndex();
        
        if ( dc != this.itsCells.remove(targetOrd -1) )
        {
            throw new SystemErrorException(mName + "remove failed?!?!");
        }
        
        retVal = dc;
        
        this.numCells = this.itsCells.size();
        
        for ( i = targetOrd - 1; i < this.numCells; i++)
        {
            dc = this.itsCells.get(i);
            
            if ( dc == null )
            {
                throw new SystemErrorException(mName + "can't get cell" + i);
            }
            
            if ( dc.getOrd() != i + 2 )
            {
                throw new SystemErrorException(mName + "unexpected cell ord " + 
                        dc.getOrd() + "(" + (i + 2) + " expected)");
            }
            
            dc.cascadeSetOrd(i + 1);
        }
        
        this.db.cascadeEnd();
        
        return retVal;
        
    } /* DataColumn::removeCell */
    
    
    /**
     * replaceCell()
     *
     * Replace the DataCell at targetOrd in this.itsCells with the supplied 
     * DataCell.  Return the old DataCell.
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to notify listeners of changes, and to transfer the 
     *      instance of DataCellListeners from the old to the new incarnation
     *      of the data cell.  Added calls to mark the beginning and end of 
     *      any resulting cascade of changes.
     *                                              JRM -- 2/10/08
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
        throws SystemErrorException
    {
        final String mName = "DataColumn::replaceCell(): ";
        int i;
        DataCell oldCell = null;
        DataCell retVal = null;
        
        if ( ( targetOrd < 1 ) || ( targetOrd > this.numCells ) )
        {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }
        
        if ( ! this.validCell(newCell, false) )
        {
            throw new SystemErrorException(mName + "invalid cell");
        }
        
        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + 
                                           "itsCells not initialized?!?");
        }
        
        oldCell = this.itsCells.get(targetOrd - 1);
        
        if ( oldCell == null )
        {
            throw new SystemErrorException(mName + "can't get old cell.");
        }
        else if ( this.db.idx.getElement(oldCell.getID()) != oldCell )
        {
            throw new SystemErrorException(mName + "oldCell not in index?!?");
        }
        
        this.db.cascadeStart();
        
        newCell.setOrd(targetOrd);
        
        oldCell.cascadeSetPending(newCell);
        
        /* If temporal order is enabled, we will sort the cells, and assign
         * new ords as neccessary when we receive the end cascade message.
         */
                
        this.db.cascadeEnd();
        
        retVal = oldCell;
        
        if ( oldCell.getListeners() != null )
        {
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
     *                                              JRM -- 1/22/08
     *
     * Changes:
     *
     *    - None.
     */
    
    // TODO: re-write this method with sanity checking.
    
    protected void sortCells()
        throws SystemErrorException
    {
        final String mName = "DataColumn::sortCells(): ";
        int i;
        class dc_onset_comp implements java.util.Comparator<DataCell>
        {
            public int compare(DataCell dc1, DataCell dc2)
            {
                int result = 0;
                
                if ( dc1.onset.insane_gt(dc2.onset) )
                {
                    result = 1;
                }
                else if ( dc1.onset.insane_lt(dc2.onset) )
                {
                    result = -1;
                }
                
                return result;
            }
        };
        dc_onset_comp comp = new dc_onset_comp();

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + "itsCells null on entry");
        }
        
        if ( this.numCells > 0 )
        {
            java.util.Collections.sort(this.itsCells, comp);
            
            for ( i = 0; i < this.numCells; i++ )
            {
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
     *                                              JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void sortItsCells()
        throws SystemErrorException
    {
        final String mName = "DataColumn::sortItsCells(): ";
        int i;
        class cascade_dc_onset_comp implements java.util.Comparator<DataCell>
        {
            public int compare(DataCell dc1, DataCell dc2)
            {
                int result = 0;
                boolean threwSystemErrorException = false;
                String systemErrorExceptionString = null;
                
                try
                {
                    if ( dc1.cascadeGetOnset().insane_gt(dc2.cascadeGetOnset()) )
                    {
                        result = 1;
                    }
                    else if (dc1.cascadeGetOnset().insane_lt(dc2.cascadeGetOnset()))
                    {
                        result = -1;
                    }
                }
                
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                    systemErrorExceptionString = e.getMessage();
                }
                
                if ( threwSystemErrorException )
                {
                    System.out.printf(
                            "%s: Caught SystemErrorException \"%s\".\n",
                            mName, systemErrorExceptionString);
                    System.out.flush();
                    
                    int i = 1/0; // to force an arithmatic exception.
                }
                
                return result;
            }
        };
        cascade_dc_onset_comp comp = new cascade_dc_onset_comp();
        
        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName + "cascade not in progress?!");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName + "itsCells null on entry");
        }
        
        if ( this.numCells > 0 )
        {
            int oldOrd;
            DataCell c;
            java.util.Collections.sort(this.itsCells, comp);
            
            for ( i = 0; i < this.numCells; i++ )
            {
                c = this.itsCells.get(i);
                
                if ( c.cascadeGetOrd() != i + 1 )
                {
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
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - Added the newCell parameter that allows us to skip the cell id
     *      check on new cells that haven't been added to the index yet.
     */
    
    private boolean validCell(DataCell cell,
                              boolean newCell)
        throws SystemErrorException
    {
        final String mName = "DataColumn::validCell(): ";
        
        if ( cell == null )
        {
            throw new SystemErrorException(mName + "cell null on entry.");
        }
        
        if ( cell.getDB() != this.getDB() )
        {
            return false;
        }
        
        if ( cell.getItsMveID() != this.itsMveID )
        {
            return false;
        }
        
        if ( cell.getItsColID() != this.getID() )
        {
            return false;
        }
        
        if ( cell.getItsMveType() != this.itsMveType )
        {
            throw new SystemErrorException(mName + "type mismatch");
        }
        
        if ( ( ! newCell ) && ( cell.getID() == DBIndex.INVALID_ID ) )
        {
            throw new SystemErrorException(mName + "cell has invalid ID");
        }
        
        
        // other sanity checks needed?
        
        return true;
        
    } /* DataColumn::validCell() */
  
    
    /*************************************************************************/
    /********************* MVE Change Management: ****************************/
    /*************************************************************************/
    
    /**
     * VEChanged()
     *
     * Needed to implement the InternalVocabElementListener interface.
     *
     * Handle the various housekeeping required to process a change in the 
     * MatrixVocabElement associated with this DataColumn.
     *
     * Verify that the db and mveID match -- throw system errors if they don't. 
     *
     * Verify that this.cascadeInProgress is true.  Throw a system
     * it it isn't.
     *
     *                                  JRM -- 3/20/08 
     *
     * Changes:
     *
     *    - None.
     */
        
    public void VEChanged(Database db,
                         long VEID,
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
        final String mName = "DataColumn::VEChanged(): ";
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( this.itsMveID != VEID )
        {
            throw new SystemErrorException(mName + "mveID mismatch.");
        }
        
        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName + 
                                           "cascade not in progress?!?.");
        }
        
        if ( this.pending != null )
        {
            throw new SystemErrorException(mName + 
                    "this.pending not null on entry?!?!");
        }
        
        if ( ( nameChanged ) || ( varLenChanged ) )
        {
            this.pending = new DataColumn(this);
        
            if ( nameChanged )
            {
                if ( this.name.compareTo(oldName) != 0 )
                {
                    throw new SystemErrorException(mName + 
                                                   "oldName != this.name");
                }
                this.pending.setName(newName);
            }

            if ( varLenChanged )
            {
                if ( this.varLen != oldVarLen )
                {
                    throw new SystemErrorException(mName + 
                                                   "oldVarLen != this.varLen");
                }
                this.pending.varLen = newVarLen;
            }
        }
        
        if ( fargListChanged )
        {
            for ( Cell c : this.itsCells )
            {
                ((DataCell)c).cascadeUpdateForFargListChange(n2o,
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
        
    } /* DataColumn::VEChanged() */
 
    
    /**
     * VEDeleted()
     *
     * Needed to implement the InternalVocabElementListener interface.
     *
     * This method should never be called, as the DataColumn should have 
     * de-registered before the MatrixVocabElement is deleted.
     *
     * Throw a system error if the method is ever called.
     *
     *                                  JRM -- 3/20/08 
     *
     * Changes:
     *
     *    - None.
     */
        
    public void VEDeleted(Database db,
                   long VEID)
        throws SystemErrorException
    {
        final String mName = "DataColumn::VEDeleted(): ";
        
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

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
   
    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) Three argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs to the
     *         three argument constructor.  Verify that all fields of the 
     *         DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the three argument constructor fails on invalid
     *         input.  
     *
     * 2) Four argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs along with 
     *         a comment to the four argument constructor.  Verify that all 
     *         fields of the DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the constructor fails when passed invalid data.
     *
     * 3) Seven argument constructor:
     *
     *      As per four argument constructor, save that an onset, offset and 
     *      a cell value value are supplied to the constructor.  Verify that 
     *      the onset, offset, and value appear in the DataCell.
     *
     *      Verify that the constructor fails when passed invalid data.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct DataCells of all types using the 3, 4, and 7 argument
     *         constructors with a variety of values.  
     *
     *         Now use the copy constructor to create a copies of the 
     *         DataCells, and verify that the copies are correct. 
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad 
     *         value that can be tested unless we go an manualy break some 
     *         DataCells created with the other constructors.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsMveID(), getItsMveType() getOnset(), 
     *      getOffset(), getVal(), setOnset(), setOffset(), and setVal() 
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the Cell.TestAccessorMethods() 
     *      method.
     *
     *      Verify that the accessors fail on invalid data.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods. 
     *
     * 
     *************************************************************************/

    /**
     * TestClassDataColumn()
     *
     * Main routine for tests of class DataColumn.
     *
     *                                      JRM -- 12/25/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassDataColumn(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class DataColumn:\n");
        
        if ( ! Test3ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Test5ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestCellManagement(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class DataCell.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class DataCell.\n\n");
        }
        
        return pass;
        
    } /* DataCell::TestClassDataColumn() */
    
    
    /**
     * Test3ArgConstructor()
     * 
     * Run a battery of tests on the three argument constructor for this 
     * class, and on the instance returned.
     * 
     *                                              JRM -- 11/13/07
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
            "Testing 3 argument constructor for class DataColumn              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long f_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        DataColumn dc = null;
        MatrixVocabElement f_mve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.matrixType.FLOAT);
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.matrixType.INTEGER);
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.matrixType.MATRIX);
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.matrixType.NOMINAL);
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.matrixType.PREDICATE);
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.matrixType.TEXT);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_col == null ) ||
             ( i_col == null ) ||
             ( m_col == null ) ||
             ( n_col == null ) ||
             ( p_col == null ) ||
             ( t_col == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( f_col == null )
                {
                    outStream.printf("f_col allocation failed.\n");
                }
                
                if ( i_col == null )
                {
                    outStream.printf("i_col allocation failed.\n");
                }
                
                if ( m_col == null )
                {
                    outStream.printf("m_col allocation failed.\n");
                }
                
                if ( n_col == null )
                {
                    outStream.printf("n_col allocation failed.\n");
                }
                
                if ( p_col == null )
                {
                    outStream.printf("p_col allocation failed.\n");
                }
                
                if ( t_col == null )
                {
                    outStream.printf("t_col allocation failed.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else
        {
            failures += Verify3ArgConstInit(db,
                                      f_col,
                                      "f_col",
                                      "f_col",
                                      MatrixVocabElement.matrixType.FLOAT,
                                      outStream,
                                      verbose);

            failures += Verify3ArgConstInit(db,
                                      i_col,
                                      "i_col",
                                      "i_col",
                                      MatrixVocabElement.matrixType.INTEGER,
                                      outStream,
                                      verbose);

            failures += Verify3ArgConstInit(db,
                                      m_col,
                                      "m_col",
                                      "m_col",
                                      MatrixVocabElement.matrixType.MATRIX,
                                      outStream,
                                      verbose);

            failures += Verify3ArgConstInit(db,
                                      n_col,
                                      "n_col",
                                      "n_col",
                                      MatrixVocabElement.matrixType.NOMINAL,
                                      outStream,
                                      verbose);

            failures += Verify3ArgConstInit(db,
                                      p_col,
                                      "p_col",
                                      "p_col",
                                      MatrixVocabElement.matrixType.PREDICATE,
                                      outStream,
                                      verbose);

            failures += Verify3ArgConstInit(db,
                                      t_col,
                                      "t_col",
                                      "t_col",
                                      MatrixVocabElement.matrixType.TEXT,
                                      outStream,
                                      verbose);
        }
        
        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc = new DataColumn(null, "f_col", 
                                    MatrixVocabElement.matrixType.FLOAT);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                         "FLOAT) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                         "FLOAT) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                     "FLOAT) failed to throw a system error " +
                                     "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a null name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc = new DataColumn(db, null, 
                                    MatrixVocabElement.matrixType.FLOAT);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, null, " +
                                         "FLOAT) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, null, "+
                                         "FLOAT) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, null, "+
                                     "FLOAT) failed to throw a system error " +
                                     "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an empty name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc = new DataColumn(db, "", 
                                    MatrixVocabElement.matrixType.FLOAT);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"\", " +
                                         "FLOAT) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"\", "+
                                         "FLOAT) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"\", "+
                                     "FLOAT) failed to throw a system error " +
                                     "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc = new DataColumn(db, " invalid ", 
                                    MatrixVocabElement.matrixType.FLOAT);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", " +
                                         "FLOAT) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", "+
                                         "FLOAT) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", "+
                                     "FLOAT) failed to throw a system error " +
                                     "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a name that is in use */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col = new DataColumn(db, "f_col", 
                                       MatrixVocabElement.matrixType.FLOAT);
                f_colID = db.addColumn(f_col);
                f_col = db.getDataColumn(f_colID);
                f_mveID = f_col.getItsMveID();
                f_mve = db.getMatrixVE(f_mveID);

                dc = new DataColumn(db, "f_col", 
                                    MatrixVocabElement.matrixType.FLOAT);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( f_colID == DBIndex.INVALID_ID ) ||
                 ( f_col == null ) ||
                 ( f_mveID == DBIndex.INVALID_ID ) ||
                 ( f_mve == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_colID == DBIndex.INVALID_ID ) ||
                         ( f_col == null ) ||
                         ( f_mveID == DBIndex.INVALID_ID ) ||
                         ( f_mve == null ) )
                    {
                        outStream.printf(
                                "Errors allocating f_col. f_colID = %d, " +
                                "f_mveID = %d.  Setup for new DataColumn(db, " +
                                "\"f_col\", FLOAT) test failed.\n", 
                                f_colID, f_mveID);
                    }
 
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"f_col\", " +
                                         "FLOAT) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"f_col\", "+
                                         "FLOAT) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"f_col\", "+
                                     "FLOAT) failed to throw a system error " +
                                     "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid type */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc = new DataColumn(db, "valid", 
                                    MatrixVocabElement.matrixType.UNDEFINED);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"valid\", " +
                                         "UNDEFINED) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"valid\", "+
                                         "UNDEFINED) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"valid\", "+
                                 "UNDEFINED) failed to throw a system error " +
                                 "exception.\n");
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
        
    } /* DataCell::Test3ArgConstructor() */
    
    
    /**
     * Test5ArgConstructor()
     * 
     * Run a battery of tests on the five argument constructor for this 
     * class, and on the instance returned.
     * 
     *                                              JRM -- 12/27/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test5ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 5 argument constructor for class DataColumn              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long f_mve1ID = DBIndex.INVALID_ID;
        long f_mve2ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long i_mve1ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long n_mve1ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long p_mve1ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long t_mve1ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement f_mve1 = null;
        MatrixVocabElement f_mve2 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement i_mve1 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement n_mve1 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement p_mve1 = null;
        MatrixVocabElement t_mve0 = null;
        MatrixVocabElement t_mve1 = null;
        DataColumn f_col0 = null;
        DataColumn f_col1 = null;
        DataColumn i_col0 = null;
        DataColumn i_col1 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn n_col0 = null;
        DataColumn n_col1 = null;
        DataColumn p_col0 = null;
        DataColumn p_col1 = null;
        DataColumn t_col0 = null;
        DataColumn t_col1 = null;
        DataColumn dc = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            f_mve0 = new MatrixVocabElement(db, "f_col0");
            f_mve0.setType(MatrixVocabElement.matrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve0.appendFormalArg(farg);
            db.vl.addElement(f_mve0);
            f_mve0ID = f_mve0.getID();
            f_col0 = new DataColumn(db, "f_col0", false, true, f_mve0ID);
            
            f_mve1 = new MatrixVocabElement(db, "f_col1");
            f_mve1.setType(MatrixVocabElement.matrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve1.appendFormalArg(farg);
            db.vl.addElement(f_mve1);
            f_mve1ID = f_mve1.getID();
            f_col1 = new DataColumn(db, "f_col1", true, false, f_mve1ID);
            
            /* we use f_mve2 & f_mve2ID for failures tests */
            f_mve2 = new MatrixVocabElement(db, "f_col2");
            f_mve2.setType(MatrixVocabElement.matrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve2.appendFormalArg(farg);
            db.vl.addElement(f_mve2);
            f_mve2ID = f_mve2.getID();
            
            
            i_mve0 = new MatrixVocabElement(db, "i_col0");
            i_mve0.setType(MatrixVocabElement.matrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve0.appendFormalArg(farg);
            db.vl.addElement(i_mve0);
            i_mve0ID = i_mve0.getID();
            i_col0 = new DataColumn(db, "i_col0", false, true, i_mve0ID);
            
            i_mve1 = new MatrixVocabElement(db, "i_col1");
            i_mve1.setType(MatrixVocabElement.matrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve1.appendFormalArg(farg);
            db.vl.addElement(i_mve1);
            i_mve1ID = i_mve1.getID();
            i_col1 = new DataColumn(db, "i_col1", true, false, i_mve1ID);
            
            
            m_mve0 = new MatrixVocabElement(db, "m_col0");
            m_mve0.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve0.appendFormalArg(farg);
            db.vl.addElement(m_mve0);
            m_mve0ID = m_mve0.getID();
            m_col0 = new DataColumn(db, "m_col0", false, true, m_mve0ID);
            
            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);
            
            
            n_mve0 = new MatrixVocabElement(db, "n_col0");
            n_mve0.setType(MatrixVocabElement.matrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve0.appendFormalArg(farg);
            db.vl.addElement(n_mve0);
            n_mve0ID = n_mve0.getID();
            n_col0 = new DataColumn(db, "n_col0", false, true, n_mve0ID);
            
            n_mve1 = new MatrixVocabElement(db, "n_col1");
            n_mve1.setType(MatrixVocabElement.matrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve1.appendFormalArg(farg);
            db.vl.addElement(n_mve1);
            n_mve1ID = n_mve1.getID();
            n_col1 = new DataColumn(db, "n_col1", true, false, n_mve1ID);
            
            
            p_mve0 = new MatrixVocabElement(db, "p_col0");
            p_mve0.setType(MatrixVocabElement.matrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve0.appendFormalArg(farg);
            db.vl.addElement(p_mve0);
            p_mve0ID = p_mve0.getID();
            p_col0 = new DataColumn(db, "p_col0", false, true, p_mve0ID);
            
            p_mve1 = new MatrixVocabElement(db, "p_col1");
            p_mve1.setType(MatrixVocabElement.matrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve1.appendFormalArg(farg);
            db.vl.addElement(p_mve1);
            p_mve1ID = p_mve1.getID();
            p_col1 = new DataColumn(db, "p_col1", true, false, p_mve1ID);
            
            
            t_mve0 = new MatrixVocabElement(db, "t_col0");
            t_mve0.setType(MatrixVocabElement.matrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve0.appendFormalArg(farg);
            db.vl.addElement(t_mve0);
            t_mve0ID = t_mve0.getID();
            t_col0 = new DataColumn(db, "t_col0", false, true, t_mve0ID);
            
            t_mve1 = new MatrixVocabElement(db, "t_col1");
            t_mve1.setType(MatrixVocabElement.matrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve1.appendFormalArg(farg);
            db.vl.addElement(t_mve1);
            t_mve1ID = t_mve1.getID();
            t_col1 = new DataColumn(db, "t_col1", true, false, t_mve1ID);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_mve0 == null ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve1 == null ) ||
             ( f_mve1ID == DBIndex.INVALID_ID ) ||
             ( f_col1 == null ) ||
             ( f_mve2 == null ) ||
             ( f_mve2ID == DBIndex.INVALID_ID ) ||
             ( i_mve0 == null ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve1 == null ) ||
             ( i_mve1ID == DBIndex.INVALID_ID ) ||
             ( i_col1 == null ) ||
             ( m_mve0 == null ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve1 == null ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( n_mve0 == null ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve1 == null ) ||
             ( n_mve1ID == DBIndex.INVALID_ID ) ||
             ( n_col1 == null ) ||
             ( p_mve0 == null ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve1 == null ) ||
             ( p_mve1ID == DBIndex.INVALID_ID ) ||
             ( p_col1 == null ) ||
             ( t_mve0 == null ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve1 == null ) ||
             ( t_mve1ID == DBIndex.INVALID_ID ) ||
             ( t_col1 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_mve0 == null ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) )
                {
                    outStream.print("f_col0 allocation failed.\n");
                }
                
                if ( ( f_mve1 == null ) ||
                     ( f_mve1ID == DBIndex.INVALID_ID ) ||
                     ( f_col1 == null ) )
                {
                    outStream.print("f_col1 allocation failed.\n");
                }
                
                if ( ( f_mve2 == null ) ||
                     ( f_mve2ID == DBIndex.INVALID_ID ) )
                {
                    outStream.print("f_mve2 allocation failed.\n");
                }
                
                if ( ( i_mve0 == null ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ) 
                {
                    outStream.print("i_col0 allocation failed.\n");
                }
                
                if ( ( i_mve1 == null ) ||
                     ( i_mve1ID == DBIndex.INVALID_ID ) ||
                     ( i_col1 == null ) )
                {
                    outStream.print("i_col1 allocation failed.\n");
                }
                
                if ( ( m_mve0 == null ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) )
                {
                    outStream.print("m_col0 allocation failed.\n");
                }
                
                if ( ( m_mve1 == null ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) ) 
                {
                    outStream.print("m_col1 allocation failed.\n");
                }
                
                if ( ( n_mve0 == null ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) )
                {
                    outStream.print("n_col0 allocation failed.\n");
                }
                
                if ( ( n_mve1 == null ) ||
                     ( n_mve1ID == DBIndex.INVALID_ID ) ||
                     ( n_col1 == null ) )
                {
                    outStream.print("n_col1 allocation failed.\n");
                }
                
                if ( ( p_mve0 == null ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) )
                {
                    outStream.print("p_col0 allocation failed.\n");
                }
                
                if ( ( p_mve1 == null ) ||
                     ( p_mve1ID == DBIndex.INVALID_ID ) ||
                     ( p_col1 == null ) )
                {
                    outStream.print("p_col1 allocation failed.\n");
                }
                
                if ( ( t_mve0 == null ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) )
                {
                    outStream.print("t_col0 allocation failed.\n");
                }
                
                if ( ( t_mve1 == null ) ||
                     ( t_mve1ID == DBIndex.INVALID_ID ) ||
                     ( t_col1 == null ) )
                {
                    outStream.print("t_col1 allocation failed.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else
        {
            failures += Verify5ArgConstInit(db, 
                                    f_col0, 
                                    "f_col0", 
                                    "f_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    f_mve0.getVarLen(),
                                    f_mve0ID, 
                                    MatrixVocabElement.matrixType.FLOAT, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    f_col1, 
                                    "f_col1", 
                                    "f_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    f_mve1.getVarLen(),
                                    f_mve1ID, 
                                    MatrixVocabElement.matrixType.FLOAT, 
                                    outStream,
                                    verbose);


            failures += Verify5ArgConstInit(db, 
                                    i_col0, 
                                    "i_col0", 
                                    "i_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    i_mve0.getVarLen(),
                                    i_mve0ID, 
                                    MatrixVocabElement.matrixType.INTEGER, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    i_col1, 
                                    "i_col1", 
                                    "i_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    i_mve1.getVarLen(),
                                    i_mve1ID, 
                                    MatrixVocabElement.matrixType.INTEGER, 
                                    outStream,
                                    verbose);


            failures += Verify5ArgConstInit(db, 
                                    m_col0, 
                                    "m_col0", 
                                    "m_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    m_mve0.getVarLen(),
                                    m_mve0ID, 
                                    MatrixVocabElement.matrixType.MATRIX, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    m_col1, 
                                    "m_col1", 
                                    "m_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    m_mve1.getVarLen(),
                                    m_mve1ID, 
                                    MatrixVocabElement.matrixType.MATRIX, 
                                    outStream,
                                    verbose);


            failures += Verify5ArgConstInit(db, 
                                    n_col0, 
                                    "n_col0", 
                                    "n_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    n_mve0.getVarLen(),
                                    n_mve0ID, 
                                    MatrixVocabElement.matrixType.NOMINAL, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    n_col1, 
                                    "n_col1", 
                                    "n_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    n_mve1.getVarLen(),
                                    n_mve1ID, 
                                    MatrixVocabElement.matrixType.NOMINAL, 
                                    outStream,
                                    verbose);


            failures += Verify5ArgConstInit(db, 
                                    p_col0, 
                                    "p_col0", 
                                    "p_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    p_mve0.getVarLen(),
                                    p_mve0ID, 
                                    MatrixVocabElement.matrixType.PREDICATE, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    p_col1, 
                                    "p_col1", 
                                    "p_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    p_mve1.getVarLen(),
                                    p_mve1ID, 
                                    MatrixVocabElement.matrixType.PREDICATE, 
                                    outStream,
                                    verbose);


            failures += Verify5ArgConstInit(db, 
                                    t_col0, 
                                    "t_col0", 
                                    "t_col0", 
                                    false, /* expected hidden */
                                    true,  /* expected read only */
                                    t_mve0.getVarLen(),
                                    t_mve0ID, 
                                    MatrixVocabElement.matrixType.TEXT, 
                                    outStream,
                                    verbose);

            failures += Verify5ArgConstInit(db, 
                                    t_col1, 
                                    "t_col1", 
                                    "t_col1", 
                                    true,   /* expected hidden */
                                    false,  /* expected read only */
                                    t_mve1.getVarLen(),
                                    t_mve1ID, 
                                    MatrixVocabElement.matrixType.TEXT, 
                                    outStream,
                                    verbose);
        }
        
        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(null, "f_col2", false, true, f_mve2ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                "false, true, f_mve2_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                "false, true, f_mve2_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(null, \"f_col\", "+
                                "false, true, f_mve2_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a null name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, null, false, true, f_mve2ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, null, "+
                                "false, true, f_mve2_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, null, "+
                                "false, true, f_mve2_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, null, "+
                                "false, true, f_mve2_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an empty name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, "", false, true, f_mve2ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"\", "+
                                "false, true, f_mve2_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"\", "+
                                "false, true, f_mve2_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"\", "+
                                "false, true, f_mve2_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid name */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, " invalid ", false, true, f_mve2ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", "+
                                "false, true, f_mve2_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", "+
                                "false, true, f_mve2_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \" invalid \", "+
                                "false, true, f_mve2_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a name mismatch */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, "f_col3", false, true, f_mve2ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"f_col3\", "+
                                "false, true, f_mve2_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"f_col3\", "+
                                "false, true, f_mve2_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"f_col3\", "+
                                "false, true, f_mve2_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on the invalid ID */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, "f_col2", false, true, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                                "false, true, INVALID_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                                "false, true, INVALID_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                                "false, true, INVALID_ID) failed to throw a " +
                                "system error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a bad mve ID */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(db, "f_col2", false, true, f_mve2ID + 1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                            "false, true, f_mve2ID + 1) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                                "false, true, f_mve2ID + 1) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(db, \"f_col2\", "+
                            "false, true, f_mve2ID + 1) failed to throw a " +
                            "system error exception.\n");
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
        
    } /* DataCell::Test5ArgConstructor() */

    
    /**
     * TestAccessors()
     * 
     * Run a battery of tests on the accessor methods of the class. 
     *
     * The accessor methods don't care about the type of the column.  
     * Thus for simplicity, most of tests are performed on a float column.
     * 
     *                                              JRM -- 12/31/07
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
            "Testing class DataColumn accessors                               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long f_col0ID = DBIndex.INVALID_ID;
        long i_col0ID = DBIndex.INVALID_ID;
        long m_col0ID = DBIndex.INVALID_ID;
        long m_col1ID = DBIndex.INVALID_ID;
        long n_col0ID = DBIndex.INVALID_ID;
        long p_col0ID = DBIndex.INVALID_ID;
        long t_col0ID = DBIndex.INVALID_ID;
        long fargID;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement t_mve0 = null;
        DataColumn f_col0 = null;
        DataColumn i_col0 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn n_col0 = null;
        DataColumn p_col0 = null;
        DataColumn t_col0 = null;
        DataColumn dc = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell3 = null;
        DataCell f_cell4 = null;
        DataCell f_cell5 = null;
        DataCell f_cell6 = null;
        DataCell f_cell7 = null;
        DataCell f_cell8 = null;
        DataCell i_cell0 = null;
        DataCell m_cell0 = null;
        DataCell n_cell0 = null;
        DataCell p_cell0 = null;
        DataCell t_cell0 = null;
        DataCell f_cell0_c = null;
        DataCell f_cell1_c = null;
        DataCell f_cell2_c = null;
        DataCell f_cell3_c = null;
        DataCell f_cell4_c = null;
        DataCell f_cell5_c = null;
        DataCell f_cell6_c = null;
        DataCell f_cell7_c = null;
        DataCell f_cell8_c = null;
        DataCell i_cell0_c = null;
        DataCell m_cell0_c = null;
        DataCell n_cell0_c = null;
        DataCell p_cell0_c = null;
        DataCell t_cell0_c = null;
        TimeStamp f_onset0 = null;
        TimeStamp f_onset1 = null;
        TimeStamp f_onset2 = null;
        TimeStamp f_onset3 = null;
        TimeStamp f_onset4 = null;
        TimeStamp f_onset5 = null;
        TimeStamp f_onset6 = null;
        TimeStamp f_onset7 = null;
        TimeStamp f_onset8 = null;
        TimeStamp f_offset0 = null;
        TimeStamp f_offset1 = null;
        TimeStamp f_offset2 = null;
        TimeStamp f_offset3 = null;
        TimeStamp f_offset4 = null;
        TimeStamp f_offset5 = null;
        TimeStamp f_offset6 = null;
        TimeStamp f_offset7 = null;
        TimeStamp f_offset8 = null;
        Vector<DataValue> f_arg_list0 = null;
        Vector<DataValue> f_arg_list1 = null;
        Vector<DataValue> f_arg_list2 = null;
        Vector<DataValue> f_arg_list3 = null;
        Vector<DataValue> f_arg_list4 = null;
        Vector<DataValue> f_arg_list5 = null;
        Vector<DataValue> f_arg_list6 = null;
        Vector<DataValue> f_arg_list7 = null;
        Vector<DataValue> f_arg_list8 = null;
        Matrix f_matrix0 = null;
        Matrix f_matrix1 = null;
        Matrix f_matrix2 = null;
        Matrix f_matrix3 = null;
        Matrix f_matrix4 = null;
        Matrix f_matrix5 = null;
        Matrix f_matrix6 = null;
        Matrix f_matrix7 = null;
        Matrix f_matrix8 = null;
        FormalArgument farg = null;
        DataValue arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First, allocate a selection of columns and cells to work with. After
         * we insert the columns in the database, we use db.cl.getColumn()
         * to get a reference to the actual column in the database.
         */
        try
        {
            db = new ODBCDatabase();
            

            f_col0 = new DataColumn(db, "f_col0", 
                                    MatrixVocabElement.matrixType.FLOAT);
            f_col0ID = db.addColumn(f_col0);
            f_col0 = (DataColumn)db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);
           
            
            i_col0 = new DataColumn(db, "i_col0", 
                                    MatrixVocabElement.matrixType.INTEGER);
            i_col0ID = db.addColumn(i_col0);
            i_col0 = (DataColumn)db.cl.getColumn(i_col0ID);
            i_mve0ID = i_col0.getItsMveID();
            i_mve0 = db.getMatrixVE(i_mve0ID);
            
            
            m_col0 = new DataColumn(db, "m_col0", 
                                    MatrixVocabElement.matrixType.MATRIX);
            m_col0ID = db.addColumn(m_col0);
            m_col0 = (DataColumn)db.cl.getColumn(m_col0ID);
            m_mve0ID = m_col0.getItsMveID();
            m_mve0 = db.getMatrixVE(m_mve0ID);
            
            
            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);
            db.cl.addColumn(m_col1);
            m_col1ID = m_col1.getID();

            
            n_col0 = new DataColumn(db, "n_col0", 
                                    MatrixVocabElement.matrixType.NOMINAL);
            n_col0ID = db.addColumn(n_col0);
            n_col0 = (DataColumn)db.cl.getColumn(n_col0ID);
            n_mve0ID = n_col0.getItsMveID();
            n_mve0 = db.getMatrixVE(n_mve0ID);
            
            
            p_col0 = new DataColumn(db, "p_col0", 
                                    MatrixVocabElement.matrixType.PREDICATE);
            p_col0ID = db.addColumn(p_col0);
            p_col0 = (DataColumn)db.cl.getColumn(p_col0ID);
            p_mve0ID = p_col0.getItsMveID();
            p_mve0 = db.getMatrixVE(p_mve0ID);
            
            
            t_col0 = new DataColumn(db, "t_col0", 
                                    MatrixVocabElement.matrixType.TEXT);
            t_col0ID = db.addColumn(t_col0);
            t_col0 = (DataColumn)db.cl.getColumn(t_col0ID);
            t_mve0ID = t_col0.getItsMveID();
            t_mve0 = db.getMatrixVE(t_mve0ID);
            
            
            f_onset0 = new TimeStamp(db.getTicks(), 60);
            f_offset0 = new TimeStamp(db.getTicks(), 120);
            f_arg_list0 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);
            f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID, 
                                       f_onset0, f_offset0, f_matrix0);

            f_onset1 = new TimeStamp(db.getTicks(), 180);
            f_offset1 = new TimeStamp(db.getTicks(), 240);
            f_arg_list1 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            f_arg_list1.add(arg);
            f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
            f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID, 
                                       f_onset1, f_offset1, f_matrix1);

            f_onset2 = new TimeStamp(db.getTicks(), 300);
            f_offset2 = new TimeStamp(db.getTicks(), 360);
            f_arg_list2 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 2.0);
            f_arg_list2.add(arg);
            f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
            f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID, 
                                       f_onset2, f_offset2, f_matrix2);

            f_onset3 = new TimeStamp(db.getTicks(), 420);
            f_offset3 = new TimeStamp(db.getTicks(), 480);
            f_arg_list3 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 3.0);
            f_arg_list3.add(arg);
            f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
            f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID, 
                                       f_onset3, f_offset3, f_matrix3);

            f_onset4 = new TimeStamp(db.getTicks(), 540);
            f_offset4 = new TimeStamp(db.getTicks(), 600);
            f_arg_list4 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 4.0);
            f_arg_list4.add(arg);
            f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
            f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID, 
                                       f_onset4, f_offset4, f_matrix4);

            f_onset5 = new TimeStamp(db.getTicks(), 660);
            f_offset5 = new TimeStamp(db.getTicks(), 720);
            f_arg_list5 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 5.0);
            f_arg_list5.add(arg);
            f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
            f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID, 
                                       f_onset5, f_offset5, f_matrix5);

            f_onset6 = new TimeStamp(db.getTicks(), 780);
            f_offset6 = new TimeStamp(db.getTicks(), 840);
            f_arg_list6 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 6.0);
            f_arg_list6.add(arg);
            f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
            f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID, 
                                       f_onset6, f_offset6, f_matrix6);

            f_onset7 = new TimeStamp(db.getTicks(), 900);
            f_offset7 = new TimeStamp(db.getTicks(), 960);
            f_arg_list7 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 7.0);
            f_arg_list7.add(arg);
            f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
            f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID, 
                                       f_onset7, f_offset7, f_matrix7);

            f_onset8 = new TimeStamp(db.getTicks(), 900);
            f_offset8 = new TimeStamp(db.getTicks(), 960);
            f_arg_list8 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 8.0);
            f_arg_list8.add(arg);
            f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
            f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID, 
                                       f_onset8, f_offset8, f_matrix8);

            i_cell0 = new DataCell(db, "i_cell0", i_col0ID, i_mve0ID);
            m_cell0 = new DataCell(db, "m_cell0", m_col0ID, m_mve0ID);
            n_cell0 = new DataCell(db, "n_cell0", n_col0ID, n_mve0ID);
            p_cell0 = new DataCell(db, "p_cell0", p_col0ID, p_mve0ID);
            t_cell0 = new DataCell(db, "t_cell0", t_col0ID, t_mve0ID);

            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_col0ID == DBIndex.INVALID_ID ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve0 == null ) ||
             ( i_col0ID == DBIndex.INVALID_ID ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve0 == null ) ||
             ( m_col0ID == DBIndex.INVALID_ID ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve0 == null ) ||
             ( m_col1ID == DBIndex.INVALID_ID ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( m_mve1 == null ) ||
             ( n_col0ID == DBIndex.INVALID_ID ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve0 == null ) ||
             ( p_col0ID == DBIndex.INVALID_ID ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve0 == null ) ||
             ( t_col0ID == DBIndex.INVALID_ID ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve0 == null ) ||
             ( f_onset0 == null ) ||
             ( f_offset0 == null ) ||
             ( f_cell0 == null ) ||
             ( f_onset1 == null ) ||
             ( f_offset1 == null ) ||
             ( f_cell1 == null ) ||
             ( f_onset2 == null ) ||
             ( f_offset2 == null ) ||
             ( f_cell2 == null ) ||
             ( f_onset3 == null ) ||
             ( f_offset3 == null ) ||
             ( f_cell3 == null ) ||
             ( f_onset4 == null ) ||
             ( f_offset4 == null ) ||
             ( f_cell4 == null ) ||
             ( f_onset5 == null ) ||
             ( f_offset5 == null ) ||
             ( f_cell5 == null ) ||
             ( f_onset6 == null ) ||
             ( f_offset6 == null ) ||
             ( f_cell6 == null ) ||
             ( f_onset7 == null ) ||
             ( f_offset7 == null ) ||
             ( f_cell7 == null ) ||
             ( i_cell0 == null ) ||
             ( m_cell0 == null ) ||
             ( n_cell0 == null ) ||
             ( p_cell0 == null ) ||
             ( t_cell0 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_col0ID == DBIndex.INVALID_ID ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) ||
                     ( f_mve0 == null ) )
                {
                    outStream.printf("f_col0 alloc failed.  f_col0ID = %d, " + 
                            "f_mve0ID = %d\n", f_col0ID, f_mve0ID);
                }
                
                if ( ( i_col0ID == DBIndex.INVALID_ID ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ||
                     ( i_mve0 == null ) )
                {
                    outStream.printf("i_col0 alloc failed.  i_col0ID = %d, " + 
                            "i_mve0ID = %d\n", i_col0ID, i_mve0ID);
                }
                
                if ( ( m_col0ID == DBIndex.INVALID_ID ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) ||
                     ( m_mve0 == null ) )
                {
                    outStream.printf("m_col0 alloc failed.  m_col0ID = %d, " + 
                            "m_mve0ID = %d\n", m_col0ID, m_mve0ID);
                }
                
                if ( ( m_col1ID == DBIndex.INVALID_ID ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) ||
                     ( m_mve1 == null ) )
                {
                    outStream.printf("m_col1 alloc failed.  m_col1ID = %d, " + 
                            "m_mve1ID = %d\n", m_col1ID, m_mve1ID);
                }
                
                if ( ( n_col0ID == DBIndex.INVALID_ID ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) ||
                     ( n_mve0 == null ) )
                {
                    outStream.printf("n_col0 alloc failed.  n_col0ID = %d, " + 
                            "n_mve0ID = %d\n", n_col0ID, n_mve0ID);
                }
                
                if ( ( p_col0ID == DBIndex.INVALID_ID ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) ||
                     ( p_mve0 == null ) )
                {
                    outStream.printf("p_col0 alloc failed.  p_col0ID = %d, " + 
                            "p_mve0ID = %d\n", p_col0ID, p_mve0ID);
                }
                
                if ( ( t_col0ID == DBIndex.INVALID_ID ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) ||
                     ( t_mve0 == null ) )
                {
                    outStream.printf("t_col0 alloc failed.  t_col0ID = %d, " + 
                            "t_mve0ID = %d\n", t_col0ID, t_mve0ID);
                }
                
                if ( ( f_onset0 == null ) ||
                     ( f_offset0 == null ) ||
                     ( f_cell0 == null ) )
                {
                    outStream.printf("f_cell0 alloc failed.\n");
                }
                
                if ( ( f_onset1 == null ) ||
                     ( f_offset1 == null ) ||
                     ( f_cell1 == null ) )
                {
                    outStream.printf("f_cell1 alloc failed.\n");
                }
                
                if ( ( f_onset2 == null ) ||
                     ( f_offset2 == null ) ||
                     ( f_cell2 == null ) )
                {
                    outStream.printf("f_cell2 alloc failed.\n");
                }
                
                if ( ( f_onset3 == null ) ||
                     ( f_offset3 == null ) ||
                     ( f_cell3 == null ) )
                {
                    outStream.printf("f_cell3 alloc failed.\n");
                }
                
                if ( ( f_onset4 == null ) ||
                     ( f_offset4 == null ) ||
                     ( f_cell4 == null ) )
                {
                    outStream.printf("f_cell4 alloc failed.\n");
                }
                
                if ( ( f_onset5 == null ) ||
                     ( f_offset5 == null ) ||
                     ( f_cell5 == null ) )
                {
                    outStream.printf("f_cell5 alloc failed.\n");
                }
                
                if ( ( f_onset6 == null ) ||
                     ( f_offset6 == null ) ||
                     ( f_cell6 == null ) )
                {
                    outStream.printf("f_cell6 alloc failed.\n");
                }
                
                if ( ( f_onset7 == null ) ||
                     ( f_offset7 == null ) ||
                     ( f_cell7 == null ) )
                {
                    outStream.printf("f_cell7 alloc failed.\n");
                }
                
                if ( i_cell0 == null )
                {
                    outStream.printf("i_cell0 alloc failed.\n");
                }
                
                if ( m_cell0 == null )
                {
                    outStream.printf("m_cell0 alloc failed.\n");
                }
                
                if ( n_cell0 == null )
                {
                    outStream.printf("n_cell0 alloc failed.\n");
                }
                
                if ( p_cell0 == null )
                {
                    outStream.printf("p_cell0 alloc failed.\n");
                }
                
                if ( t_cell0 == null )
                {
                    outStream.printf("t_cell0 alloc failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else if ( ( f_col0.itsCells == null ) ||
                  ( i_col0.itsCells == null ) ||
                  ( m_col0.itsCells == null ) ||
                  ( n_col0.itsCells == null ) ||
                  ( p_col0.itsCells == null ) ||
                  ( t_col0.itsCells == null ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                    "one or more column itsCells fields not initialized.\n");
            }
        }
        
        
        /********************************/
        /*** test inherited accessors ***/
        /********************************/
        
        if ( failures == 0 )
        {
            failures += TestAccessors(db,
                                      f_col0,
                                      "f_col0",
                                      "i_col0",
                                      false,
                                      false,
                                      0,
                                      outStream,
                                      verbose);
        }
        
        
        /******************************************/
        /*** test accessors local to this class ***/
        /******************************************/
        
        /*** test getItsCells() setItsCells() ***/
        
        /* getItsCells() and setItsCells() are only used internally, thus
         * testing can be fairly limited -- we will set itsCells to null, and
         * then back to the original value.  Make sure that numCells is 
         * updated accordingly.
         *
         * Since setItsCells() is intended purely for internal use, it has 
         * no error checking -- thus we need not verify that it fails as
         * expected.
         */
        
        if ( failures == 0 )
        {
            /* fcol_0 is empty at present, so start by adding some cells. */

            Vector<DataCell> saved_f_col0_cells = null;
            Vector<DataCell> saved_i_col0_cells = null;
            
            completed = false;
            threwSystemErrorException = false;
            try
            {
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
                f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
                f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 3 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 3 )
                    {
                        outStream.printf(
                                "f_col0.numCells = %d (3 expected)(0).\n", 
                                f_col0.numCells);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("get/set its cells test setup " +
                                "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("get/set its cells test setup threw " +
                                         "a system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
            
            /* Now run getItsCells() on DataColumns both with and without
             * cells and verify that we get the expected values.
             */
            if ( failures == 0 )
            {
                saved_f_col0_cells = f_col0.getItsCells();
                saved_i_col0_cells = i_col0.getItsCells();
                
                if ( ( saved_f_col0_cells != f_col0.itsCells ) ||
                     ( saved_i_col0_cells != i_col0.itsCells ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        if ( saved_f_col0_cells != f_col0.itsCells )
                        {
                            outStream.printf(
                                "saved_f_col0_cells != f_col0.itsCells (1).\n");
                        }
                        
                        if ( saved_i_col0_cells != i_col0.itsCells )
                        {
                            outStream.printf(
                                "saved_i_col0_cells != i_col0.itsCells (1).\n");
                        }
                    }
                }
            }
            
            /* next, use setItsCells() to set the itsCells fields of both 
             * DataColumns to null.  Verify that numCells is zero after 
             * the operation.
             */
            if ( failures == 0 )
            {
                f_col0.setItsCells(null);
                i_col0.setItsCells(null);
                
                if ( ( f_col0.itsCells != null ) ||
                     ( f_col0.getItsCells() != null ) ||
                     ( f_col0.numCells != 0 ) ||
                     ( i_col0.itsCells != null ) ||
                     ( i_col0.getItsCells() != null ) ||
                     ( i_col0.numCells != 0 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        if ( f_col0.itsCells != null )
                        {
                            outStream.printf(
                                "f_col0.itsCells != null (2).\n");
                        }
                        
                        if ( f_col0.getItsCells() != null )
                        {
                            outStream.printf(
                                    "f_col0.getItsCells != null (2).\n");
                        }
                        
                        if ( f_col0.numCells != 0 )
                        {
                            outStream.printf(
                                    "f_col0.numCells = %d (0 expected)(2).\n", 
                                    f_col0.numCells);
                        }
                        
                        if ( i_col0.itsCells != null )
                        {
                            outStream.printf(
                                "i_col0.itsCells != null (2).\n");
                        }
                        
                        if ( i_col0.getItsCells() != null )
                        {
                            outStream.printf(
                                    "i_col0.getItsCells != null. (2)\n");
                        }
                                                
                        if ( i_col0.numCells != 0 )
                        {
                            outStream.printf(
                                    "i_col0.numCells = %d (0 expected)(2).\n", 
                                    i_col0.numCells);
                        }
                    }
                }
            }
            
            /* finally, restore the itsCells fields to the saved values.  Verify
             * that numCells is updated appropriately.
             */
            if ( failures == 0 )
            {
                f_col0.setItsCells(saved_f_col0_cells);
                i_col0.setItsCells(saved_i_col0_cells);
                
                if ( ( f_col0.itsCells != saved_f_col0_cells ) ||
                     ( f_col0.getItsCells() != saved_f_col0_cells ) ||
                     ( f_col0.numCells != 3 ) ||
                     ( i_col0.itsCells != saved_i_col0_cells ) ||
                     ( i_col0.getItsCells() != saved_i_col0_cells ) ||
                     ( i_col0.numCells != 0 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        if ( f_col0.itsCells != saved_f_col0_cells )
                        {
                            outStream.printf(
                                "f_col0.itsCells != saved_f_col0_cells (3).\n");
                        }
                        
                        if ( f_col0.getItsCells() != saved_f_col0_cells )
                        {
                            outStream.printf(
                                    "f_col0.getItsCells != saved_f_col0_cells (3).\n");
                        }
                        
                        if ( f_col0.numCells != 3 )
                        {
                            outStream.printf(
                                    "f_col0.numCells = %d (3 expected)(3).\n", 
                                    f_col0.numCells);
                        }
                        
                        if ( i_col0.itsCells != saved_i_col0_cells )
                        {
                            outStream.printf(
                                "i_col0.itsCells != saved_i_col0_cells (3).\n");
                        }
                        
                        if ( i_col0.getItsCells() != saved_i_col0_cells )
                        {
                            outStream.printf(
                                    "i_col0.getItsCells != saved_i_col0_cells. (3)\n");
                        }
                                                
                        if ( i_col0.numCells != 0 )
                        {
                            outStream.printf(
                                    "i_col0.numCells = %d (0 expected)(3).\n", 
                                    i_col0.numCells);
                        }
                    }
                }
            }
        }
        
        /*** test getItsMveID(), getItsMveType() and setItsMveID() ***/
        
        /* setItsMveID() has already been tested extensively in its normal
         * mode (that is, setting the initial value of itsMve).  Thus in 
         * this test we restrict ourselves to verifying that it fails as 
         * expected.
         */
        
        if ( failures == 0 )
        {
            if ( ( f_col0.getItsMveID() != f_mve0.getID() ) ||
                 ( i_col0.getItsMveID() != i_mve0.getID() ) || 
                 ( m_col0.getItsMveID() != m_mve0.getID() ) ||
                 ( n_col0.getItsMveID() != n_mve0.getID() ) ||
                 ( p_col0.getItsMveID() != p_mve0.getID() ) ||
                 ( t_col0.getItsMveID() != t_mve0.getID() ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected value returned by one or " +
                                     "more calls to getItsMveID().\n");
                }
            }
            
            if ( ( f_col0.getItsMveType() != f_mve0.getType() ) ||
                 ( i_col0.getItsMveType() != i_mve0.getType() ) ||
                 ( m_col0.getItsMveType() != m_mve0.getType() ) ||
                 ( n_col0.getItsMveType() != n_mve0.getType() ) ||
                 ( p_col0.getItsMveType() != p_mve0.getType() ) ||
                 ( t_col0.getItsMveType() != t_mve0.getType() ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected value returned by one or " +
                                     "more calls to getItsMveType().\n");
                }
            }
        }
        
        /* try changing an exitisting mveID -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            try
            {
                f_col0.setItsMveID(p_mve0.getID());
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf("f_col0.setItsMveID(p_mve0.getID())" +
                                         " completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("f_col0.setItsMveID(p_mve0.getID()) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        /* try creating a new column, and setting its mve ID to the invalid id -
         * should fail.
         */
        if ( failures == 0 )
        {
            boolean setupCompleted = false;
            DataColumn newCol = null;
            
            completed = false;
            threwSystemErrorException = false;
            try
            {
                newCol = new DataColumn(db, "newCol0", 
                                        MatrixVocabElement.matrixType.FLOAT);
                
                setupCompleted = true;
                
                newCol.setItsMveID(DBIndex.INVALID_ID);
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! setupCompleted ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ! setupCompleted )
                    {
                        outStream.printf("setItsMveID() test setup didn't " +
                                "complete(1).\n");
                    }
                    
                    if ( completed )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(DBIndex.INVALID_ID) " +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(DBIndex.INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        /* try creating a new column, and setting its mve ID to the ID of 
         * an unused mve that is of the wrong type -- should fail.
         */
        if ( failures == 0 )
        {
            boolean setupCompleted = false;
            MatrixVocabElement i_mve1 = null;
            DataColumn newCol = null;
            
            completed = false;
            threwSystemErrorException = false;
            try
            {
                newCol = new DataColumn(db, "newCol1", 
                                        MatrixVocabElement.matrixType.FLOAT);
                
                i_mve1 = new MatrixVocabElement(db, "newCol1");
                i_mve1.setType(MatrixVocabElement.matrixType.INTEGER);
                i_mve1.appendFormalArg(new IntFormalArg(db, "<int0>"));
                db.vl.addElement(i_mve1);
                i_mve1 = db.vl.getMatrixVocabElement("newCol1");

                setupCompleted = true;
                
                newCol.setItsMveID(i_mve1.getID());
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( newCol == null ) ||
                 ( i_mve1 == null ) ||
                 ( i_mve1.getID() == DBIndex.INVALID_ID ) ||
                 ( ! setupCompleted ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( newCol == null )
                    {
                        outStream.printf("newCol == null(2).\n");
                    }
                        
                    if ( i_mve1 == null )
                    {
                        outStream.printf("( i_mve1 == null )(2).\n");
                    }
                        
                    if ( i_mve1.getID() == DBIndex.INVALID_ID )
                    {
                        outStream.printf(
                                "i_mve1.getID() == DBIndex.INVALID_ID(2).\n");
                    }
                        
                    if ( ! setupCompleted )
                    {
                        outStream.printf("setItsMveID() test setup didn't " +
                                "complete(2).\n");
                    }
                    
                    if ( completed )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(i_mve1.getID()) " +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(i_mve1.getID() " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        /* try creating a new column, and setting its mve ID to the ID of 
         * a non-mve -- should fail.
         */
        if ( failures == 0 )
        {
            boolean setupCompleted = false;
            DataColumn newCol = null;
            
            completed = false;
            threwSystemErrorException = false;
            try
            {
                newCol = new DataColumn(db, "newCol2", 
                                        MatrixVocabElement.matrixType.FLOAT);
                
                setupCompleted = true;
                
                newCol.setItsMveID(f_cell0_c.getID());
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( newCol == null ) ||
                 ( f_cell0_c.getID() == DBIndex.INVALID_ID ) ||
                 ( ! setupCompleted ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( newCol == null )
                    {
                        outStream.printf("newCol == null(3).\n");
                    }
                        
                    if ( f_cell0.getID() == DBIndex.INVALID_ID )
                    {
                        outStream.printf(
                                "f_cell0_c.getID() == DBIndex.INVALID_ID(3).\n");
                    }
                        
                    if ( ! setupCompleted )
                    {
                        outStream.printf("setItsMveID() test setup didn't " +
                                "complete(3).\n");
                    }
                    
                    if ( completed )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(f_cell0.getID()) " +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "newCol.setItsMveID(f_cell0.getID() " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        
        /*** test getVarLen() ***/
        
        if ( failures == 0 )
        {
            if ( ( f_col0.getVarLen() != f_mve0.getVarLen() ) ||
                 ( i_col0.getVarLen() != i_mve0.getVarLen() ) ||
                 ( m_col0.getVarLen() != m_mve0.getVarLen() ) ||
                 ( m_col1.getVarLen() != m_mve1.getVarLen() ) ||
                 ( n_col0.getVarLen() != n_mve0.getVarLen() ) ||
                 ( p_col0.getVarLen() != p_mve0.getVarLen() ) ||
                 ( t_col0.getVarLen() != t_mve0.getVarLen() ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected output from one or more " +
                            "calls to getVarLen().\n");
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
        
    } /* DataCell::TestAccessors() */

    
    /**
     * TestCellManagement()
     * 
     * Run a battery of tests on the cell management methods of the class. 
     *
     * With the exception of the validCell() method, the cell management methods
     * don't care about the type of the column.  Thus for simplicity, most of 
     * tests are performed on a float column.
     * 
     *                                              JRM -- 12/31/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestCellManagement(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing cell management methods for class DataColumn             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long f_col0ID = DBIndex.INVALID_ID;
        long i_col0ID = DBIndex.INVALID_ID;
        long m_col0ID = DBIndex.INVALID_ID;
        long n_col0ID = DBIndex.INVALID_ID;
        long p_col0ID = DBIndex.INVALID_ID;
        long t_col0ID = DBIndex.INVALID_ID;
        long fargID;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement t_mve0 = null;
        DataColumn f_col0 = null;
        DataColumn i_col0 = null;
        DataColumn m_col0 = null;
        DataColumn n_col0 = null;
        DataColumn p_col0 = null;
        DataColumn t_col0 = null;
        DataColumn dc = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell3 = null;
        DataCell f_cell4 = null;
        DataCell f_cell5 = null;
        DataCell f_cell6 = null;
        DataCell f_cell7 = null;
        DataCell f_cell8 = null;
        DataCell f_cell0_c = null;
        DataCell f_cell1_c = null;
        DataCell f_cell2_c = null;
        DataCell f_cell3_c = null;
        DataCell f_cell4_c = null;
        DataCell f_cell5_c = null;
        DataCell f_cell6_c = null;
        DataCell f_cell7_c = null;
        DataCell f_cell8_c = null;
        DataCell i_cell0 = null;
        DataCell m_cell0 = null;
        DataCell n_cell0 = null;
        DataCell p_cell0 = null;
        DataCell t_cell0 = null;
        TimeStamp f_onset0 = null;
        TimeStamp f_onset1 = null;
        TimeStamp f_onset2 = null;
        TimeStamp f_onset3 = null;
        TimeStamp f_onset4 = null;
        TimeStamp f_onset5 = null;
        TimeStamp f_onset6 = null;
        TimeStamp f_onset7 = null;
        TimeStamp f_onset8 = null;
        TimeStamp f_offset0 = null;
        TimeStamp f_offset1 = null;
        TimeStamp f_offset2 = null;
        TimeStamp f_offset3 = null;
        TimeStamp f_offset4 = null;
        TimeStamp f_offset5 = null;
        TimeStamp f_offset6 = null;
        TimeStamp f_offset7 = null;
        TimeStamp f_offset8 = null;
        Vector<DataValue> f_arg_list0 = null;
        Vector<DataValue> f_arg_list1 = null;
        Vector<DataValue> f_arg_list2 = null;
        Vector<DataValue> f_arg_list3 = null;
        Vector<DataValue> f_arg_list4 = null;
        Vector<DataValue> f_arg_list5 = null;
        Vector<DataValue> f_arg_list6 = null;
        Vector<DataValue> f_arg_list7 = null;
        Vector<DataValue> f_arg_list8 = null;
        Matrix f_matrix0 = null;
        Matrix f_matrix1 = null;
        Matrix f_matrix2 = null;
        Matrix f_matrix3 = null;
        Matrix f_matrix4 = null;
        Matrix f_matrix5 = null;
        Matrix f_matrix6 = null;
        Matrix f_matrix7 = null;
        Matrix f_matrix8 = null;
        FormalArgument farg = null;
        DataValue arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First, allocate a selection of columns and cells to work with. After
         * we insert the columns in the database, we use db.cl.getColumn()
         * to get a reference to the actual column in the database.
         */
        try
        {
            db = new ODBCDatabase();
            

            f_col0 = new DataColumn(db, "f_col0", 
                                    MatrixVocabElement.matrixType.FLOAT);
            f_col0ID = db.addColumn(f_col0);
            f_col0 = (DataColumn)db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);
           
            
            i_col0 = new DataColumn(db, "i_col0", 
                                    MatrixVocabElement.matrixType.INTEGER);
            i_col0ID = db.addColumn(i_col0);
            i_col0 = (DataColumn)db.cl.getColumn(i_col0ID);
            i_mve0ID = i_col0.getItsMveID();
            i_mve0 = db.getMatrixVE(i_mve0ID);
            
            
            m_col0 = new DataColumn(db, "m_col0", 
                                    MatrixVocabElement.matrixType.MATRIX);
            m_col0ID = db.addColumn(m_col0);
            m_col0 = (DataColumn)db.cl.getColumn(m_col0ID);
            m_mve0ID = m_col0.getItsMveID();
            m_mve0 = db.getMatrixVE(m_mve0ID);
            
            
            n_col0 = new DataColumn(db, "n_col0", 
                                    MatrixVocabElement.matrixType.NOMINAL);
            n_col0ID = db.addColumn(n_col0);
            n_col0 = (DataColumn)db.cl.getColumn(n_col0ID);
            n_mve0ID = n_col0.getItsMveID();
            n_mve0 = db.getMatrixVE(n_mve0ID);
            
            
            p_col0 = new DataColumn(db, "p_col0", 
                                    MatrixVocabElement.matrixType.PREDICATE);
            p_col0ID = db.addColumn(p_col0);
            p_col0 = (DataColumn)db.cl.getColumn(p_col0ID);
            p_mve0ID = p_col0.getItsMveID();
            p_mve0 = db.getMatrixVE(p_mve0ID);
            
            
            t_col0 = new DataColumn(db, "t_col0", 
                                    MatrixVocabElement.matrixType.TEXT);
            t_col0ID = db.addColumn(t_col0);
            t_col0 = (DataColumn)db.cl.getColumn(t_col0ID);
            t_mve0ID = t_col0.getItsMveID();
            t_mve0 = db.getMatrixVE(t_mve0ID);
            
            
            f_onset0 = new TimeStamp(db.getTicks(), 60);
            f_offset0 = new TimeStamp(db.getTicks(), 120);
            f_arg_list0 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);
            f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID, 
                                       f_onset0, f_offset0, f_matrix0);

            f_onset1 = new TimeStamp(db.getTicks(), 180);
            f_offset1 = new TimeStamp(db.getTicks(), 240);
            f_arg_list1 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            f_arg_list1.add(arg);
            f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
            f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID, 
                                       f_onset1, f_offset1, f_matrix1);

            f_onset2 = new TimeStamp(db.getTicks(), 300);
            f_offset2 = new TimeStamp(db.getTicks(), 360);
            f_arg_list2 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 2.0);
            f_arg_list2.add(arg);
            f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
            f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID, 
                                       f_onset2, f_offset2, f_matrix2);

            f_onset3 = new TimeStamp(db.getTicks(), 420);
            f_offset3 = new TimeStamp(db.getTicks(), 480);
            f_arg_list3 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 3.0);
            f_arg_list3.add(arg);
            f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
            f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID, 
                                       f_onset3, f_offset3, f_matrix3);

            f_onset4 = new TimeStamp(db.getTicks(), 540);
            f_offset4 = new TimeStamp(db.getTicks(), 600);
            f_arg_list4 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 4.0);
            f_arg_list4.add(arg);
            f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
            f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID, 
                                       f_onset4, f_offset4, f_matrix4);

            f_onset5 = new TimeStamp(db.getTicks(), 660);
            f_offset5 = new TimeStamp(db.getTicks(), 720);
            f_arg_list5 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 5.0);
            f_arg_list5.add(arg);
            f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
            f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID, 
                                       f_onset5, f_offset5, f_matrix5);

            f_onset6 = new TimeStamp(db.getTicks(), 780);
            f_offset6 = new TimeStamp(db.getTicks(), 840);
            f_arg_list6 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 6.0);
            f_arg_list6.add(arg);
            f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
            f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID, 
                                       f_onset6, f_offset6, f_matrix6);

            f_onset7 = new TimeStamp(db.getTicks(), 900);
            f_offset7 = new TimeStamp(db.getTicks(), 960);
            f_arg_list7 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 7.0);
            f_arg_list7.add(arg);
            f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
            f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID, 
                                       f_onset7, f_offset7, f_matrix7);

            f_onset8 = new TimeStamp(db.getTicks(), 900);
            f_offset8 = new TimeStamp(db.getTicks(), 960);
            f_arg_list8 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 8.0);
            f_arg_list8.add(arg);
            f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
            f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID, 
                                       f_onset8, f_offset8, f_matrix8);

            i_cell0 = new DataCell(db, "i_cell0", i_col0ID, i_mve0ID);
            m_cell0 = new DataCell(db, "m_cell0", m_col0ID, m_mve0ID);
            n_cell0 = new DataCell(db, "n_cell0", n_col0ID, n_mve0ID);
            p_cell0 = new DataCell(db, "p_cell0", p_col0ID, p_mve0ID);
            t_cell0 = new DataCell(db, "t_cell0", t_col0ID, t_mve0ID);

            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_col0ID == DBIndex.INVALID_ID ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve0 == null ) ||
             ( i_col0ID == DBIndex.INVALID_ID ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve0 == null ) ||
             ( m_col0ID == DBIndex.INVALID_ID ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve0 == null ) ||
             ( n_col0ID == DBIndex.INVALID_ID ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve0 == null ) ||
             ( p_col0ID == DBIndex.INVALID_ID ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve0 == null ) ||
             ( t_col0ID == DBIndex.INVALID_ID ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve0 == null ) ||
             ( f_onset0 == null ) ||
             ( f_offset0 == null ) ||
             ( f_cell0 == null ) ||
             ( f_onset1 == null ) ||
             ( f_offset1 == null ) ||
             ( f_cell1 == null ) ||
             ( f_onset2 == null ) ||
             ( f_offset2 == null ) ||
             ( f_cell2 == null ) ||
             ( f_onset3 == null ) ||
             ( f_offset3 == null ) ||
             ( f_cell3 == null ) ||
             ( f_onset4 == null ) ||
             ( f_offset4 == null ) ||
             ( f_cell4 == null ) ||
             ( f_onset5 == null ) ||
             ( f_offset5 == null ) ||
             ( f_cell5 == null ) ||
             ( f_onset6 == null ) ||
             ( f_offset6 == null ) ||
             ( f_cell6 == null ) ||
             ( f_onset7 == null ) ||
             ( f_offset7 == null ) ||
             ( f_cell7 == null ) ||
             ( i_cell0 == null ) ||
             ( m_cell0 == null ) ||
             ( n_cell0 == null ) ||
             ( p_cell0 == null ) ||
             ( t_cell0 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_col0ID == DBIndex.INVALID_ID ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) ||
                     ( f_mve0 == null ) )
                {
                    outStream.printf("f_col0 alloc failed.  f_col0ID = %d, " + 
                            "f_mve0ID = %d\n", f_col0ID, f_mve0ID);
                }
                
                if ( ( i_col0ID == DBIndex.INVALID_ID ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ||
                     ( i_mve0 == null ) )
                {
                    outStream.printf("i_col0 alloc failed.  i_col0ID = %d, " + 
                            "f_mve0ID = %d\n", i_col0ID, i_mve0ID);
                }
                
                if ( ( m_col0ID == DBIndex.INVALID_ID ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) ||
                     ( m_mve0 == null ) )
                {
                    outStream.printf("m_col0 alloc failed.  m_col0ID = %d, " + 
                            "f_mve0ID = %d\n", m_col0ID, m_mve0ID);
                }
                
                if ( ( n_col0ID == DBIndex.INVALID_ID ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) ||
                     ( n_mve0 == null ) )
                {
                    outStream.printf("n_col0 alloc failed.  n_col0ID = %d, " + 
                            "f_mve0ID = %d\n", n_col0ID, n_mve0ID);
                }
                
                if ( ( p_col0ID == DBIndex.INVALID_ID ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) ||
                     ( p_mve0 == null ) )
                {
                    outStream.printf("p_col0 alloc failed.  p_col0ID = %d, " + 
                            "f_mve0ID = %d\n", p_col0ID, p_mve0ID);
                }
                
                if ( ( t_col0ID == DBIndex.INVALID_ID ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) ||
                     ( t_mve0 == null ) )
                {
                    outStream.printf("t_col0 alloc failed.  t_col0ID = %d, " + 
                            "f_mve0ID = %d\n", t_col0ID, t_mve0ID);
                }
                
                if ( ( f_onset0 == null ) ||
                     ( f_offset0 == null ) ||
                     ( f_cell0 == null ) )
                {
                    outStream.printf("f_cell0 alloc failed.\n");
                }
                
                if ( ( f_onset1 == null ) ||
                     ( f_offset1 == null ) ||
                     ( f_cell1 == null ) )
                {
                    outStream.printf("f_cell1 alloc failed.\n");
                }
                
                if ( ( f_onset2 == null ) ||
                     ( f_offset2 == null ) ||
                     ( f_cell2 == null ) )
                {
                    outStream.printf("f_cell2 alloc failed.\n");
                }
                
                if ( ( f_onset3 == null ) ||
                     ( f_offset3 == null ) ||
                     ( f_cell3 == null ) )
                {
                    outStream.printf("f_cell3 alloc failed.\n");
                }
                
                if ( ( f_onset4 == null ) ||
                     ( f_offset4 == null ) ||
                     ( f_cell4 == null ) )
                {
                    outStream.printf("f_cell4 alloc failed.\n");
                }
                
                if ( ( f_onset5 == null ) ||
                     ( f_offset5 == null ) ||
                     ( f_cell5 == null ) )
                {
                    outStream.printf("f_cell5 alloc failed.\n");
                }
                
                if ( ( f_onset6 == null ) ||
                     ( f_offset6 == null ) ||
                     ( f_cell6 == null ) )
                {
                    outStream.printf("f_cell6 alloc failed.\n");
                }
                
                if ( ( f_onset7 == null ) ||
                     ( f_offset7 == null ) ||
                     ( f_cell7 == null ) )
                {
                    outStream.printf("f_cell7 alloc failed.\n");
                }
                
                if ( i_cell0 == null )
                {
                    outStream.printf("i_cell0 alloc failed.\n");
                }
                
                if ( m_cell0 == null )
                {
                    outStream.printf("m_cell0 alloc failed.\n");
                }
                
                if ( n_cell0 == null )
                {
                    outStream.printf("n_cell0 alloc failed.\n");
                }
                
                if ( p_cell0 == null )
                {
                    outStream.printf("p_cell0 alloc failed.\n");
                }
                
                if ( t_cell0 == null )
                {
                    outStream.printf("t_cell0 alloc failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else if ( ( f_col0.itsCells == null ) ||
                  ( i_col0.itsCells == null ) ||
                  ( m_col0.itsCells == null ) ||
                  ( n_col0.itsCells == null ) ||
                  ( p_col0.itsCells == null ) ||
                  ( t_col0.itsCells == null ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                    "one or more column itsCells fields not initialized.\n");
            }
        }
        
        
        /* test append cell */
        if ( failures == 0 )
        {
            String expectedString = 
                    "((1, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:01:000, 00:00:02:000, (0.0)))";
            String expectedDBString = 
                    "(itsCells " +
                        "((DataCell (id 19) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:05:000)) " +
                            "(offset (60,00:00:06:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 20) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 19) " +
                                            "(itsValue 2.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 21) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:03:000)) " +
                            "(offset (60,00:00:04:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 22) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 21) " +
                                            "(itsValue 1.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 23) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:01:000)) " +
                            "(offset (60,00:00:02:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 24) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 23) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
                f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
                f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 3 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 3 )
                    {
                        outStream.printf("f_col0 = %d (3 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString.compareTo(f_col0.itsCellsToString()) 
                         != 0 )
                    {
                        outStream.printf(
                                "Unexpected f_col0.itsCellsToString(1): \"%s\"\n", 
                                f_col0.itsCellsToString());
                    }
                    
                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString()) 
                         != 0 )
                    {
                        outStream.printf(
                                "Unexpected f_col0.itsCellsToDBString(1): \"%s\"\n", 
                                f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "appendCell() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("appendCell() test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* remove the existing cells in preparation for the next test */
        if ( failures == 0 )
        {
            String expectedString = "()";
            String expectedDBString = "(itsCells ())";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try 
            {
                f_col0.removeCell(3, f_cell0_c.getID());
                f_col0.removeCell(2, f_cell1_c.getID());
                f_col0.removeCell(1, f_cell2_c.getID());
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0numCells = %d (0 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString.compareTo(f_col0.itsCellsToString()) 
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToString(2): \"%s\"\n", 
                            f_col0.itsCellsToString());
                    }
                    
                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString()) 
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToDBString(2): \"%s\"\n", 
                            f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "removeCell() test 1 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("removeCell() test 1 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* test insert cell */
        if ( failures == 0 )
        {
            String expectedString = 
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(3, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(4, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedDBString = 
                    "(itsCells " +
                        "((DataCell (id 27) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:09:000)) " +
                            "(offset (60,00:00:10:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 28) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 27) " +
                                            "(itsValue 4.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 25) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:07:000)) " +
                            "(offset (60,00:00:08:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 26) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 25) " +
                                            "(itsValue 3.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 31) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:13:000)) " +
                            "(offset (60,00:00:14:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 32) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 31) " +
                                            "(itsValue 6.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell (id 29) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 4) " +
                            "(onset (60,00:00:11:000)) " +
                            "(offset (60,00:00:12:000)) " +
                            "(val " +
                                "(Matrix (mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 30) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 29) " +
                                            "(itsValue 5.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(f_cell3_c = new DataCell(f_cell3), 1);
                f_col0.insertCell(f_cell4_c = new DataCell(f_cell4), 1);
                f_col0.insertCell(f_cell5_c = new DataCell(f_cell5), 3);
                f_col0.insertCell(f_cell6_c = new DataCell(f_cell6), 3);
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 4 ) ||
                 ( expectedString.compareTo(f_col0.itsCellsToString()) != 0 ) ||
                 ( expectedDBString.compareTo(
                        f_col0.itsCellsToDBString()) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 4 )
                    {
                        outStream.printf("f_col0 = %d (4 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString.compareTo(f_col0.itsCellsToString()) 
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToString(3): \"%s\"\n", 
                            f_col0.itsCellsToString());
                    }
                    
                    if ( expectedDBString.compareTo(f_col0.itsCellsToDBString()) 
                         != 0 )
                    {
                        outStream.printf(
                            "Unexpected f_col0.itsCellsToDBString(3): \"%s\"\n", 
                            f_col0.itsCellsToDBString());
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "insertCell() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("insertCell() test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* remove cells again -- test removeCell() more fully in passing */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String expectedString0 = 
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(3, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(4, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedString1 = 
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:13:000, 00:00:14:000, (6.0)), " +
                     "(3, 00:00:11:000, 00:00:12:000, (5.0)))";
            String expectedString2 = 
                    "((1, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(2, 00:00:13:000, 00:00:14:000, (6.0)))";
            String expectedString3 = 
                    "((1, 00:00:13:000, 00:00:14:000, (6.0)))";
            String expectedString4 = "()";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try 
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.removeCell(2, f_cell3_c.getID());
                testString1 = f_col0.itsCellsToString();
                f_col0.removeCell(3, f_cell5_c.getID());
                testString2 = f_col0.itsCellsToString();
                f_col0.removeCell(1, f_cell4_c.getID());
                testString3 = f_col0.itsCellsToString();
                f_col0.removeCell(1, f_cell6_c.getID());
                testString4 = f_col0.itsCellsToString();
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0 = %d (0 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n", 
                            testString0);
                    }
                    
                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n", 
                            testString1);
                    }
                    
                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n", 
                            testString2);
                    }
                    
                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n", 
                            testString3);
                    }
                    
                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n", 
                            testString4);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "removeCell() test 2 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("removeCell() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* next, test the replaceCell() and getCellCopy() methods. Deal with 
         * the singleton cell case first...
         */
        if ( failures == 0 )
        {
            String testString0 = "";
            String testString1 = "";
            String testString2 = "";
            String testString3 = "";
            String expectedString0 =
                    "()";
            String expectedString1 = 
                    "((1, 00:00:01:000, 00:00:02:000, (0.0)))";
            String expectedString2 = 
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)))";
            String expectedString3 = 
                    "(1, 00:00:01:000, 00:00:02:000, (0.0))";
            DataCell f_cell0a = null;
            DataCell expected_old_cell = null;
            DataCell old_cell = null;
            Matrix m = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try 
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
                testString1 = f_col0.itsCellsToString();
                expected_old_cell = f_col0.getCell(1);
                f_cell0a = f_col0.getCellCopy(1);
                m = f_cell0a.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(10.0);
                f_cell0a.setVal(m);
                old_cell = f_col0.replaceCell(f_cell0a, 1);
                testString2 = f_col0.itsCellsToString();
                testString3 = old_cell.toString();
                
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 1 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( old_cell != expected_old_cell ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 1 )
                    {
                        outStream.printf("f_col0 = %d (1 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n", 
                            testString0);
                    }
                    
                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n", 
                            testString1);
                    }
                    
                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n", 
                            testString2);
                    }
                    
                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n", 
                            testString3);
                    }
                    
                    if ( old_cell != expected_old_cell )
                    {
                        outStream.printf("old_cell != expected_old_cell\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "replaceCell() test 1 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("replaceCell() test 1 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* ...and then with multiple entries.
         */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String testString5 = null;
            String testString6 = null;
            String testString7 = null;
            String expectedString0 =
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)))";
            String expectedString1 = 
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)))";
            String expectedString2 = 
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString3 = 
                    "(3, 00:00:05:000, 00:00:06:000, (2.0))";
            String expectedString4 = 
                    "((1, 00:00:01:000, 00:00:02:000, (10.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (40.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString5 = 
                    "(2, 00:00:03:000, 00:00:04:000, (1.0))";
            String expectedString6 = 
                    "((1, 00:00:01:000, 00:00:02:000, (50.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (40.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (30.0)))";
            String expectedString7 = 
                    "(1, 00:00:01:000, 00:00:02:000, (10.0))";
            DataCell cell = null;
            DataCell expected_old_cell0 = null;
            DataCell expected_old_cell1 = null;
            DataCell expected_old_cell2 = null;
            DataCell old_cell0 = null;
            DataCell old_cell1 = null;
            DataCell old_cell2 = null;
            Matrix m = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try 
            {
                testString0 = f_col0.itsCellsToString();
                f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
                testString1 = f_col0.itsCellsToString();
                
                expected_old_cell0 = f_col0.getCell(3);
                cell = f_col0.getCellCopy(3);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(30.0);
                cell.setVal(m);
                old_cell0 = f_col0.replaceCell(cell, 3);
                testString2 = f_col0.itsCellsToString();
                testString3 = old_cell0.toString();
                
                expected_old_cell1 = f_col0.getCell(2);
                cell = f_col0.getCellCopy(2);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(40.0);
                cell.setVal(m);
                old_cell1 = f_col0.replaceCell(cell, 2);
                testString4 = f_col0.itsCellsToString();
                testString5 = old_cell1.toString();
                
                expected_old_cell2 = f_col0.getCell(1);
                cell = f_col0.getCellCopy(1);
                m = cell.getVal();
                ((FloatDataValue)(m.getArg(0))).setItsValue(50.0);
                cell.setVal(m);
                old_cell2 = f_col0.replaceCell(cell, 1);
                testString6 = f_col0.itsCellsToString();
                testString7 = old_cell2.toString();
                
                /* tidy up for the next test */
                f_col0.removeCell(3, f_cell2_c.getID());
                f_col0.removeCell(2, f_cell1_c.getID());
                f_col0.removeCell(1, f_cell0_c.getID());
                
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 0 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( expectedString5.compareTo(testString5) != 0 ) ||
                 ( expectedString6.compareTo(testString6) != 0 ) ||
                 ( expectedString7.compareTo(testString7) != 0 ) ||
                 ( old_cell0 != expected_old_cell0 ) ||
                 ( old_cell1 != expected_old_cell1 ) ||
                 ( old_cell2 != expected_old_cell2 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 0 )
                    {
                        outStream.printf("f_col0.numCells = %d (0 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n", 
                            testString0);
                    }
                    
                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n", 
                            testString1);
                    }
                    
                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n", 
                            testString2);
                    }
                    
                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n", 
                            testString3);
                    }
                    
                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n", 
                            testString4);
                    }
                    
                    if ( expectedString5.compareTo(testString5) != 0 )
                    {
                        outStream.printf("Unexpected testString5: \"%s\"\n", 
                            testString5);
                    }
                    
                    if ( expectedString6.compareTo(testString6) != 0 )
                    {
                        outStream.printf("Unexpected testString6: \"%s\"\n", 
                            testString6);
                    }
                    
                    if ( expectedString7.compareTo(testString7) != 0 )
                    {
                        outStream.printf("Unexpected testString7: \"%s\"\n", 
                            testString7);
                    }
                    
                    if ( old_cell0 != expected_old_cell0 )
                    {
                        outStream.printf("old_cell0 != expected_old_cell0\n");
                    }
                    
                    if ( old_cell1 != expected_old_cell1 )
                    {
                        outStream.printf("old_cell1 != expected_old_cell1\n");
                    }
                    
                    if ( old_cell2 != expected_old_cell2 )
                    {
                        outStream.printf("old_cell2 != expected_old_cell2\n");
                    }
                    

                    if ( ! completed )
                    {
                        outStream.printf(
                                "replaceCell() test 2 failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("replaceCell() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* finally, test sortCells */
        if ( failures == 0 )
        {
            String testString0 = null;
            String testString1 = null;
            String testString2 = null;
            String testString3 = null;
            String testString4 = null;
            String testString5 = null;
            String testString6 = null;
            String testString7 = null;
            String expectedString0 =
                    "((1, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString1 = 
                    "((1, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString2 = 
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:15:000, 00:00:16:000, (7.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)))";
            String expectedString3 = 
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString4 = 
                    "((1, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString5 = 
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)))";
            String expectedString6 = 
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(4, 00:00:15:000, 00:00:16:000, (7.0)), " +
                     "(5, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(6, 00:00:09:000, 00:00:10:000, (4.0)))";
            String expectedString7 = 
                    "((1, 00:00:03:000, 00:00:04:000, (1.0)), " +
                     "(2, 00:00:05:000, 00:00:06:000, (2.0)), " +
                     "(3, 00:00:07:000, 00:00:08:000, (3.0)), " +
                     "(4, 00:00:09:000, 00:00:10:000, (4.0)), " +
                     "(5, 00:00:15:000, 00:00:16:000, (8.0)), " +
                     "(6, 00:00:15:000, 00:00:16:000, (7.0)))";
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try 
            {
                /* do a sort on an empty column to make sure there are 
                 * no problems.
                 */
                f_col0.sortCells();
                
                f_col0.appendCell(f_cell7_c = new DataCell(f_cell7));
                
                testString0 = f_col0.itsCellsToString();
                
                f_col0.sortCells();

                testString1 = f_col0.itsCellsToString();
                
                f_col0.insertCell(f_cell1_c = new DataCell(f_cell1), 1);
                f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));

                testString2 = f_col0.itsCellsToString();
                
                f_col0.sortCells();
                
                testString3 = f_col0.itsCellsToString();
                
                f_col0.insertCell(f_cell8_c = new DataCell(f_cell8), 1);
                
                testString4 = f_col0.itsCellsToString();
                
                f_col0.sortCells();
                
                testString5 = f_col0.itsCellsToString();
                
                f_col0.appendCell(f_cell3 = new DataCell(f_cell3));
                f_col0.appendCell(f_cell4 = new DataCell(f_cell4));

                testString6 = f_col0.itsCellsToString();
                
                f_col0.sortCells();
                
                testString7 = f_col0.itsCellsToString();
                
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0.numCells != 6 ) ||
                 ( expectedString0.compareTo(testString0) != 0 ) ||
                 ( expectedString1.compareTo(testString1) != 0 ) ||
                 ( expectedString2.compareTo(testString2) != 0 ) ||
                 ( expectedString3.compareTo(testString3) != 0 ) ||
                 ( expectedString4.compareTo(testString4) != 0 ) ||
                 ( expectedString5.compareTo(testString5) != 0 ) ||
                 ( expectedString6.compareTo(testString6) != 0 ) ||
                 ( expectedString7.compareTo(testString7) != 0 ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0.numCells != 6 )
                    {
                        outStream.printf("f_col0 = %d (6 expected).\n", 
                                         f_col0.numCells);
                    }
                    
                    if ( expectedString0.compareTo(testString0) != 0 )
                    {
                        outStream.printf("Unexpected testString0: \"%s\"\n", 
                            testString0);
                    }
                    
                    if ( expectedString1.compareTo(testString1) != 0 )
                    {
                        outStream.printf("Unexpected testString1: \"%s\"\n", 
                            testString1);
                    }
                    
                    if ( expectedString2.compareTo(testString2) != 0 )
                    {
                        outStream.printf("Unexpected testString2: \"%s\"\n", 
                            testString2);
                    }
                    
                    if ( expectedString3.compareTo(testString3) != 0 )
                    {
                        outStream.printf("Unexpected testString3: \"%s\"\n", 
                            testString3);
                    }
                    
                    if ( expectedString4.compareTo(testString4) != 0 )
                    {
                        outStream.printf("Unexpected testString4: \"%s\"\n", 
                            testString4);
                    }
                    
                    if ( expectedString5.compareTo(testString5) != 0 )
                    {
                        outStream.printf("Unexpected testString5: \"%s\"\n", 
                            testString5);
                    }
                    
                    if ( expectedString6.compareTo(testString6) != 0 )
                    {
                        outStream.printf("Unexpected testString6: \"%s\"\n", 
                            testString6);
                    }
                    
                    if ( expectedString7.compareTo(testString7) != 0 )
                    {
                        outStream.printf("Unexpected testString7: \"%s\"\n", 
                            testString7);
                    }                    

                    if ( ! completed )
                    {
                        outStream.printf(
                                "sortCells() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("sortCells() test 2 threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        

        
        /* Now verify that the cell management methods fail on invalid input */

        /* verify appendCell fails on null */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(null);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("appendCell(null) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendCell(null) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify appendCell fails when fed a cell configured 
         * for another column.
         *
         * One could argue that I should run this test on all cell type /
         * coplumn type pairs.  However, this would be overkill, as while
         * the validCell() method does check these issues, it does so only
         * after verifying the cells itsColID and itsMveID fields match those 
         * of the target column.  
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.appendCell(i_cell0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("appendCell(i_cell0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendCell(i_cell0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on null */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(null, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(null, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(null, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a cell configured for a different column */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(i_cell0, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(i_cell0, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(i_cell0, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.insertCell(f_cell3, 0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(f_cell3, 0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertCell(f_cell3, 0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify insertCell fails on a ord  that is larger than the number 
         * of cells in column plus 1.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 2;
                f_col0.insertCell(f_cell3, bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("insertCell(f_cell3, bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "insertCell(f_cell3, bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.getCell(0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("getCell(0) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCell(0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCell(0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCell fails on a ord greater than the number of 
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.getCell(bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "getCell(bogus_ord) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCell(bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCell(bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCellCopy fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.getCellCopy(0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("getCellCopy(0) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCellCopy(0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCellCopy(0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that getCellCopy fails on a ord greater than the number of 
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.getCellCopy(bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "getCellCopy(bogus_ord) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("getCellCopy(bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("getCellCopy(bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                testCell = f_col0.removeCell(0, f_cell0.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf("removeCell(0, f_cell0.getID()) " +
                                "returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "removeCell(0, f_cell0.getID()) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "removeCell(0, f_cell0.getID()) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a ord greater than the number of 
         * cells in the column.
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                testCell = f_col0.removeCell(bogus_ord, f_cell0.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "removeCell(bogus_ord, f_cell0.getID()) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that removeCell fails on a bogus cell ID.
         */
        if ( failures == 0 )
        {
            long bogusID;
            DataCell targetCell = null;
            DataCell testCell = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                targetCell = f_col0.getCell(1);
                bogusID = targetCell.getID() + 1;
                testCell = f_col0.removeCell(1, bogusID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( testCell != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( testCell != null )
                    {
                        outStream.printf(
                                "removeCell(1, bogusID) returned non null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("removeCell(1, bogusID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("removeCell(1, bogusID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on a non-positive ord */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(f_cell6, 0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("replaceCell(f_cell6, 0) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(f_cell6, 0) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on greater than the number of 
         * cells in the column. 
         */
        if ( failures == 0 )
        {
            int bogus_ord;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                bogus_ord = f_col0.getNumCells() + 1;
                f_col0.replaceCell(f_cell6, bogus_ord);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf(
                                "replaceCell(f_cell6, bogus_ord) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "replaceCell(f_cell6, bogus_ord) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on null new cell. 
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(null, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf("replaceCell(null, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(null, 1) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify replaceCell fails on col/cell mismatch. 
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0.replaceCell(i_cell0, 1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {

                    if ( completed )
                    {
                        outStream.printf(
                                "replaceCell(i_cell0, 1) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("replaceCell(i_cell0, 1) failed to " +
                                "throw a system error exception.\n");
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
        
    } /* DataCell::TestCellManagement() */

    
    /**
     * TestCopyConstructor()
     * 
     * Run a battery of tests on the copy constructor for this 
     * class, and on the instances returned.
     * 
     *                                              JRM -- 12/29/07
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
            "Testing copy argument constructor for class DataColumn           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long f_mve1ID = DBIndex.INVALID_ID;
        long f_mve2ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long i_mve1ID = DBIndex.INVALID_ID;
        long i_mve2ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long m_mve2ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long n_mve1ID = DBIndex.INVALID_ID;
        long n_mve2ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long p_mve1ID = DBIndex.INVALID_ID;
        long p_mve2ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long t_mve1ID = DBIndex.INVALID_ID;
        long t_mve2ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        MatrixVocabElement f_mve1 = null;
        MatrixVocabElement f_mve2 = null;
        MatrixVocabElement i_mve1 = null;
        MatrixVocabElement i_mve2 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement m_mve2 = null;
        MatrixVocabElement n_mve1 = null;
        MatrixVocabElement n_mve2 = null;
        MatrixVocabElement p_mve1 = null;
        MatrixVocabElement p_mve2 = null;
        MatrixVocabElement t_mve1 = null;
        MatrixVocabElement t_mve2 = null;
        DataColumn f_col0 = null;
        DataColumn f_col1 = null;
        DataColumn f_col2 = null;
        DataColumn i_col0 = null;
        DataColumn i_col1 = null;
        DataColumn i_col2 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn m_col2 = null;
        DataColumn n_col0 = null;
        DataColumn n_col1 = null;
        DataColumn n_col2 = null;
        DataColumn p_col0 = null;
        DataColumn p_col1 = null;
        DataColumn p_col2 = null;
        DataColumn t_col0 = null;
        DataColumn t_col1 = null;
        DataColumn t_col2 = null;
        DataColumn f_col0_copy = null;
        DataColumn f_col1_copy = null;
        DataColumn f_col2_copy = null;
        DataColumn i_col0_copy = null;
        DataColumn i_col1_copy = null;
        DataColumn i_col2_copy = null;
        DataColumn m_col0_copy = null;
        DataColumn m_col1_copy = null;
        DataColumn m_col2_copy = null;
        DataColumn n_col0_copy = null;
        DataColumn n_col1_copy = null;
        DataColumn n_col2_copy = null;
        DataColumn p_col0_copy = null;
        DataColumn p_col1_copy = null;
        DataColumn p_col2_copy = null;
        DataColumn t_col0_copy = null;
        DataColumn t_col1_copy = null;
        DataColumn t_col2_copy = null;
        DataColumn dc = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First allocate a selection of data columns to test the 
         * the copy constructor on.  Note that in no case do we create
         * a set of cells for the base columns as the cells vector should 
         * not be copied.  We will test this at the Database level, where
         * we will be constructing sets of cells for the columns already.
         */
        try
        {
            db = new ODBCDatabase();
            

            f_col0 = new DataColumn(db, "f_col0", 
                                    MatrixVocabElement.matrixType.FLOAT);
           
            f_mve1 = new MatrixVocabElement(db, "f_col1");
            f_mve1.setType(MatrixVocabElement.matrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve1.appendFormalArg(farg);
            db.vl.addElement(f_mve1);
            f_mve1ID = f_mve1.getID();
            f_col1 = new DataColumn(db, "f_col1", true, false, f_mve1ID);
            
            f_mve2 = new MatrixVocabElement(db, "f_col2");
            f_mve2.setType(MatrixVocabElement.matrixType.FLOAT);
            farg = new FloatFormalArg(db);
            f_mve2.appendFormalArg(farg);
            db.vl.addElement(f_mve2);
            f_mve2ID = f_mve2.getID();
            f_col2 = new DataColumn(db, "f_col2", false, true, f_mve2ID);
            
            
            i_col0 = new DataColumn(db, "i_col0", 
                                    MatrixVocabElement.matrixType.INTEGER);
            
            i_mve1 = new MatrixVocabElement(db, "i_col1");
            i_mve1.setType(MatrixVocabElement.matrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve1.appendFormalArg(farg);
            db.vl.addElement(i_mve1);
            i_mve1ID = i_mve1.getID();
            i_col1 = new DataColumn(db, "i_col1", true, false, i_mve1ID);
            
            i_mve2 = new MatrixVocabElement(db, "i_col2");
            i_mve2.setType(MatrixVocabElement.matrixType.INTEGER);
            farg = new IntFormalArg(db);
            i_mve2.appendFormalArg(farg);
            db.vl.addElement(i_mve2);
            i_mve2ID = i_mve2.getID();
            i_col2 = new DataColumn(db, "i_col2", false, true, i_mve2ID);
            
            
            m_col0 = new DataColumn(db, "m_col0", 
                                    MatrixVocabElement.matrixType.MATRIX);
            
            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);
            
            m_mve2 = new MatrixVocabElement(db, "m_col2");
            m_mve2.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve2.appendFormalArg(farg);
            db.vl.addElement(m_mve2);
            m_mve2ID = m_mve2.getID();
            m_col2 = new DataColumn(db, "m_col2", false, true, m_mve2ID);
            
            
            n_col0 = new DataColumn(db, "n_col0", 
                                    MatrixVocabElement.matrixType.NOMINAL);
            
            n_mve1 = new MatrixVocabElement(db, "n_col1");
            n_mve1.setType(MatrixVocabElement.matrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve1.appendFormalArg(farg);
            db.vl.addElement(n_mve1);
            n_mve1ID = n_mve1.getID();
            n_col1 = new DataColumn(db, "n_col1", true, false, n_mve1ID);
            
            n_mve2 = new MatrixVocabElement(db, "n_col2");
            n_mve2.setType(MatrixVocabElement.matrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            n_mve2.appendFormalArg(farg);
            db.vl.addElement(n_mve2);
            n_mve2ID = n_mve2.getID();
            n_col2 = new DataColumn(db, "n_col2", false, true, n_mve2ID);
            
            
            p_col0 = new DataColumn(db, "p_col0", 
                                    MatrixVocabElement.matrixType.PREDICATE);
            
            p_mve1 = new MatrixVocabElement(db, "p_col1");
            p_mve1.setType(MatrixVocabElement.matrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve1.appendFormalArg(farg);
            db.vl.addElement(p_mve1);
            p_mve1ID = p_mve1.getID();
            p_col1 = new DataColumn(db, "p_col1", true, false, p_mve1ID);
            
            p_mve2 = new MatrixVocabElement(db, "p_col2");
            p_mve2.setType(MatrixVocabElement.matrixType.PREDICATE);
            farg = new PredFormalArg(db);
            p_mve2.appendFormalArg(farg);
            db.vl.addElement(p_mve2);
            p_mve2ID = p_mve2.getID();
            p_col2 = new DataColumn(db, "p_col2", false, true, p_mve2ID);
            
            
            t_col0 = new DataColumn(db, "t_col0", 
                                    MatrixVocabElement.matrixType.TEXT);
            
            t_mve1 = new MatrixVocabElement(db, "t_col1");
            t_mve1.setType(MatrixVocabElement.matrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve1.appendFormalArg(farg);
            db.vl.addElement(t_mve1);
            t_mve1ID = t_mve1.getID();
            t_col1 = new DataColumn(db, "t_col1", true, false, t_mve1ID);
            
            t_mve2 = new MatrixVocabElement(db, "t_col2");
            t_mve2.setType(MatrixVocabElement.matrixType.TEXT);
            farg = new TextStringFormalArg(db);
            t_mve2.appendFormalArg(farg);
            db.vl.addElement(t_mve2);
            t_mve2ID = t_mve2.getID();
            t_col2 = new DataColumn(db, "t_col2", false, true, t_mve2ID);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_col0 == null ) ||
             ( f_mve1 == null ) ||
             ( f_mve1ID == DBIndex.INVALID_ID ) ||
             ( f_col1 == null ) ||
             ( f_mve2 == null ) ||
             ( f_mve2ID == DBIndex.INVALID_ID ) ||
             ( f_col2 == null ) ||
             ( f_mve2ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve1 == null ) ||
             ( i_mve1ID == DBIndex.INVALID_ID ) ||
             ( i_col1 == null ) ||
             ( i_mve2 == null ) ||
             ( i_mve2ID == DBIndex.INVALID_ID ) ||
             ( i_col2 == null ) ||
             ( m_col0 == null ) ||
             ( m_mve1 == null ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( m_mve2 == null ) ||
             ( m_mve2ID == DBIndex.INVALID_ID ) ||
             ( m_col2 == null ) ||
             ( n_col0 == null ) ||
             ( n_mve1 == null ) ||
             ( n_mve1ID == DBIndex.INVALID_ID ) ||
             ( n_col1 == null ) ||
             ( n_mve2 == null ) ||
             ( n_mve2ID == DBIndex.INVALID_ID ) ||
             ( n_col2 == null ) ||
             ( p_col0 == null ) ||
             ( p_mve1 == null ) ||
             ( p_mve1ID == DBIndex.INVALID_ID ) ||
             ( p_col1 == null ) ||
             ( p_mve2 == null ) ||
             ( p_mve2ID == DBIndex.INVALID_ID ) ||
             ( p_col2 == null ) ||
             ( t_col0 == null ) ||
             ( t_mve1 == null ) ||
             ( t_mve1ID == DBIndex.INVALID_ID ) ||
             ( t_col1 == null ) ||
             ( t_mve2 == null ) ||
             ( t_mve2ID == DBIndex.INVALID_ID ) ||
             ( t_col2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( f_col0 == null )
                {
                    outStream.print("f_col0 allocation failed.\n");
                }
                
                if ( ( f_mve1 == null ) ||
                     ( f_mve1ID == DBIndex.INVALID_ID ) ||
                     ( f_col1 == null ) )
                {
                    outStream.print("f_col1 allocation failed.\n");
                }
                
                if ( ( f_mve2 == null ) ||
                     ( f_mve2ID == DBIndex.INVALID_ID ) ||
                     ( f_col2 == null ) )
                {
                    outStream.print("f_col2 allocation failed.\n");
                }
                
                
                if ( i_col0 == null ) 
                {
                    outStream.print("i_col0 allocation failed.\n");
                }
                
                if ( ( i_mve1 == null ) ||
                     ( i_mve1ID == DBIndex.INVALID_ID ) ||
                     ( i_col1 == null ) )
                {
                    outStream.print("i_col1 allocation failed.\n");
                }
                
                if ( ( i_mve2 == null ) ||
                     ( i_mve2ID == DBIndex.INVALID_ID ) ||
                     ( i_col2 == null ) )
                {
                    outStream.print("i_col2 allocation failed.\n");
                }
                
                
                if ( m_col0 == null )
                {
                    outStream.print("m_col0 allocation failed.\n");
                }
                
                if ( ( m_mve1 == null ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) ) 
                {
                    outStream.print("m_col1 allocation failed.\n");
                }
                
                if ( ( m_mve2 == null ) ||
                     ( m_mve2ID == DBIndex.INVALID_ID ) ||
                     ( m_col2 == null ) ) 
                {
                    outStream.print("m_col2 allocation failed.\n");
                }
                
                
                if ( n_col0 == null )
                {
                    outStream.print("n_col0 allocation failed.\n");
                }
                
                if ( ( n_mve1 == null ) ||
                     ( n_mve1ID == DBIndex.INVALID_ID ) ||
                     ( n_col1 == null ) )
                {
                    outStream.print("n_col1 allocation failed.\n");
                }
                
                if ( ( n_mve2 == null ) ||
                     ( n_mve2ID == DBIndex.INVALID_ID ) ||
                     ( n_col2 == null ) )
                {
                    outStream.print("n_col2 allocation failed.\n");
                }
                
                
                if ( p_col0 == null )
                {
                    outStream.print("p_col0 allocation failed.\n");
                }
                
                if ( ( p_mve1 == null ) ||
                     ( p_mve1ID == DBIndex.INVALID_ID ) ||
                     ( p_col1 == null ) )
                {
                    outStream.print("p_col1 allocation failed.\n");
                }
                
                if ( ( p_mve2 == null ) ||
                     ( p_mve2ID == DBIndex.INVALID_ID ) ||
                     ( p_col2 == null ) )
                {
                    outStream.print("p_col2 allocation failed.\n");
                }
                
                
                if ( t_col0 == null )
                {
                    outStream.print("t_col0 allocation failed.\n");
                }
                
                if ( ( t_mve1 == null ) ||
                     ( t_mve1ID == DBIndex.INVALID_ID ) ||
                     ( t_col1 == null ) )
                {
                    outStream.print("t_col1 allocation failed.\n");
                }
                
                if ( ( t_mve2 == null ) ||
                     ( t_mve2ID == DBIndex.INVALID_ID ) ||
                     ( t_col2 == null ) )
                {
                    outStream.print("t_col2 allocation failed.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_col0_copy = new DataColumn(f_col0);
                f_col1_copy = new DataColumn(f_col1);
                f_col2_copy = new DataColumn(f_col2);
            
                i_col0_copy = new DataColumn(i_col0);
                i_col1_copy = new DataColumn(i_col1);
                i_col2_copy = new DataColumn(i_col2);
            
                m_col0_copy = new DataColumn(m_col0);
                m_col1_copy = new DataColumn(m_col1);
                m_col2_copy = new DataColumn(m_col2);
            
                n_col0_copy = new DataColumn(n_col0);
                n_col1_copy = new DataColumn(n_col1);
                n_col2_copy = new DataColumn(n_col2);
            
                p_col0_copy = new DataColumn(p_col0);
                p_col1_copy = new DataColumn(p_col1);
                p_col2_copy = new DataColumn(p_col2);
           
                t_col0_copy = new DataColumn(t_col0);
                t_col1_copy = new DataColumn(t_col1);
                t_col2_copy = new DataColumn(t_col2);
            
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_col0_copy == null ) ||
                 ( f_col1_copy == null ) ||
                 ( f_col2_copy == null ) ||
                 ( i_col0_copy == null ) ||
                 ( i_col1_copy == null ) ||
                 ( i_col2_copy == null ) ||
                 ( m_col0_copy == null ) ||
                 ( m_col1_copy == null ) ||
                 ( m_col2_copy == null ) ||
                 ( n_col0_copy == null ) ||
                 ( n_col1_copy == null ) ||
                 ( n_col2_copy == null ) ||
                 ( p_col0_copy == null ) ||
                 ( p_col1_copy == null ) ||
                 ( p_col2_copy == null ) ||
                 ( t_col0_copy == null ) ||
                 ( t_col1_copy == null ) ||
                 ( t_col2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_col0_copy == null )
                    {
                        outStream.print("f_col0_copy allocation failed.\n");
                    }

                    if ( f_col1_copy == null )
                    {
                        outStream.print("f_col1_copy allocation failed.\n");
                    }

                    if ( f_col1_copy == null )
                    {
                        outStream.print("f_col2_copy allocation failed.\n");
                    }

                    if ( i_col0_copy == null )
                    {
                        outStream.print("i_col0_copy allocation failed.\n");
                    }

                    if ( i_col1_copy == null )
                    {
                        outStream.print("i_col1_copy allocation failed.\n");
                    }

                    if ( i_col1_copy == null )
                    {
                        outStream.print("i_col2_copy allocation failed.\n");
                    }

                    if ( m_col0_copy == null )
                    {
                        outStream.print("m_col0_copy allocation failed.\n");
                    }

                    if ( m_col1_copy == null )
                    {
                        outStream.print("m_col1_copy allocation failed.\n");
                    }

                    if ( m_col1_copy == null )
                    {
                        outStream.print("m_col2_copy allocation failed.\n");
                    }

                    if ( n_col0_copy == null )
                    {
                        outStream.print("n_col0_copy allocation failed.\n");
                    }

                    if ( n_col1_copy == null )
                    {
                        outStream.print("n_col1_copy allocation failed.\n");
                    }

                    if ( n_col1_copy == null )
                    {
                        outStream.print("n_col2_copy allocation failed.\n");
                    }

                    if ( p_col0_copy == null )
                    {
                        outStream.print("p_col0_copy allocation failed.\n");
                    }

                    if ( p_col1_copy == null )
                    {
                        outStream.print("p_col1_copy allocation failed.\n");
                    }

                    if ( p_col1_copy == null )
                    {
                        outStream.print("p_col2_copy allocation failed.\n");
                    }

                    if ( t_col0_copy == null )
                    {
                        outStream.print("t_col0_copy allocation failed.\n");
                    }

                    if ( t_col1_copy == null )
                    {
                        outStream.print("t_col1_copy allocation failed.\n");
                    }

                    if ( t_col1_copy == null )
                    {
                        outStream.print("t_col2_copy allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "copy constructor test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("copy constructor test threw a " +
                                         "system error exception: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyDataColumnCopy(f_col0, f_col0_copy, 
                        outStream, verbose, "f_col0", "f_col0_copy");
                
                failures += VerifyDataColumnCopy(f_col1, f_col1_copy, 
                        outStream, verbose, "f_col1", "f_col1_copy");
                
                failures += VerifyDataColumnCopy(f_col2, f_col2_copy, 
                        outStream, verbose, "f_col2", "f_col2_copy");

                
                failures += VerifyDataColumnCopy(i_col0, i_col0_copy, 
                        outStream, verbose, "i_col0", "i_col0_copy");
                
                failures += VerifyDataColumnCopy(i_col1, i_col1_copy, 
                        outStream, verbose, "i_col1", "i_col1_copy");
                
                failures += VerifyDataColumnCopy(f_col2, f_col2_copy, 
                        outStream, verbose, "i_col2", "i_col2_copy");

                
                failures += VerifyDataColumnCopy(m_col0, m_col0_copy, 
                        outStream, verbose, "m_col0", "m_col0_copy");
                
                failures += VerifyDataColumnCopy(m_col1, m_col1_copy, 
                        outStream, verbose, "m_col1", "m_col1_copy");
                
                failures += VerifyDataColumnCopy(m_col2, m_col2_copy, 
                        outStream, verbose, "m_col2", "m_col2_copy");

                
                failures += VerifyDataColumnCopy(n_col0, n_col0_copy, 
                        outStream, verbose, "n_col0", "n_col0_copy");
                
                failures += VerifyDataColumnCopy(n_col1, n_col1_copy, 
                        outStream, verbose, "n_col1", "n_col1_copy");
                
                failures += VerifyDataColumnCopy(n_col2, n_col2_copy, 
                        outStream, verbose, "n_col2", "n_col2_copy");

                
                failures += VerifyDataColumnCopy(p_col0, p_col0_copy, 
                        outStream, verbose, "p_col0", "p_col0_copy");
                
                failures += VerifyDataColumnCopy(p_col1, p_col1_copy, 
                        outStream, verbose, "p_col1", "p_col1_copy");
                
                failures += VerifyDataColumnCopy(p_col2, p_col2_copy, 
                        outStream, verbose, "p_col2", "p_col2_copy");

                
                failures += VerifyDataColumnCopy(t_col0, t_col0_copy, 
                        outStream, verbose, "t_col0", "t_col0_copy");
                
                failures += VerifyDataColumnCopy(t_col1, t_col1_copy, 
                        outStream, verbose, "t_col1", "t_col1_copy");
                
                failures += VerifyDataColumnCopy(t_col2, t_col2_copy, 
                        outStream, verbose, "t_col2", "t_col2_copy");
            }
        }
        
        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on null */
        if ( failures == 0 )
        {
            dc = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                dc= new DataColumn(null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( dc != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( dc != null )
                    {
                        outStream.printf(
                                "new DataColumn(null) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataColumn(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataColumn(null) failed to " +
                                "throw a system error exception.\n");
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
        
    } /* DataCell::TestCopyConstructor() */

    
    /**
     * TestToStringMethods()
     * 
     * Run a battery of tests on the toString methods of the class. 
     *
     *                                              JRM -- 12/31/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_mve0ID = DBIndex.INVALID_ID;
        long i_mve0ID = DBIndex.INVALID_ID;
        long m_mve0ID = DBIndex.INVALID_ID;
        long m_mve1ID = DBIndex.INVALID_ID;
        long n_mve0ID = DBIndex.INVALID_ID;
        long p_mve0ID = DBIndex.INVALID_ID;
        long t_mve0ID = DBIndex.INVALID_ID;
        long f_col0ID = DBIndex.INVALID_ID;
        long i_col0ID = DBIndex.INVALID_ID;
        long m_col0ID = DBIndex.INVALID_ID;
        long m_col1ID = DBIndex.INVALID_ID;
        long n_col0ID = DBIndex.INVALID_ID;
        long p_col0ID = DBIndex.INVALID_ID;
        long t_col0ID = DBIndex.INVALID_ID;
        long fargID;
        MatrixVocabElement f_mve0 = null;
        MatrixVocabElement i_mve0 = null;
        MatrixVocabElement m_mve0 = null;
        MatrixVocabElement m_mve1 = null;
        MatrixVocabElement n_mve0 = null;
        MatrixVocabElement p_mve0 = null;
        MatrixVocabElement t_mve0 = null;
        DataColumn f_col0 = null;
        DataColumn i_col0 = null;
        DataColumn m_col0 = null;
        DataColumn m_col1 = null;
        DataColumn n_col0 = null;
        DataColumn p_col0 = null;
        DataColumn t_col0 = null;
        DataColumn dc = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell3 = null;
        DataCell f_cell4 = null;
        DataCell f_cell5 = null;
        DataCell f_cell6 = null;
        DataCell f_cell7 = null;
        DataCell f_cell8 = null;
        DataCell i_cell0 = null;
        DataCell m_cell0 = null;
        DataCell n_cell0 = null;
        DataCell p_cell0 = null;
        DataCell t_cell0 = null;
        DataCell f_cell0_c = null;
        DataCell f_cell1_c = null;
        DataCell f_cell2_c = null;
        DataCell f_cell3_c = null;
        DataCell f_cell4_c = null;
        DataCell f_cell5_c = null;
        DataCell f_cell6_c = null;
        DataCell f_cell7_c = null;
        DataCell f_cell8_c = null;
        DataCell i_cell0_c = null;
        DataCell m_cell0_c = null;
        DataCell n_cell0_c = null;
        DataCell p_cell0_c = null;
        DataCell t_cell0_c = null;
        TimeStamp f_onset0 = null;
        TimeStamp f_onset1 = null;
        TimeStamp f_onset2 = null;
        TimeStamp f_onset3 = null;
        TimeStamp f_onset4 = null;
        TimeStamp f_onset5 = null;
        TimeStamp f_onset6 = null;
        TimeStamp f_onset7 = null;
        TimeStamp f_onset8 = null;
        TimeStamp f_offset0 = null;
        TimeStamp f_offset1 = null;
        TimeStamp f_offset2 = null;
        TimeStamp f_offset3 = null;
        TimeStamp f_offset4 = null;
        TimeStamp f_offset5 = null;
        TimeStamp f_offset6 = null;
        TimeStamp f_offset7 = null;
        TimeStamp f_offset8 = null;
        Vector<DataValue> f_arg_list0 = null;
        Vector<DataValue> f_arg_list1 = null;
        Vector<DataValue> f_arg_list2 = null;
        Vector<DataValue> f_arg_list3 = null;
        Vector<DataValue> f_arg_list4 = null;
        Vector<DataValue> f_arg_list5 = null;
        Vector<DataValue> f_arg_list6 = null;
        Vector<DataValue> f_arg_list7 = null;
        Vector<DataValue> f_arg_list8 = null;
        Matrix f_matrix0 = null;
        Matrix f_matrix1 = null;
        Matrix f_matrix2 = null;
        Matrix f_matrix3 = null;
        Matrix f_matrix4 = null;
        Matrix f_matrix5 = null;
        Matrix f_matrix6 = null;
        Matrix f_matrix7 = null;
        Matrix f_matrix8 = null;
        FormalArgument farg = null;
        DataValue arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* First, allocate a selection of columns and cells to work with. After
         * we insert the columns in the database, we use db.cl.getColumn()
         * to get a reference to the actual column in the database.
         */
        try
        {
            db = new ODBCDatabase();
            

            f_col0 = new DataColumn(db, "f_col0", 
                                    MatrixVocabElement.matrixType.FLOAT);
            f_col0ID = db.addColumn(f_col0);
            f_col0 = (DataColumn)db.cl.getColumn(f_col0ID);
            f_mve0ID = f_col0.getItsMveID();
            f_mve0 = db.getMatrixVE(f_mve0ID);
           
            
            i_col0 = new DataColumn(db, "i_col0", 
                                    MatrixVocabElement.matrixType.INTEGER);
            i_col0ID = db.addColumn(i_col0);
            i_col0 = (DataColumn)db.cl.getColumn(i_col0ID);
            i_mve0ID = i_col0.getItsMveID();
            i_mve0 = db.getMatrixVE(i_mve0ID);
            
            
            m_col0 = new DataColumn(db, "m_col0", 
                                    MatrixVocabElement.matrixType.MATRIX);
            m_col0ID = db.addColumn(m_col0);
            m_col0 = (DataColumn)db.cl.getColumn(m_col0ID);
            m_mve0ID = m_col0.getItsMveID();
            m_mve0 = db.getMatrixVE(m_mve0ID);
            
            
            m_mve1 = new MatrixVocabElement(db, "m_col1");
            m_mve1.setType(MatrixVocabElement.matrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg>");
            m_mve1.appendFormalArg(farg);
            m_mve1.setVarLen(true);
            db.vl.addElement(m_mve1);
            m_mve1ID = m_mve1.getID();
            m_col1 = new DataColumn(db, "m_col1", true, false, m_mve1ID);
            db.cl.addColumn(m_col1);
            m_col1ID = m_col1.getID();

            
            n_col0 = new DataColumn(db, "n_col0", 
                                    MatrixVocabElement.matrixType.NOMINAL);
            n_col0ID = db.addColumn(n_col0);
            n_col0 = (DataColumn)db.cl.getColumn(n_col0ID);
            n_mve0ID = n_col0.getItsMveID();
            n_mve0 = db.getMatrixVE(n_mve0ID);
            
            
            p_col0 = new DataColumn(db, "p_col0", 
                                    MatrixVocabElement.matrixType.PREDICATE);
            p_col0ID = db.addColumn(p_col0);
            p_col0 = (DataColumn)db.cl.getColumn(p_col0ID);
            p_mve0ID = p_col0.getItsMveID();
            p_mve0 = db.getMatrixVE(p_mve0ID);
            
            
            t_col0 = new DataColumn(db, "t_col0", 
                                    MatrixVocabElement.matrixType.TEXT);
            t_col0ID = db.addColumn(t_col0);
            t_col0 = (DataColumn)db.cl.getColumn(t_col0ID);
            t_mve0ID = t_col0.getItsMveID();
            t_mve0 = db.getMatrixVE(t_mve0ID);
            
            
            f_onset0 = new TimeStamp(db.getTicks(), 60);
            f_offset0 = new TimeStamp(db.getTicks(), 120);
            f_arg_list0 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 0.0);
            f_arg_list0.add(arg);
            f_matrix0 = new Matrix(db, f_mve0ID, f_arg_list0);
            f_cell0 = new DataCell(db, "f_cell0", f_col0ID, f_mve0ID, 
                                       f_onset0, f_offset0, f_matrix0);

            f_onset1 = new TimeStamp(db.getTicks(), 180);
            f_offset1 = new TimeStamp(db.getTicks(), 240);
            f_arg_list1 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            f_arg_list1.add(arg);
            f_matrix1 = new Matrix(db, f_mve0ID, f_arg_list1);
            f_cell1 = new DataCell(db, "f_cell1", f_col0ID, f_mve0ID, 
                                       f_onset1, f_offset1, f_matrix1);

            f_onset2 = new TimeStamp(db.getTicks(), 300);
            f_offset2 = new TimeStamp(db.getTicks(), 360);
            f_arg_list2 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 2.0);
            f_arg_list2.add(arg);
            f_matrix2 = new Matrix(db, f_mve0ID, f_arg_list2);
            f_cell2 = new DataCell(db, "f_cell2", f_col0ID, f_mve0ID, 
                                       f_onset2, f_offset2, f_matrix2);

            f_onset3 = new TimeStamp(db.getTicks(), 420);
            f_offset3 = new TimeStamp(db.getTicks(), 480);
            f_arg_list3 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 3.0);
            f_arg_list3.add(arg);
            f_matrix3 = new Matrix(db, f_mve0ID, f_arg_list3);
            f_cell3 = new DataCell(db, "f_cell3", f_col0ID, f_mve0ID, 
                                       f_onset3, f_offset3, f_matrix3);

            f_onset4 = new TimeStamp(db.getTicks(), 540);
            f_offset4 = new TimeStamp(db.getTicks(), 600);
            f_arg_list4 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 4.0);
            f_arg_list4.add(arg);
            f_matrix4 = new Matrix(db, f_mve0ID, f_arg_list4);
            f_cell4 = new DataCell(db, "f_cell4", f_col0ID, f_mve0ID, 
                                       f_onset4, f_offset4, f_matrix4);

            f_onset5 = new TimeStamp(db.getTicks(), 660);
            f_offset5 = new TimeStamp(db.getTicks(), 720);
            f_arg_list5 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 5.0);
            f_arg_list5.add(arg);
            f_matrix5 = new Matrix(db, f_mve0ID, f_arg_list5);
            f_cell5 = new DataCell(db, "f_cell5", f_col0ID, f_mve0ID, 
                                       f_onset5, f_offset5, f_matrix5);

            f_onset6 = new TimeStamp(db.getTicks(), 780);
            f_offset6 = new TimeStamp(db.getTicks(), 840);
            f_arg_list6 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 6.0);
            f_arg_list6.add(arg);
            f_matrix6 = new Matrix(db, f_mve0ID, f_arg_list6);
            f_cell6 = new DataCell(db, "f_cell6", f_col0ID, f_mve0ID, 
                                       f_onset6, f_offset6, f_matrix6);

            f_onset7 = new TimeStamp(db.getTicks(), 900);
            f_offset7 = new TimeStamp(db.getTicks(), 960);
            f_arg_list7 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 7.0);
            f_arg_list7.add(arg);
            f_matrix7 = new Matrix(db, f_mve0ID, f_arg_list7);
            f_cell7 = new DataCell(db, "f_cell7", f_col0ID, f_mve0ID, 
                                       f_onset7, f_offset7, f_matrix7);

            f_onset8 = new TimeStamp(db.getTicks(), 900);
            f_offset8 = new TimeStamp(db.getTicks(), 960);
            f_arg_list8 = new Vector<DataValue>();
            fargID = f_mve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 8.0);
            f_arg_list8.add(arg);
            f_matrix8 = new Matrix(db, f_mve0ID, f_arg_list8);
            f_cell8 = new DataCell(db, "f_cell8", f_col0ID, f_mve0ID, 
                                       f_onset8, f_offset8, f_matrix8);

            i_cell0 = new DataCell(db, "i_cell0", i_col0ID, i_mve0ID);
            m_cell0 = new DataCell(db, "m_cell0", m_col0ID, m_mve0ID);
            n_cell0 = new DataCell(db, "n_cell0", n_col0ID, n_mve0ID);
            p_cell0 = new DataCell(db, "p_cell0", p_col0ID, p_mve0ID);
            t_cell0 = new DataCell(db, "t_cell0", t_col0ID, t_mve0ID);
            
            f_col0.appendCell(f_cell0_c = new DataCell(f_cell0));
            f_col0.appendCell(f_cell1_c = new DataCell(f_cell1));
            f_col0.appendCell(f_cell2_c = new DataCell(f_cell2));
            f_col0.appendCell(f_cell3_c = new DataCell(f_cell3));
            f_col0.appendCell(f_cell4_c = new DataCell(f_cell4));
            f_col0.appendCell(f_cell5_c = new DataCell(f_cell5));
            f_col0.appendCell(f_cell6_c = new DataCell(f_cell6));
            f_col0.appendCell(f_cell7_c = new DataCell(f_cell7));
            f_col0.appendCell(f_cell8_c = new DataCell(f_cell8));
            
            i_col0.appendCell(i_cell0_c = new DataCell(i_cell0));
            m_col0.appendCell(m_cell0_c = new DataCell(m_cell0));
            n_col0.appendCell(n_cell0_c = new DataCell(n_cell0));
            p_col0.appendCell(p_cell0_c = new DataCell(p_cell0));
            t_col0.appendCell(t_cell0_c = new DataCell(t_cell0));

            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_col0ID == DBIndex.INVALID_ID ) ||
             ( f_mve0ID == DBIndex.INVALID_ID ) ||
             ( f_col0 == null ) ||
             ( f_mve0 == null ) ||
             ( i_col0ID == DBIndex.INVALID_ID ) ||
             ( i_mve0ID == DBIndex.INVALID_ID ) ||
             ( i_col0 == null ) ||
             ( i_mve0 == null ) ||
             ( m_col0ID == DBIndex.INVALID_ID ) ||
             ( m_mve0ID == DBIndex.INVALID_ID ) ||
             ( m_col0 == null ) ||
             ( m_mve0 == null ) ||
             ( m_col1ID == DBIndex.INVALID_ID ) ||
             ( m_mve1ID == DBIndex.INVALID_ID ) ||
             ( m_col1 == null ) ||
             ( m_mve1 == null ) ||
             ( n_col0ID == DBIndex.INVALID_ID ) ||
             ( n_mve0ID == DBIndex.INVALID_ID ) ||
             ( n_col0 == null ) ||
             ( n_mve0 == null ) ||
             ( p_col0ID == DBIndex.INVALID_ID ) ||
             ( p_mve0ID == DBIndex.INVALID_ID ) ||
             ( p_col0 == null ) ||
             ( p_mve0 == null ) ||
             ( t_col0ID == DBIndex.INVALID_ID ) ||
             ( t_mve0ID == DBIndex.INVALID_ID ) ||
             ( t_col0 == null ) ||
             ( t_mve0 == null ) ||
             ( f_onset0 == null ) ||
             ( f_offset0 == null ) ||
             ( f_cell0 == null ) ||
             ( f_onset1 == null ) ||
             ( f_offset1 == null ) ||
             ( f_cell1 == null ) ||
             ( f_onset2 == null ) ||
             ( f_offset2 == null ) ||
             ( f_cell2 == null ) ||
             ( f_onset3 == null ) ||
             ( f_offset3 == null ) ||
             ( f_cell3 == null ) ||
             ( f_onset4 == null ) ||
             ( f_offset4 == null ) ||
             ( f_cell4 == null ) ||
             ( f_onset5 == null ) ||
             ( f_offset5 == null ) ||
             ( f_cell5 == null ) ||
             ( f_onset6 == null ) ||
             ( f_offset6 == null ) ||
             ( f_cell6 == null ) ||
             ( f_onset7 == null ) ||
             ( f_offset7 == null ) ||
             ( f_cell7 == null ) ||
             ( f_onset8 == null ) ||
             ( f_offset8 == null ) ||
             ( f_cell8 == null ) ||
             ( i_cell0 == null ) ||
             ( m_cell0 == null ) ||
             ( n_cell0 == null ) ||
             ( p_cell0 == null ) ||
             ( t_cell0 == null ) ||
             ( f_col0.getNumCells() != 9 ) ||
             ( i_col0.getNumCells() != 1 ) ||
             ( m_col0.getNumCells() != 1 ) ||
             ( m_col1.getNumCells() != 0 ) ||
             ( n_col0.getNumCells() != 1 ) ||
             ( p_col0.getNumCells() != 1 ) ||
             ( t_col0.getNumCells() != 1 ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_col0ID == DBIndex.INVALID_ID ) ||
                     ( f_mve0ID == DBIndex.INVALID_ID ) ||
                     ( f_col0 == null ) ||
                     ( f_mve0 == null ) )
                {
                    outStream.printf("f_col0 alloc failed.  f_col0ID = %d, " + 
                            "f_mve0ID = %d\n", f_col0ID, f_mve0ID);
                }
                
                if ( ( i_col0ID == DBIndex.INVALID_ID ) ||
                     ( i_mve0ID == DBIndex.INVALID_ID ) ||
                     ( i_col0 == null ) ||
                     ( i_mve0 == null ) )
                {
                    outStream.printf("i_col0 alloc failed.  i_col0ID = %d, " + 
                            "i_mve0ID = %d\n", i_col0ID, i_mve0ID);
                }
                
                if ( ( m_col0ID == DBIndex.INVALID_ID ) ||
                     ( m_mve0ID == DBIndex.INVALID_ID ) ||
                     ( m_col0 == null ) ||
                     ( m_mve0 == null ) )
                {
                    outStream.printf("m_col0 alloc failed.  m_col0ID = %d, " + 
                            "m_mve0ID = %d\n", m_col0ID, m_mve0ID);
                }
                
                if ( ( m_col1ID == DBIndex.INVALID_ID ) ||
                     ( m_mve1ID == DBIndex.INVALID_ID ) ||
                     ( m_col1 == null ) ||
                     ( m_mve1 == null ) )
                {
                    outStream.printf("m_col1 alloc failed.  m_col1ID = %d, " + 
                            "m_mve1ID = %d\n", m_col1ID, m_mve1ID);
                }
                
                if ( ( n_col0ID == DBIndex.INVALID_ID ) ||
                     ( n_mve0ID == DBIndex.INVALID_ID ) ||
                     ( n_col0 == null ) ||
                     ( n_mve0 == null ) )
                {
                    outStream.printf("n_col0 alloc failed.  n_col0ID = %d, " + 
                            "n_mve0ID = %d\n", n_col0ID, n_mve0ID);
                }
                
                if ( ( p_col0ID == DBIndex.INVALID_ID ) ||
                     ( p_mve0ID == DBIndex.INVALID_ID ) ||
                     ( p_col0 == null ) ||
                     ( p_mve0 == null ) )
                {
                    outStream.printf("p_col0 alloc failed.  p_col0ID = %d, " + 
                            "p_mve0ID = %d\n", p_col0ID, p_mve0ID);
                }
                
                if ( ( t_col0ID == DBIndex.INVALID_ID ) ||
                     ( t_mve0ID == DBIndex.INVALID_ID ) ||
                     ( t_col0 == null ) ||
                     ( t_mve0 == null ) )
                {
                    outStream.printf("t_col0 alloc failed.  t_col0ID = %d, " + 
                            "t_mve0ID = %d\n", t_col0ID, t_mve0ID);
                }
                
                if ( ( f_onset0 == null ) ||
                     ( f_offset0 == null ) ||
                     ( f_cell0 == null ) )
                {
                    outStream.printf("f_cell0 alloc failed.\n");
                }
                
                if ( ( f_onset1 == null ) ||
                     ( f_offset1 == null ) ||
                     ( f_cell1 == null ) )
                {
                    outStream.printf("f_cell1 alloc failed.\n");
                }
                
                if ( ( f_onset2 == null ) ||
                     ( f_offset2 == null ) ||
                     ( f_cell2 == null ) )
                {
                    outStream.printf("f_cell2 alloc failed.\n");
                }
                
                if ( ( f_onset3 == null ) ||
                     ( f_offset3 == null ) ||
                     ( f_cell3 == null ) )
                {
                    outStream.printf("f_cell3 alloc failed.\n");
                }
                
                if ( ( f_onset4 == null ) ||
                     ( f_offset4 == null ) ||
                     ( f_cell4 == null ) )
                {
                    outStream.printf("f_cell4 alloc failed.\n");
                }
                
                if ( ( f_onset5 == null ) ||
                     ( f_offset5 == null ) ||
                     ( f_cell5 == null ) )
                {
                    outStream.printf("f_cell5 alloc failed.\n");
                }
                
                if ( ( f_onset6 == null ) ||
                     ( f_offset6 == null ) ||
                     ( f_cell6 == null ) )
                {
                    outStream.printf("f_cell6 alloc failed.\n");
                }
                
                if ( ( f_onset7 == null ) ||
                     ( f_offset7 == null ) ||
                     ( f_cell7 == null ) )
                {
                    outStream.printf("f_cell7 alloc failed.\n");
                }
                
                if ( ( f_onset8 == null ) ||
                     ( f_offset8 == null ) ||
                     ( f_cell8 == null ) )
                {
                    outStream.printf("f_cell8 alloc failed.\n");
                }
                
                if ( i_cell0 == null )
                {
                    outStream.printf("i_cell0 alloc failed.\n");
                }
                
                if ( m_cell0 == null )
                {
                    outStream.printf("m_cell0 alloc failed.\n");
                }
                
                if ( n_cell0 == null )
                {
                    outStream.printf("n_cell0 alloc failed.\n");
                }
                
                if ( p_cell0 == null )
                {
                    outStream.printf("p_cell0 alloc failed.\n");
                }
                
                if ( t_cell0 == null )
                {
                    outStream.printf("t_cell0 alloc failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }
                
                if ( f_col0.getNumCells() != 9 )
                {
                    outStream.printf(
                            "f_col0.getNumCells() = %d (9 expected).\n",
                            f_col0.getNumCells());
                }
                
                if ( i_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "i_col0.getNumCells() = %d (1 expected).\n",
                            i_col0.getNumCells());
                }
                
                if ( m_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "m_col0.getNumCells() = %d (1 expected).\n",
                            m_col0.getNumCells());
                }
                
                if ( m_col1.getNumCells() != 0 )
                {
                    outStream.printf(
                            "m_col1.getNumCells() = %d (0 expected).\n",
                            m_col1.getNumCells());
                }
                
                if ( n_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "n_col0.getNumCells() = %d (1 expected).\n",
                            n_col0.getNumCells());
                }
                
                if ( p_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "p_col0.getNumCells() = %d (1 expected).\n",
                            p_col0.getNumCells());
                }
                
                if ( t_col0.getNumCells() != 1 )
                {
                    outStream.printf(
                            "t_col0.getNumCells() = %d (1 expected).\n",
                            t_col0.getNumCells());
                }
                
                if ( ! completed )
                {
                    outStream.printf("test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        else if ( ( f_col0.itsCells == null ) ||
                  ( i_col0.itsCells == null ) ||
                  ( m_col0.itsCells == null ) ||
                  ( m_col1.itsCells == null ) ||
                  ( n_col0.itsCells == null ) ||
                  ( p_col0.itsCells == null ) ||
                  ( t_col0.itsCells == null ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                    "one or more column itsCells fields not initialized.\n");
            }
        }
        
        if ( failures == 0 )
        {
            String f_col0_string = null;
            String f_col0_DBstring = null;
            String i_col0_string = null;
            String i_col0_DBstring = null;
            String m_col0_string = null;
            String m_col0_DBstring = null;
            String m_col1_string = null;
            String m_col1_DBstring = null;
            String n_col0_string = null;
            String n_col0_DBstring = null;
            String p_col0_string = null;
            String p_col0_DBstring = null;
            String t_col0_string = null;
            String t_col0_DBstring = null;
            String expected_f_col0_string =
                "(f_col0, ((1, 00:00:01:000, 00:00:02:000, (0.0)), " +
                          "(2, 00:00:03:000, 00:00:04:000, (1.0)), " +
                          "(3, 00:00:05:000, 00:00:06:000, (2.0)), " +
                          "(4, 00:00:07:000, 00:00:08:000, (3.0)), " +
                          "(5, 00:00:09:000, 00:00:10:000, (4.0)), " +
                          "(6, 00:00:11:000, 00:00:12:000, (5.0)), " +
                          "(7, 00:00:13:000, 00:00:14:000, (6.0)), " +
                          "(8, 00:00:15:000, 00:00:16:000, (7.0)), " +
                          "(9, 00:00:15:000, 00:00:16:000, (8.0))))";
            String expected_f_col0_DBstring =
                "(DataColumn " +
                    "(name f_col0) " +
                    "(id 3) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 1) " +
                    "(itsMveType FLOAT) " +
                    "(varLen false) " +
                    "(numCells 9) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 22) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 1) " +
                            "(onset (60,00:00:01:000)) " +
                            "(offset (60,00:00:02:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 23) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 22) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 24) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 2) " +
                            "(onset (60,00:00:03:000)) " +
                            "(offset (60,00:00:04:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 25) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 24) " +
                                            "(itsValue 1.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 26) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 3) " +
                            "(onset (60,00:00:05:000)) " +
                            "(offset (60,00:00:06:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 27) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 26) " +
                                            "(itsValue 2.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 28) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 4) " +
                            "(onset (60,00:00:07:000)) " +
                            "(offset (60,00:00:08:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 29) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 28) " +
                                            "(itsValue 3.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 30) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 5) " +
                            "(onset (60,00:00:09:000)) " +
                            "(offset (60,00:00:10:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 31) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 30) " +
                                            "(itsValue 4.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 32) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 6) " +
                            "(onset (60,00:00:11:000)) " +
                            "(offset (60,00:00:12:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 33) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 32) " +
                                            "(itsValue 5.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 34) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 7) " +
                            "(onset (60,00:00:13:000)) " +
                            "(offset (60,00:00:14:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 35) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 34) " +
                                            "(itsValue 6.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 36) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 8) " +
                            "(onset (60,00:00:15:000)) " +
                            "(offset (60,00:00:16:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 37) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 36) " +
                                            "(itsValue 7.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0)))))))), " +
                        "(DataCell " +
                            "(id 38) " +
                            "(itsColID 3) " +
                            "(itsMveID 1) " +
                            "(itsMveType FLOAT) " +
                            "(ord 9) " +
                            "(onset (60,00:00:15:000)) " +
                            "(offset (60,00:00:16:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 1) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue " +
                                            "(id 39) " +
                                            "(itsFargID 2) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 38) " +
                                            "(itsValue 8.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))))))";
            String expected_i_col0_string = 
                "(i_col0, ((1, 00:00:00:000, 00:00:00:000, (0))))";
            String expected_i_col0_DBstring = 
                "(DataColumn " +
                    "(name i_col0) " +
                    "(id 6) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 4) " +
                    "(itsMveType INTEGER) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 40) " +
                            "(itsColID 6) " +
                            "(itsMveID 4) " +
                            "(itsMveType INTEGER) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 4) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue " +
                                            "(id 41) " +
                                            "(itsFargID 5) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 40) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))))))";
            String expected_m_col0_string = 
                "(m_col0, ((1, 00:00:00:000, 00:00:00:000, (<arg>))))";
            String expected_m_col0_DBstring = 
                "(DataColumn " +
                    "(name m_col0) " +
                    "(id 9) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 7) " +
                    "(itsMveType MATRIX) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 42) " +
                            "(itsColID 9) " +
                            "(itsMveID 7) " +
                            "(itsMveType MATRIX) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 7) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue " +
                                            "(id 43) " +
                                            "(itsFargID 8) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 42) " +
                                            "(itsValue <arg>) " +
                                            "(subRange false))))))))))))";
            String expected_m_col1_string = 
                "(m_col1, ())";
            String expected_m_col1_DBstring = 
                "(DataColumn " +
                    "(name m_col1) " +
                    "(id 12) " +
                    "(hidden true) " +
                    "(readOnly false) " +
                    "(itsMveID 10) " +
                    "(itsMveType MATRIX) " +
                    "(varLen true) " +
                    "(numCells 0) " +
                    "(itsCells ())))";
            String expected_n_col0_string = 
                "(n_col0, ((1, 00:00:00:000, 00:00:00:000, ())))";
            String expected_n_col0_DBstring = 
                "(DataColumn " +
                    "(name n_col0) " +
                    "(id 15) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 13) " +
                    "(itsMveType NOMINAL) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 44) " +
                            "(itsColID 15) " +
                            "(itsMveID 13) " +
                            "(itsMveType NOMINAL) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 13) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((NominalDataValue " +
                                            "(id 45) " +
                                            "(itsFargID 14) " +
                                            "(itsFargType NOMINAL) " +
                                            "(itsCellID 44) " +
                                            "(itsValue <null>) " +
                                            "(subRange false))))))))))))";
            String expected_p_col0_string = 
                "(p_col0, ((1, 00:00:00:000, 00:00:00:000, (()))))";
            String expected_p_col0_DBstring = 
                "(DataColumn " +
                    "(name p_col0) " +
                    "(id 18) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 16) " +
                    "(itsMveType PREDICATE) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                        "((DataCell " +
                            "(id 46) " +
                            "(itsColID 18) " +
                            "(itsMveID 16) " +
                            "(itsMveType PREDICATE) " +
                            "(ord 1) " +
                            "(onset (60,00:00:00:000)) " +
                            "(offset (60,00:00:00:000)) " +
                            "(val " +
                                "(Matrix " +
                                    "(mveID 16) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((PredDataValue " +
                                            "(id 47) " +
                                            "(itsFargID 17) " +
                                            "(itsFargType PREDICATE) " +
                                            "(itsCellID 46) " +
                                            "(itsValue ()) " +
                                            "(subRange false))))))))))))";
            String expected_t_col0_string = 
                "(t_col0, ((1, 00:00:00:000, 00:00:00:000, ())))";
            String expected_t_col0_DBstring = 
                "(DataColumn " +
                    "(name t_col0) " +
                    "(id 21) " +
                    "(hidden false) " +
                    "(readOnly false) " +
                    "(itsMveID 19) " +
                    "(itsMveType TEXT) " +
                    "(varLen false) " +
                    "(numCells 1) " +
                    "(itsCells " +
                    "((DataCell " +
                        "(id 49) " +
                        "(itsColID 21) " +
                        "(itsMveID 19) " +
                        "(itsMveType TEXT) " +
                        "(ord 1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val " +
                            "(Matrix " +
                                "(mveID 19) " +
                                "(varLen false) " +
                                "(argList " +
                                    "((TextStringDataValue " +
                                        "(id 50) " +
                                        "(itsFargID 20) " +
                                        "(itsFargType TEXT) " +
                                        "(itsCellID 49) " +
                                        "(itsValue <null>) " +
                                        "(subRange false))))))))))))";
            
            f_col0_string = f_col0.toString();
            f_col0_DBstring = f_col0.toDBString();

            i_col0_string = i_col0.toString();
            i_col0_DBstring = i_col0.toDBString();

            m_col0_string = m_col0.toString();
            m_col0_DBstring = m_col0.toDBString();

            m_col1_string = m_col1.toString();
            m_col1_DBstring = m_col1.toDBString();

            n_col0_string = n_col0.toString();
            n_col0_DBstring = n_col0.toDBString();

            p_col0_string = p_col0.toString();
            p_col0_DBstring = p_col0.toDBString();

            t_col0_string = t_col0.toString();
            t_col0_DBstring = t_col0.toDBString();
            
            if ( expected_f_col0_string.compareTo(f_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_col0.toString(): \"%s\".\n", 
                                     f_col0.toString());
                }
            }
            
            if ( expected_f_col0_DBstring.compareTo(f_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_col0.toDBString(): \"%s\".\n", 
                                     f_col0.toDBString());
                }
            }
            
            
            if ( expected_i_col0_string.compareTo(i_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_col0.toString(): \"%s\".\n", 
                                     i_col0.toString());
                }
            }
            
            if ( expected_i_col0_DBstring.compareTo(i_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_col0.toDBString(): \"%s\".\n", 
                                     i_col0.toDBString());
                }
            }
            
            
            if ( expected_m_col0_string.compareTo(m_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_col0.toString(): \"%s\".\n", 
                                     m_col0.toString());
                }
            }
            
            if ( expected_m_col0_DBstring.compareTo(m_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_col0.toDBString(): \"%s\".\n", 
                                     m_col0.toDBString());
                }
            }
            
            
            if ( expected_m_col1_string.compareTo(m_col1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_col1.toString(): \"%s\".\n", 
                                     m_col1.toString());
                }
            }
            
            if ( expected_m_col1_DBstring.compareTo(m_col1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_col1.toDBString(): \"%s\".\n", 
                                     m_col1.toDBString());
                }
            }
            
            
            if ( expected_n_col0_string.compareTo(n_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_col0.toString(): \"%s\".\n", 
                                     n_col0.toString());
                }
            }
            
            if ( expected_n_col0_DBstring.compareTo(n_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_col0.toDBString(): \"%s\".\n", 
                                     n_col0.toDBString());
                }
            }
            
            
            if ( expected_p_col0_string.compareTo(p_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_col0.toString(): \"%s\".\n", 
                                     p_col0.toString());
                }
            }
            
            if ( expected_p_col0_DBstring.compareTo(p_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_col0.toDBString(): \"%s\".\n", 
                                     p_col0.toDBString());
                }
            }
            
            
            if ( expected_t_col0_string.compareTo(t_col0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_col0.toString(): \"%s\".\n", 
                                     t_col0.toString());
                }
            }
            
            if ( expected_t_col0_DBstring.compareTo(t_col0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_col0.toDBString(): \"%s\".\n", 
                                     t_col0.toDBString());
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
        
    } /* DataCell::TestToStringMethods() */

    
    /**
     * Verify3ArgConstInit()
     *
     * Verify that the supplied instance of DataColumn has been correctly 
     * initialized by a constructor.
     *
     *                                              JRM -- 12/26/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int Verify3ArgConstInit(Database db,
                                  DataColumn dc,
                                  String desc,
                                  String expectedName,
                                  MatrixVocabElement.matrixType expectedMveType,
                                  java.io.PrintStream outStream,
                                  boolean verbose)
    {
        boolean expectedHidden = false;
        boolean expectedReadOnly = false;
        int expectedNumCells = 0;
        int failures = 0;
        
        if ( db == null )
        {
            failures++;
            outStream.printf(
                    "DataColumn::Verify3ArgConstInit: db null on entry.\n");
        }
        
        if ( dc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::Verify3ArgConstInit: dc null on entry.\n");
        }
        
        if ( desc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::Verify3ArgConstInit: c null on entry.\n");
        }

        if ( dc.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("dc.db not initialized correctly.\n");
            }
        }

        failures += VerifyInitialization(db,
                                         (Column)dc,
                                         desc,
                                         expectedName,
                                         expectedHidden,
                                         expectedReadOnly,
                                         expectedNumCells,
                                         outStream,
                                         verbose);
                
        if ( dc.itsCells != null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: dc.itsCells != null.\n", desc);
            }
        }
        
        if ( dc.itsMveType != expectedMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: dc.itsMveType not initialized correctly: %s (%s).\n",
                        desc, 
                        dc.itsMveType.toString(), 
                        expectedMveType.toString());
            }
        }
        
        if ( dc.itsMveID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: dc.itsMveID = %d (%d expected).\n",
                                 desc, 
                                 dc.itsMveID, 
                                 DBIndex.INVALID_ID);
            }
        }
        
        if ( dc.varLen != false )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: dc.varLen = %b (%b expected).\n",
                                 desc, 
                                 dc.varLen, 
                                 false);
            }
        }
                
        return failures;
        
    } /* DataColumn::Verify3ArgConstInit() */

    
    /**
     * Verify5ArgConstInit()
     *
     * Verify that the supplied instance of DataColumn has been correctly 
     * initialized by a 5 arg constructor.
     *
     *                                              JRM -- 12/26/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int Verify5ArgConstInit(Database db,
                                  DataColumn dc,
                                  String desc,
                                  String expectedName,
                                  boolean expectedHidden,
                                  boolean expectedReadOnly,
                                  boolean expectedVarLen,
                                  long expectedMveID,
                                  MatrixVocabElement.matrixType expectedMveType,
                                  java.io.PrintStream outStream,
                                  boolean verbose)
    {
        int expectedNumCells = 0;
        int failures = 0;
        
        if ( db == null )
        {
            failures++;
            outStream.printf(
                    "DataColumn::Verify5ArgConstInit: db null on entry.\n");
        }
        
        if ( dc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::Verify5ArgConstInit: dc null on entry.\n");
        }
        
        if ( desc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::Verify5ArgConstInit: c null on entry.\n");
        }

        if ( dc.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("dc.db not initialized correctly.\n");
            }
        }

        failures += VerifyInitialization(db,
                                         (Column)dc,
                                         desc,
                                         expectedName,
                                         expectedHidden,
                                         expectedReadOnly,
                                         expectedNumCells,
                                         outStream,
                                         verbose);
                
        if ( dc.itsCells != null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: dc.itsCells != null.\n", desc);
            }
        }
        
        if ( dc.itsMveID != expectedMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: dc.itsMveID not initialized correctly: %d (%d).\n",
                        desc, 
                        dc.itsMveID, 
                        expectedMveID);
            }
        }
        
        if ( dc.itsMveType != expectedMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: dc.itsMveType not initialized correctly: %s (%s).\n",
                        desc, 
                        dc.itsMveType.toString(), 
                        expectedMveType.toString());
            }
        }
        
        if ( dc.varLen != expectedVarLen )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: dc.varLen = %b (%b expected).\n",
                                 desc, 
                                 dc.varLen, 
                                 expectedVarLen);
            }
        }
                
        return failures;
        
    } /* DataColumn::Verify3ArgConstInit() */


    /**
     * VerifyDataColumnCopy()
     *
     * Verify that the supplied instances of DataColumn are distinct, that they
     * contain no common references (other than db), and that with the exception
     * of the itsCells field, they have the same value (Recall that the copy
     * construtor for DataColumn specifically does not copy itsCells from the
     * base instance.  Instead itsCells in the copy is always set to null, 
     * regardless of the value of itsCells in the base instance).
     *
     *                                              JRM -- 12/30/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int VerifyDataColumnCopy(DataColumn base,
                                           DataColumn copy,
                                           java.io.PrintStream outStream,
                                           boolean verbose,
                                           String baseDesc,
                                           String copyDesc)
    {
        int failures = 0;
        
        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDataColumnCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDataColumnCopy: %s null on entry.\n", 
                             copyDesc);
        }
        else if ( base == copy )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        
        failures += Column.VerifyColumnCopy((Column)base, (Column)copy, 
                                            outStream, verbose, 
                                            baseDesc, copyDesc);

        if ( copy.itsCells != null )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsCells != null.\n", copyDesc);
            }
        }

        if ( base.itsMveID != copy.itsMveID )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsMveID == %d != %s.itsMveID == %d.\n", 
                                 baseDesc, base.itsMveID, 
                                 copyDesc, copy.itsMveID);
            }
        }
        
        if ( base.itsMveType != copy.itsMveType )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsMveType == %s != %s.itsMveType == %s.\n", 
                                 baseDesc, base.itsMveType.toString(), 
                                 copyDesc, copy.itsMveType.toString());
            }
        }

        if ( base.varLen != copy.varLen )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.varLen == %b != %s.itsMveID == %b.\n", 
                                 baseDesc, base.varLen, 
                                 copyDesc, copy.varLen);
            }
        }
        
        return failures;
        
    } /* DataColumn::VerifyDataColumnCopy() */

} //End of DataColumn class definition
