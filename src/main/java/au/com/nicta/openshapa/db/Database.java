/*
 * Database.java
 *
 * Created on December 12, 2006, 12:37 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 * Abstract database class
 * @author FGA
 */
public abstract class Database
{
    
    /*************************************************************************/
    /*************************** Constants: **********************************/
    /*************************************************************************/
    
    /** Constant type for Data Column Creation */
    public final static int COLUMN_TYPE_DATA = 1;
      
    /** Constant type for Reference Column Creation */
    public final static int COLUMN_TYPE_REFERENCE = 2;

    /** Default Ticks per second from MacSHAPA */
    public final static int DEFAULT_TPS = 60;

    /** Default start time */
    public final static long DEFAULT_START_TIME = 0;
  
    
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    
    /** Database name */
    String name = "Undefined";
    
    /** Database description */
    String description = null;
    
    /** Start time flag */
    protected boolean useStartTime = false;

    /** Start time value */
    long startTime = DEFAULT_START_TIME;

    /** Ticks per second */
    int tps = DEFAULT_TPS;
    
    /** Whether we are keeping all columns sorted by time automatically */
    protected boolean temporalOrdering = false;

//    /** Database change listeners */
//    java.util.Vector<DatabaseChangeListener> changeListeners =
//            new java.util.Vector<DatabaseChangeListener>();
    
    /** Current database user UID */
    int curUID = 0;
    
    /** Index of all DBElements in the database */
    DBIndex idx = null;
    
    /** List of all vocab elements in the database */
    VocabList vl = null;
    
    /** list of all columns in the database */
    ColumnList cl = null;
    
    /** Cascade Listeners */
    private CascadeListeners listeners = null;

    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /**
     * Database()
     *
     * Constructor for Database.  Sets up data structures used by all flavors
     * of databases.
     *                                              JRM -- 4/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public Database()
        throws SystemErrorException
    {
        super();
        
        this.idx = new DBIndex(this);
        
        this.vl = new VocabList(this);
        
        this.cl = new ColumnList(this);
        
        this.listeners = new CascadeListeners(this);
        
        return;
        
    } /* Database::Database() */
   
        
    /*************************************************************************/
    /******************* Abstract Method Declarations: ***********************/
    /*************************************************************************/

    /*** Version Reporting ***/
    
    /**
     * getType()
     *
     * Gets the database type string<br>
     * (eg ODB File)
     *
     * Changes:
     *
     *    - None.
     */
  
    public abstract String getType();

    /**
     * getVersion()
     *
     * Gets the database version number<br>
     * (eg 2.1)
     *
     * Changes:
     *
     *    - None.
     */
    
    public abstract float getVersion();


//    /*** Database element management ***/
//    
//    /**
//     * getCell() -- by column ID and cell ID
//     *
//     * Gets the cell associated with the given id in the given column
//     * @param columnID the id of the column the cell is in
//     * @param cellID the id of the cell
//     * @return the cell associated with the given cell id
//     *
//     * Changes:
//     *
//     *    - None.
//     */
// 
//    public abstract Cell getCell(long columnID, long cellID);
//
//  
//    /**
//     * getFormalArgument() -- by formal argument ID
//     * 
//     * Gets the argument associated with the given id
//     * 
//     * @param argumentID the id of the argument
//     * @return the argument associated with the given argument id
//     * 
//     * Changes:
//     * 
//     *    - Used to be called getArgument().  Changed the name to avoid
//     *      confusion.  Also changed type to AFormalArgumentto 
//     *      reflect changes in class structure.
//     * 
//     *                                      -- JRM - 3/03/07
//     */
//  
//    public abstract FormalArgument getFormalArgument(long argumentID); 
    
  
//    /**
//     * createColumn()
//     *
//     * Creates a Column of the given type in the database.
//     * @param columnType the type of column to create:<br>
//     * Must be either:
//     * <UL>
//     * <LI>COLUMN_TYPE_DATA</LI> or
//     * <LI>COLUMN_TYPE_REFERENCE</LI>
//     * </UL>
//     * @return the newly created column object 
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//  
//    public abstract Column createColumn(int columnType);
//
//    
//    /**
//     * createCell()
//     *
//     * Creates a new cell in the given column.
//     * @param columnID the id of the column in which to create the cell
//     * @return the newly created cell
//     *
//     * Changes:
//     * 
//     *    - None.
//     */
//  
//    public abstract Cell createCell(long columnID);


//    /**
//     * createFormalArgument()
//     *
//     *      NOTE: We must support many types of formal arguments.  Thus this
//     *            method makes little sense.  Expect that I will rework this
//     *            completely.
//     *                                  -- JRM - 3/03/07
//     *
//     * Creates a new formal argument.
//     * @return the newly created formal argument
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//  
//    public abstract FormalArgument createFormalArgument();
//
//    /**
//     * createMatrixVocabElement()
//     *
//     * Creates a new matrix vocab element.
//     * @return the newly created matrix vocab element
//     *
//     * Changes:
//     *
//     *    - Changed return type to match changes in class structure for 
//     *      vocab elements.
//     *                                          -- JRM - 3/03/07
//     */
//  
//    public abstract VocabElement createMatrixVocabElement();
//
//    /**
//     * createPredicateVocabElement()
//     *
//     * Creates a new predicate vocab element.
//     * @return the newly created predicate vocab element
//     *
//     * Changes
//     *
//     *    - Changed return type to match changes in class structure for 
//     *      vocab elements.
//     *                                          -- JRM - 3/03/07
//     */
//  
//    public abstract VocabElement createPredicateVocabElement();
    
        
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * toDBString()
     *
     * Returns a String representation of the Database for debugging or testing.
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
        
        s = "(" + this.getName() + " " + 
                  this.vl.toDBString() + " " +
                  this.cl.toDBString() + ")";
               
        return (s);
        
    } /* Database::toDBString() */
    
    /**
     * toString()
     *
     * Returns a String representation of the Database for display or testing.
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
        
        if ( this.description == null )
        {
            s = "(" + this.getName() + " " + 
                      this.vl.toString() + " " +
                      this.cl.toString() + ")";
        }
        else
        {
            s = "(" + this.getName() + " " + 
                      "(Description: " + this.getDescription() + ") " +
                      this.vl.toString() + " " +
                      this.cl.toString() + ")";
        }
               
        return (s);
        
    } /* Database::toString() */

        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/
    
    /**
     * getCurUID()
     *
     * Get the current user ID.
     *
     *               JRM -- 4/11/07
     *
     * @return  ID of the user who is currently working on the database.
     *
     * Changes:
     *
     *    - None.
     */
    
    public int getCurUID()
    {
        return this.curUID;
        
    } /* Database::getCurUID() */
    
    
    /**
     * getDescription()
     *
     * Get the description of the database.
     *
     *                    JRM -- 4/10/07
     *
     * @return  A copy of the description field of the instance of Database, or 
     *          null if the description is undefined.
     *
     * Changes:
     *
     *    - None.
     */
    
    public String getDescription()
    {
        String descriptionCopy = null;
        
        if ( this.description != null )
        {
            descriptionCopy = new String(this.description);
        }
        
        return descriptionCopy;
        
    } /* Database::geDescription() */
    
    /**
     * getName()
     *
     * Get the name of the database.
     *
     *                    JRM -- 4/10/07
     *
     * @return  A copy of the name field of the instance of Database, or null
     *          if the name is undefined.
     *
     * Changes:
     *
     *    - None.
     */
    
    public String getName()
    {
        String nameCopy = null;
        
        if ( this.name != null )
        {
            nameCopy = new String(this.name);
        }
        
        return nameCopy;
        
    } /* Database::getName() */
    
    /**
     * getTemporalOrdering()
     *
     * Gets the current value of the temporal ordering flag.
     *
     *                              JRM -- 3/20/08
     *
     * @return Value of this.temporalOrdering.
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean getTemporalOrdering()
    {
        
        return this.temporalOrdering;
        
    } /* Database::getTemporalOrdering() */
     

    /**
     * getTicks()
     *
     * Gets the ticks per second
     *
     * @return ticks per second
     *
     * Changes:
     *
     *    - None.
     */
  
    public int getTicks()
    {
        return (this.tps);
        
    } /* Datebase::getTicks() */
    
    /**
     * setDescription()
     *
     * Set the description of the database.  Note that null is a valid 
     * new description, as the database description is optional.
     *
     *                                  JRM -- 4/10/07
     *
     * @return  void
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setDescription(String newDescription)
    {
        if ( newDescription != null )
        {
            this.description = new String(newDescription);
        }
        else
        {
            this.description = null;
        }
         
        return;
        
    } /* Database::seDescription() */
     
    
    /**
     * setName()
     *
     * Set the description of the database.  Note that null is a valid 
     * new description, as the database description is optional.
     *
     *                                  JRM -- 4/10/07
     *
     * @return  void
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setName(String newName)
        throws SystemErrorException
    {
        final String mName = "Databaset::setName(): ";
        
        if ( ( newName == null ) ||
             ( newName.length() == 0 ) )
        {
            throw new SystemErrorException(mName + "null or empty name");
        }
        else 
        {
            this.name = new String(newName);
        }
         
        return;
        
    } /* Database::setName() */
    
    
    /**
     * setTemporalOrdering()
     *
     * Set the current value of the temporal ordering flag.  If the flag is
     * switched from false to true, sort all the columns.
     *
     *                              JRM -- 3/20/08
     *
     * @return void.
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setTemporalOrdering(boolean newTemporalOrdering)
        throws SystemErrorException
    {
        
        if ( this.temporalOrdering != newTemporalOrdering )
        {
            this.temporalOrdering = newTemporalOrdering;
            
            if ( this.temporalOrdering )
            {
                this.cl.applyTemporalOrdering();
            }
        }
        
        return;
        
    } /* Database::setTemporalOrdering() */
   

    /**
     * setTicks()
     *
     * Sets the ticks per second
     *
     * @param tps ticks per second
     *
     * Changes:
     *
     *    - None.
     */
    
    // TODO: finish this
  
    public void setTicks(int tps)
        throws SystemErrorException
    {
        int prevTPS = this.tps;
        this.tps = tps;
        
        throw new SystemErrorException("not fully implemented");

//        // Notify all listeners of TPS change
//        for (int i=0; i<this.changeListeners.size(); i++) {
//            ((DatabaseChangeListener)this.changeListeners.elementAt(i)).databaseTicksChanged(this, prevTPS);
//        }
//        
//        return;
        
    } /* Datebase::setTicks() */


    /**
     * useStartTime()
     *
     * Gets the use start time flag
     *
     * @return true if we are to use a start time
     *
     * Changes:
     *
     *      - None.
     */
  
    public boolean useStartTime()
    {
        return (this.useStartTime);
        
    } /* Database::useStartTime() */
    
    /**
     * setUseStartTime()
     *
     * Sets the start time flag
     *
     * @param useStartTime the use start time flag value
     *
     * Changes:
     *
     *    - None.
     */
  
    public void setUseStartTime(boolean useStartTime)
    {
        this.useStartTime = useStartTime; 
    
    } /* Database::useStartTime() */


    /**
     * getStartTime()
     *
     * Gets the start time
     * @return the start time value
     *
     * Changes:
     *
     *    - None.
     */
  
    public long getStartTime()
    {
        return (this.startTime);
        
    } /* Database::getStarTime() */

    /**
     * setStartTime()
     *
     * Sets the start time
     * @param startTime the start time
     *
     * Changes:
     *
     *    - None.
     */
  
    // TODO: finish this.
    
    public void setStartTime(long startTime)
        throws SystemErrorException
    {
        long prevST = this.startTime;
        this.startTime = startTime;
        
        throw new SystemErrorException("not fully implemented");

//        // Notify all listeners of TPS change
//        for (int i=0; i<this.changeListeners.size(); i++) 
//        {
//            ((DatabaseChangeListener)this.changeListeners.elementAt(i)).databaseStartTimeChanged(this, prevST);
//        }
//        
//        return;
        
    } /* Database::setStarTime() */

        
    /*************************************************************************/
    /*************************** Methods: ************************************/
    /*************************************************************************/
    
    /*************************************************************************/
    /************************** Cell Management ******************************/
    /*************************************************************************/
    /*                                                                       */
    /* The method defined in this section support the insertion, deletion,   */
    /* and modification of cells in columns.                                 */
    /*                                                                       */
    /* The following methods are provided:                                   */
    /*                                                                       */
    /*      appendCell(cell)                                                 */
    /*      insertCell(cell, ord)                                            */
    /*                                                                       */
    /*      getCell(cellID)                                                  */
    /*      getCell(colID, cellOrd)                                          */
    /*                                                                       */
    /*      replaceCell(cellID)                                              */
    /*                                                                       */
    /*      removeCell(cellID)                                               */
    /*                                                                       */
    /*************************************************************************/
    
    /**
     * appendCell()
     *
     * Append a copy of the supplied cell to the column indicated in the 
     * itsColID field of the cell.  The cell must not have an ID assigned, 
     * and must be of a type congruent with the type of the column.  In the 
     * case of a DataCell and DataColumn, the DataCell must have itsMveID and
     * itsMveType fields with values matching that of the target DataColumn. 
     *
     * Returns the id assigned to the newly appended cell
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public long appendCell(Cell cell)
        throws SystemErrorException
    {
        final String mName = "Database::appendCell(cell): ";
        long cellID = DBIndex.INVALID_ID;
        long colID = DBIndex.INVALID_ID;
        Column col = null;
        DataColumn dc = null;
        DataCell dataCell = null;
        ReferenceColumn rc = null;
        ReferenceCell refCell = null;
        
        if ( cell == null )
        {
            throw new SystemErrorException(mName + "cell == null");
        }
        else if ( cell.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "cell.id != INVALID_ID");
        }
        
        colID = cell.getItsColID();
        
        if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "cell.itsColID == INVALID_ID");
        }
        
        col = this.cl.getColumn(colID);
        
        if ( col instanceof DataColumn )
        {
            if ( ! ( cell instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                        "cell/column type mismatch -- DataColumn ID expected");
            }
            
            dc = (DataColumn)col;
            
            dataCell = new DataCell((DataCell)cell);
            
            dc.appendCell(dataCell);
            
            cellID = dataCell.getID();
        }
        else if ( col instanceof ReferenceColumn )
        {
            if ( ! ( cell instanceof ReferenceCell ) )
            {
                throw new SystemErrorException(mName + 
                    "cell/column type mismatch -- ReferenceColumn ID expected");
            }
            
            rc = (ReferenceColumn)col;
            
            refCell = new ReferenceCell((ReferenceCell)cell);
            
            this.idx.addElement(refCell);
            cellID = refCell.getID();
            
            rc.appendCell(refCell);
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subclass");
        }
        
        return cellID;
        
    } /* Database::appendCell(cell) */
    
    
    /**
     * insertCell()
     *
     * Insert a copy of the supplied cell to the column indicated in the 
     * itsColID field of the cell at the specified ord.  
     * 
     * The cell must not have an ID assigned, and must be of a type congruent 
     * with the type of the column.  In the case of a DataCell and DataColumn, 
     * the DataCell must have itsMveID and itsMveType fields with values 
     * matching that of the target DataColumn. 
     *
     * The ord parameter must be in the range of 1 to the number of cells in
     * the column, or simply 1 if the column is empty.
     *
     * Returns the id assigned to the newly inserted cell
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public long insertdCell(Cell cell,
                            int ord)
        throws SystemErrorException
    {
        final String mName = "Database::insertCell(cell, ord): ";
        long cellID = DBIndex.INVALID_ID;
        long colID = DBIndex.INVALID_ID;
        Column col = null;
        DataColumn dc = null;
        DataCell dataCell = null;
        ReferenceColumn rc = null;
        ReferenceCell refCell = null;
        
        if ( cell == null )
        {
            throw new SystemErrorException(mName + "cell == null");
        }
        else if ( cell.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "cell.id != INVALID_ID");
        }
        else if ( ord < 1 )
        {
            throw new SystemErrorException(mName + "ord is non positive");
        }
        
        colID = cell.getItsColID();
        
        if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "cell.itsColID == INVALID_ID");
        }
        
        col = this.cl.getColumn(colID);
        
        if ( col instanceof DataColumn )
        {
            if ( ! ( cell instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                        "cell/column type mismatch -- DataColumn ID expected");
            }
            
            dc = (DataColumn)col;
            
            dataCell = new DataCell((DataCell)cell);
            
            dc.insertCell(dataCell, ord);
            
            cellID = dataCell.getID();
        }
        else if ( col instanceof ReferenceColumn )
        {
            if ( ! ( cell instanceof ReferenceCell ) )
            {
                throw new SystemErrorException(mName + 
                    "cell/column type mismatch -- ReferenceColumn ID expected");
            }
            
            rc = (ReferenceColumn)col;
            
            refCell = new ReferenceCell((ReferenceCell)cell);
            
            this.idx.addElement(refCell);
            cellID = refCell.getID();
            
            rc.insertCell(refCell, ord);
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subclass");
        }
        
        return cellID;
        
    } /* Database::insertCell(cell, ord) */
    
    
    /**
     * getCell(id)
     *
     * Given a cell id, look it up in the index, and return copy.  Throw a 
     * system error exception if no such cell exists.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public Cell getCell(long id)
        throws SystemErrorException
    {
         final String mName = "Database::getCell(id): ";
         DBElement dbe = null;
         Cell cell = null;
         
         if ( id == DBIndex.INVALID_ID )
         {
             throw new SystemErrorException(mName + "id == INVALID_ID");
         }
         
         dbe = this.idx.getElement(id);
         
         if ( dbe == null )
         {
             throw new SystemErrorException(mName + "id has no referent");
         }
         else if ( ! ( dbe instanceof Cell ) )
         {
             throw new SystemErrorException(mName + 
                                            "id doesn't refer to a Cell");
         }
         
         if ( dbe instanceof DataCell ) 
         {
             cell = new DataCell((DataCell)dbe);
         }
         else if ( dbe instanceof ReferenceCell )
         {
             cell = new ReferenceCell((ReferenceCell)dbe);
         }
         else
         {
             throw new SystemErrorException(mName + "Unknown Cell subclass.");
         }
         
         return cell;
       
    } /* Database::getCell(id) */
    
    
    /**
     * getCell(colID, ord)
     *
     * Given a column id, and a cell ord, look it up the cell at that ord in 
     * the target column, and return copy.  Throw a system error exception 
     * if no such cell exists.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public Cell getCell(long colID,
                        int ord)
        throws SystemErrorException
    {
         final String mName = "Database::getCell(colID, ord): ";
         Column col = null;
         Cell cell = null;
         
         if ( colID == DBIndex.INVALID_ID )
         {
             throw new SystemErrorException(mName + "id == INVALID_ID");
         }
         
         col = this.cl.getColumn(colID);
         
         if ( col == null )
         {
             throw new SystemErrorException(mName + "colID has no referent");
         }
         
         if ( col instanceof DataColumn )
         {
             cell = ((DataColumn)col).getCellCopy(ord);
         }
         else if ( col instanceof ReferenceColumn )
         {
             cell = ((ReferenceColumn)col).getCellCopy(ord);
         }
         else
         {
             throw new SystemErrorException(mName + 
                     "Unknown subclass of Column");
         }
         
         return cell;
       
    } /* Database::getCell(colID, ord) */
    
    
    /**
     * replaceCell()
     *
     * Replace the old version of a cell with the new one supplied as a 
     * parameter.
     *
     * The id, itsColID, itsMveID, and itsMveType fields of the new cell 
     * must match that of the old.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void replaceCell(Cell newCell)
        throws SystemErrorException
    {
        final String mName = "Database::replaceCell(newCell): ";
        int ord;
        long cellID = DBIndex.INVALID_ID;
        long colID = DBIndex.INVALID_ID;
        DBElement dbe = null;
        Cell oldCell = null;
        Column col = null;
        DataColumn dc = null;
        DataCell newDataCell = null;
        DataCell oldDataCell = null;
        ReferenceColumn rc = null;
        ReferenceCell newRefCell = null;
        ReferenceCell oldRefCell = null;
        
        if ( newCell == null )
        {
            throw new SystemErrorException(mName + "newCell == null");
        }
        else if ( newCell.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "newCell.id != INVALID_ID");
        }
        
        cellID = newCell.getID();
        
        dbe = this.idx.getElement(cellID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "newCell.id has no referent");
        }
        else if ( ! ( dbe instanceof Cell ) )
        {
            throw new SystemErrorException(mName + 
                    "newCell.id doesn't refer to a cell");
        }
        
        oldCell = (Cell)dbe;
        
        colID = newCell.getItsColID();
        
        if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "cell.itsColID == INVALID_ID");
        }
        
        col = this.cl.getColumn(colID);
        
        if ( col instanceof DataColumn )
        {
            if ( ! ( newCell instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                    "newCell/column type mismatch -- DataCell expected");
            }
            
            if ( ! ( oldCell instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                    "oldCell/column type mismatch -- DataCell expected");
            }
            
            dc = (DataColumn)col;
            newDataCell = new DataCell((DataCell)newCell);
            oldDataCell = (DataCell)oldCell;
            ord = oldDataCell.getOrd();
            
            if ( dc.replaceCell(newDataCell, ord) != oldDataCell )
            {
                throw new SystemErrorException(mName + 
                        "dc.replaceCell() return unexpected value");
            }
            
        }
        else if ( col instanceof ReferenceColumn )
        {
            if ( ! ( newCell instanceof ReferenceCell ) )
            {
                throw new SystemErrorException(mName + 
                    "newCell/column type mismatch -- ReferenceCell expected");
            }
            
            if ( ! ( oldCell instanceof ReferenceCell ) )
            {
                throw new SystemErrorException(mName + 
                    "newCell/column type mismatch -- ReferenceCell expected");
            }
            
            rc = (ReferenceColumn)col;
            newRefCell = new ReferenceCell((ReferenceCell)newCell);
            oldRefCell = (ReferenceCell)oldCell;
            ord = oldRefCell.getOrd();
            
            if ( rc.replaceCell(newRefCell, ord) != oldRefCell )
            {
                throw new SystemErrorException(mName + 
                        "rc.replaceCell() return unexpected value");
            }
            
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subclass");
        }
        
        return;
        
    } /* Database::replaceCell(cell) */
    
    
    /**
     * removeCell()
     *
     * Remove the specified cell from its column and discard it.
     *
     *                                          JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void removeCell(long cellID)
        throws SystemErrorException
    {
        final String mName = "Database::replaceCell(newCell): ";
        int ord;
        long colID = DBIndex.INVALID_ID;
        DBElement dbe = null;
        Cell cell = null;
        Column col = null;
        DataColumn dc = null;
        DataCell dataCell = null;
        ReferenceColumn rc = null;
        ReferenceCell refCell = null;
        
        if ( cellID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "cellID == INVALID_ID");
        }
        
        dbe = this.idx.getElement(cellID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "cellID has no referent");
        }
        else if ( ! ( dbe instanceof Cell ) )
        {
            throw new SystemErrorException(mName + 
                    "newCell.id doesn't refer to a cell");
        }
        
        cell = (Cell)dbe;
        
        colID = cell.getItsColID();
        
        if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "cell.itsColID == INVALID_ID");
        }
        
        col = this.cl.getColumn(colID);
        
        if ( col instanceof DataColumn )
        {
            if ( ! ( cell instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                    "cell/column type mismatch -- DataCell expected");
            }
            
            dc = (DataColumn)col;
            dataCell = (DataCell)cell;
            ord = dataCell.getOrd();
            
            if ( dc.removeCell(ord, cellID) != dataCell )
            {
                throw new SystemErrorException(mName + 
                        "dc.removeCell() return unexpected value");
            }
            
        }
        else if ( col instanceof ReferenceColumn )
        {
            if ( ! ( cell instanceof ReferenceCell ) )
            {
                throw new SystemErrorException(mName + 
                    "cell/column type mismatch -- ReferenceCell expected");
            }
            
            rc = (ReferenceColumn)col;
            refCell = (ReferenceCell)cell;
            ord = refCell.getOrd();
            
            if ( rc.removeCell(ord, cellID) != refCell )
            {
                throw new SystemErrorException(mName + 
                        "rc.removeCell() return unexpected value");
            }
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subclass");
        }
        
        return;
        
    } /* Database::removeCell() */
    

    /*************************************************************************/
    /********************** Column List Management ***************************/
    /*************************************************************************/
    /*                                                                       */
    /* The Column list contains the list of all columns in the database.     */
    /*                                                                       */
    /* At present, the column list is envisioned as containing two types of  */
    /* columns, DataColumn and ReferenceColumn.                              */
    /*                                                                       */
    /* DataColumns are very similar to the old MacSHAPA columns or spread    */
    /* sheet variables, being typed as either integer, float, text, nominal, */
    /* predicate, or matrix.  However, as we have introduced typed formal    */
    /* arguments, we are implementing them all as matricies.  The associated */
    /* matrix vocab elements of all but the matrix type are fixed length     */
    /* system matricies, and thus may not be edited by the user.             */
    /*                                                                       */
    /* In addition to being used to implement the usual user columns,        */
    /* DataColumns will probably also be used to store input and output from */
    /* reports.                                                              */
    /*                                                                       */
    /* Reference columns contain reference cells, which are just references  */
    /* to data cells.  They allow cells from different Data columns to be    */
    /* mirrored in a single reference column.  They will probably be used    */
    /* display purposes, and to construct input for some reports.            */
    /*                                                                       */
    /* The following methods support the management of columns:              */
    /*                                                                       */
    /*      addColumn()                                                      */
    /*          addDataColumn() -- internal use only                         */
    /*          addReferenceColumn() -- internal use only                    */
    /*                                                                       */
    /*      colNameInUse()                                                   */
    /*                                                                       */
    /*      getColumn(id)                                                    */
    /*      getColumn(name)                                                  */
    /*                                                                       */
    /*      getDataColumn(id)                                                */
    /*      getDataColumn(name)                                              */
    /*                                                                       */
    /*      getReferenceColumn(id)                                           */
    /*      getReferenceColumn(name)                                         */
    /*                                                                       */
    /*      getColumns()                                                     */
    /*      getDataColumns()                                                 */
    /*      getReferenceColumns()                                            */
    /*                                                                       */
    /*      removeColumn() -- target column must be empty                    */
    /*                                                                       */
    /*      replaceColumn()                                                  */
    /*                                                                       */
    /*************************************************************************/
    
    /**
     * addColumn()
     * 
     * Insert a copy of the supplied Column into the column list, and return 
     * its ID.
     *
     * The column must not have an ID assigned to it, and must have a valid
     * name that is not in use.  The column must be empty, and must have been
     * created for this database.
     *
     * If the column is a DataColumn, its type must be set.  Verify that the 
     * vocab list does not contain a MatrixVocabElement defining the syntax 
     * of the columns cells.  Then create a default initial MatrixVocabElement
     * as appropriate for the type of the DataColumn.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public long addColumn(Column col)
        throws SystemErrorException
    {
        final String mName = "Database::addColumn(col): ";
        long newColID = DBIndex.INVALID_ID;
        
        if ( col == null )
        {
            throw new SystemErrorException(mName + "col == null");
        }
        else if ( col.getDB() != this )
        {
            throw new SystemErrorException(mName + "db mismatch");
        }
        else if ( col.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "col.id != INVALID_ID");
        }
        else if ( ( ! ( this.IsValidSVarName(col.getName()) ) ) ||
                  ( this.vl.inVocabList(col.getName()) ) ||
                  ( this.cl.inColumnList(col.getName()) ) )
        {
            throw new SystemErrorException(mName + "col.name invalid or in use");
        }
        
        if ( col instanceof DataColumn )
        {
            newColID = this.addDataColumn(new DataColumn((DataColumn)col));
        }
        else if ( col instanceof ReferenceColumn )
        {
            newColID = this.addReferenceColumn(
                    new ReferenceColumn((ReferenceColumn)col));
        }
        else
        {
            throw new SystemErrorException(mName + "Unknown Column subclass");
        }
        
        return newColID;
        
    } /* Database::addColumn(col) */
    
    
    /**
     * addDataColumn()
     *
     * Given an instance of DataColumn with a valid name and type set, but 
     * no associated MatrixVocabElement or ID, construct an initial 
     * MatrixVocabElement for the DataColumn, and insert it into the vocab
     * list.  Then insert the column into the column list (and in passing, the
     * index), and return the newly assigned ID of the column.
     *
     * Before doing this, verify that the type of the DataColumn has been set,
     * and that the itsMveID field is set to the INVALID_ID.  No need to verify
     * that the column name is valid and not in use, as that has been checked
     * already.
     *
     *                                                  JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    private long addDataColumn(DataColumn dc)
        throws SystemErrorException
    {
        final String mName = "Database::addDataColumn(dc): ";
        long colID = DBIndex.INVALID_ID;
        long mveID = DBIndex.INVALID_ID;
        MatrixVocabElement mve = null;
        
        if ( ( dc == null ) ||
             ( dc.getDB() != this ) ||
             ( dc.getID() != DBIndex.INVALID_ID ) )
        {
            throw new SystemErrorException(mName + "bad dc param on entry");
        }
        else if ( ( ! ( this.IsValidSVarName(dc.getName() ) ) ) ||
                  ( this.vl.inVocabList(dc.getName()) ) ||
                  ( this.cl.inColumnList(dc.getName()) ) )
        {
            throw new SystemErrorException(mName + "dc.name invalid or in use");
        }
        else if ( dc.getItsMveID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "dc.itsMveID != INVALID_ID");
        }
        else if ( dc.getItsMveType() == MatrixVocabElement.matrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "dc.insMveType undefined");
        }
        else if ( dc.getItsCells() != null )
        {
            throw new SystemErrorException(mName +
                                           "dc.itsCells aleady defined?");
        }
        
        mve = dc.constructInitMatrixVE();
        
        this.vl.addElement(mve);
        
        mveID = mve.getID();
        
        if ( ( this.vl.getVocabElement(mveID) != mve ) ||
             ( this.vl.getVocabElement(dc.getName()) != mve ) )
        {
            throw new SystemErrorException(mName + 
                                           "mve insertion in vl failed?");
        }
        
        dc.setItsMveID(mveID);
        
        this.cl.addColumn(dc);
        
        colID = dc.getID();

        if ( ( this.cl.getColumn(colID) != dc ) ||
             ( this.cl.getColumn(dc.getName()) != dc ) ||
             ( dc.getItsCells() == null ) )
        {
            throw new SystemErrorException(mName + "dc insertion in cl failed");
        }
        
        mve.setItsColID(colID);
        
        /* If the type of the DataColumn is anything other than MATRIX, the 
         * associated MatrixVocabElement must not be editable by the user.
         * Ensure this by setting the system flag on the MVE.
         */
        if ( dc.getItsMveType() != MatrixVocabElement.matrixType.MATRIX )
        {
            mve.setSystem();
        }
        
        return colID;
        
    } /* Database::addDataColumn(dc) */
    
    
    /**
     * addReferenceColumn()
     *
     * Given an instance of a ReferenceColumn with a valid name, insert it into 
     * the column list (and in passing, the index), and return the newly 
     * assigned ID of the column.
     *
     *                                                  JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    private long addReferenceColumn(ReferenceColumn rc)
        throws SystemErrorException
    {
        final String mName = "Database::addReferenceColumn(rc): ";
        long colID = DBIndex.INVALID_ID;
        long mveID = DBIndex.INVALID_ID;
        
        if ( ( rc == null ) ||
             ( rc.getDB() != this ) ||
             ( rc.getID() != DBIndex.INVALID_ID ) )
        {
            throw new SystemErrorException(mName + "bad rc param on entry");
        }
        else if ( ( ! ( this.IsValidSVarName(rc.getName() ) ) ) ||
                  ( this.vl.inVocabList(rc.getName()) ) ||
                  ( this.cl.inColumnList(rc.getName()) ) )
        {
            throw new SystemErrorException(mName + "rc.name invalid or in use");
        }
        else if ( rc.getItsCells() != null )
        {
            throw new SystemErrorException(mName +
                                           "rc.itsCells aleady defined?");
        }
                
        this.cl.addColumn(rc);
        
        colID = rc.getID();
        
        if ( ( this.cl.getColumn(colID) != rc ) ||
             ( this.cl.getColumn(rc.getName()) != rc ) ||
             ( rc.getItsCells() == null ) )
        {
            throw new SystemErrorException(mName + "dc insertion in cl failed");
        }
        
        return colID;
        
    } /* Database::addReferenceColumn(rc) */
    
    
    /**
     * colNameInUse(name)
     *
     * Test to see if a column name is in use.  Return true if it is, and false
     * if it isn't.  Throw a system error if the name is invalid.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean colNameInUse(String name)
        throws SystemErrorException
    {
        final String mName = "Database::colNameInUse(name): ";
        boolean nameInUse = false;
        
        if ( name == null )
        {
            throw new SystemErrorException(mName + "name == null");
        }
        else if ( ! ( this.IsValidSVarName(name) ) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }
        else if ( ( this.vl.inVocabList(name) ) ||
                  ( this.cl.inColumnList(name) ) )
        {
            nameInUse = true;
        }
        
        return nameInUse;
        
    } /* DataBase::colNameInUse(name) */
    
    
    /**
     * getColumn(id)
     *
     * Given a column ID, try to look up the associated column in the 
     * column list, and return a copy of its DataColumn or ReferenceColumn
     * structure, but with itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public Column getColumn(long id)
        throws SystemErrorException
    {
        final String mName = "Database::getColumn(id): ";
        Column col = null;
        Column copy = null;
        
        col = this.cl.getColumn(id);
        
        if ( col instanceof DataColumn )
        {
            copy = new DataColumn((DataColumn)col);
        }
        else if ( col instanceof ReferenceColumn )
        {
            copy = new ReferenceColumn((ReferenceColumn)col);
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subtype?");
        }
        
        return copy;
        
    } /* Database::getColumn(id) */
    
    
    /**
     * getColumn(name)
     *
     * Given a column name, try to look up the associated column in the 
     * column list, and return a copy of its DataColumn or ReferenceColumn
     * structure, but with itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public Column getColumn(String name)
        throws SystemErrorException
    {
        final String mName = "Database::getColumn(name): ";
        Column col = null;
        Column copy = null;
        
        col = this.cl.getColumn(name);
        
        if ( col instanceof DataColumn )
        {
            copy = new DataColumn((DataColumn)col);
        }
        else if ( col instanceof ReferenceColumn )
        {
            copy = new ReferenceColumn((ReferenceColumn)col);
        }
        else
        {
            throw new SystemErrorException(mName + "unknown Column subtype?");
        }
        
        return copy;
        
    } /* Database::getColumn(name) */
    
    
    /**
     * getDataColumn(id)
     *
     * Given a data column ID, try to look up the associated data column in the 
     * column list, and return a copy of its DataColumn structure, but with 
     * itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public DataColumn getDataColumn(long id)
        throws SystemErrorException
    {
        final String mName = "Database::getDataColumn(id): ";
        Column col = null;
        DataColumn dc = null;
        
        col = this.cl.getColumn(id);
        
        if ( ! ( col instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't map to a DataColumn");
        }
        
        dc = new DataColumn((DataColumn)col);
        
        return dc;
        
    } /* Database::getDataColumn(id) */
    
    
    /**
     * getDataColumn(name)
     *
     * Given a data column name, try to look up the associated dat column in the 
     * column list, and return a copy of its DataColumn structure, but with 
     * itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public DataColumn getDataColumn(String name)
        throws SystemErrorException
    {
        final String mName = "Database::getDataColumn(name): ";
        Column col = null;
        DataColumn dc = null;
        
        col = this.cl.getColumn(name);
        
        if ( ! ( col instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "name doesn't map to a data column");
        }
        
        dc = new DataColumn((DataColumn)col);
        
        return dc;
        
    } /* Database::getDataColumn(name) */
    
    
    /**
     * getReferenceColumn(id)
     *
     * Given a reference column ID, try to look up the associated reference 
     * column in the column list, and return a copy of its ReferenceColumn 
     * structure, but with itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public ReferenceColumn getReferenceColumn(long id)
        throws SystemErrorException
    {
        final String mName = "Database::getReferenceColumn(id): ";
        Column col = null;
        ReferenceColumn rc = null;
        
        col = this.cl.getColumn(id);
        
        if ( ! ( col instanceof ReferenceColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't map to a ReferenceColumn");
        }
        
        rc = new ReferenceColumn((ReferenceColumn)col);
        
        return rc;
        
    } /* Database::getDataColumn(id) */
    
    
    /**
     * getReferenceColumn(name)
     *
     * Given a reference column name, try to look up the associated refernce
     * column in the column list, and return a copy of its ReferenceColumn 
     * structure, but with itsCells set to null.
     *
     * If no such column exists, throw a system error.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public ReferenceColumn getReferenceColumn(String name)
        throws SystemErrorException
    {
        final String mName = "Database::getReferenceColumn(name): ";
        Column col = null;
        ReferenceColumn rc = null;
        
        col = this.cl.getColumn(name);
        
        if ( ! ( col instanceof ReferenceColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "name doesn't map to a data column");
        }
        
        rc = new ReferenceColumn((ReferenceColumn)col);
        
        return rc;
        
    } /* Database::getDataColumn(name) */
    
    
    /**
     * getColumns()
     *
     * Return a vector containing copies of the DataColumn or ReferenceColumn
     * classes of each column in the column list, but with the itsCells fields
     * set to null.
     *
     * If there are no Columns, return null.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None,
     */
    
    public java.util.Vector<Column> getColumns()
        throws SystemErrorException
    {
        
        return this.cl.getColumns();
        
    } /* Database::getColumns() */
   
    
    /**
     * getDataColumns()
     *
     * Return a vector containing copies of the DataColumn classes of each 
     * data column in the column list, but with the itsCells fields
     * set to null.
     *
     * If there are no DataColumns, return null.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None,
     */
    
    public java.util.Vector<DataColumn> getDataColumns()
        throws SystemErrorException
    {
        
        return this.cl.getDataColumns();
        
    } /* Database::getDataColumns() */
   
    
    /**
     * getReferenceColumns()
     *
     * Return a vector containing copies of the ReferenceColumn classes of each 
     * reference column in the column list, but with the itsCells fields
     * set to null.
     *
     * If there are no ReferenceColumns, return null.
     *
     *                                              JRM -- 8/31/07
     *
     * Changes:
     *
     *    - None,
     */
    
    public java.util.Vector<ReferenceColumn> getReferenceColumns()
        throws SystemErrorException
    {
        
        return this.cl.getReferenceColumns();
        
    } /* Database::getReferenceColumns() */
    
    
    /**
     * removeColumn()
     *
     * Given the ID of a column, attempt to remove it from the column list
     * (and thereby from the database as a whole).  Note that a column must be
     * empty (i.e. have no cells), before it can be removed.
     *
     * If the Column is a DataColumn, also remove the MatrixVocabElement 
     * associated with the DataColumn.
     *
     *                                              JRM -- 8/31/07
     *
     * Changed:
     *
     *    - None.
     */
    
    public void removeColumn(long id)
        throws SystemErrorException
    {
        final String mName = "Database::removeColumn(id): ";
        long mveID = DBIndex.INVALID_ID;
        Column col = null;
        DataColumn dc = null;
        MatrixVocabElement mve = null;
        
        if ( id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "id == INVALID_ID");
        }
        
        col = this.cl.getColumn(id);
        
        if ( col.getNumCells() != 0 )
        {
            throw new SystemErrorException(mName + "col.numCells != 0");
        }
        
        if ( col instanceof DataColumn )
        {
            dc = (DataColumn)col;
            mveID = dc.getItsMveID();
            
            if ( mveID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                                               "dc.itsMveID == INVALID_ID");
            }
            else if ( ! this.vl.matrixInVocabList(mveID) )
            {
                throw new SystemErrorException(mName + 
                        "dc.itsMveID doesn't refer to a matrix vocab element");
            }
            
            mve = this.vl.getMatrixVocabElement(mveID);
            
            if ( mve.getItsColID() != id )
            {
                throw new SystemErrorException(mName + "mve.istColID != id");
            }
        }
        
        this.cl.removeColumn(id);
        
        if ( mveID != DBIndex.INVALID_ID )
        {
            this.vl.removeVocabElement(mveID);
        }

        return;
                
    } /* Database::removeColumn(id) */
    
    
    /**
     * replaceColumn()
     *
     * Given an instance of DataColumn or ReferenceColumn with ID matching
     * that of a column in the column list, replace the current verion of 
     * the (Data or Reference) column with a copy of the supplied (Data or
     * Reference) column.
     *
     * At present, the itsMveID and itsMveType fields of the supplied 
     * DataColumn must match that of the original instance of DataColumn.
     * We will probably want to relax this in the future, but we will keep
     * this restriction for now.
     *
     *                                                 JRM -- 8/31/07
     *
     * Changes:
     *
     *    - Modified method to check for name changes in a DataColumn.  If 
     *      the name of the DataColumn has been changed, apply the name change
     *      to the associated MVE first, so as to avoid the one change at a 
     *      time invarient.  Then check to see if there are any remaining 
     *      changes.  If there are, proceed as before.
     */ 
    
    public void replaceColumn(Column newCol)
        throws SystemErrorException
    {
        final String mName = "Database::replaceColumn(): ";
        long colID;
        Column oldCol;
        
        if ( newCol == null )
        {
            throw new SystemErrorException(mName + "newCol == null");
        }
        else if ( newCol.getDB() != this )
        {
            throw new SystemErrorException(mName + "db mismatch");
        }
        else if ( newCol.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "newCol.id == INVALID_ID");
        }
        
        colID = newCol.getID();
        
        oldCol = this.cl.getColumn(colID);
        
        if ( oldCol.getName().compareTo(newCol.getName()) != 0 )
        {
            /* we have a name change -- verify that the new name is valid 
             * and not in use.
             */
            if ( ( ! ( this.IsValidSVarName(newCol.getName() ) ) ) ||
                  ( this.vl.inVocabList(newCol.getName()) ) ||
                  ( this.cl.inColumnList(newCol.getName()) ) )
            {
                throw new SystemErrorException(mName + 
                        "newCol.name modified and either invalid or in use");
            }
        }
        
        if ( oldCol instanceof DataColumn )
        {
            DataColumn newDC;
            DataColumn oldDC;
            long mveID;
            MatrixVocabElement mve;
            MatrixVocabElement newMve;
            
            if ( ! ( newCol instanceof DataColumn ) )
            {
                throw new SystemErrorException(mName + 
                        "col type mismatch -- DataColumn expected");
            }
            
            newDC = (DataColumn)newCol;
            oldDC = (DataColumn)oldCol;
            
            if ( newDC.getItsMveID() == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                        "newDC.itsMveID == INVALID_ID");
            }
            else if ( newDC.getItsMveID() != oldDC.getItsMveID() )
            {
                throw new SystemErrorException(mName + 
                        "newDC.itsMveID != oldC.itsMveID");
            }
            else if ( newDC.getItsMveType() == 
                      MatrixVocabElement.matrixType.UNDEFINED )
            {
                throw new SystemErrorException(mName + 
                        "newDC.itsMveType == UNDEFINED");
            }
            else if ( newDC.getItsMveType() != oldDC.getItsMveType() )
            {
                throw new SystemErrorException(mName + 
                        "newDC.itsMveType != oldC.itsMveType");
            }
            
            mveID = oldDC.getItsMveID();
            mve = this.vl.getMatrixVocabElement(mveID);
            
            if ( mve.getName().compareTo(oldDC.getName()) != 0 )
            {
                System.out.printf("mve.getName() = %s\n", mve.getName());
                System.out.printf("oldDC.getName() = %s\n", oldDC.getName());
                throw new SystemErrorException(mName + 
                                               "oldDC.name != mve.name");
            }
            
            if ( oldDC.getName().compareTo(newDC.getName()) != 0 )
            {
                /* the user has changed the name of the DataColumn.  We must
                 * apply this change to the associated matrix vocab element
                 * as well.
                 */
                
                newMve = new MatrixVocabElement(mve);
                newMve.setName(newDC.getName());
                this.vl.replaceVocabElement(newMve);
                
                if ( ( this.vl.getMatrixVocabElement(mveID) != newMve ) ||
                     ( this.vl.getMatrixVocabElement(newDC.getName()) != newMve ) )
                {
                    throw new SystemErrorException(mName + 
                            "failure updating matrix for data col name change");
                }
            }
            
            this.cl.replaceDataColumn(new DataColumn(newDC), false);
        }
        else if ( oldCol instanceof ReferenceColumn )
        {
            ReferenceColumn newRC;
            
            if ( ! ( newCol instanceof ReferenceColumn ) )
            {
                throw new SystemErrorException(mName + 
                        "col type mismatch -- ReferenceColumn expected");
            }
            
            newRC = (ReferenceColumn)newCol;
            
            this.cl.replaceReferenceColumn(new ReferenceColumn(newRC));
        }
        else
        {
            throw new SystemErrorException(mName + "Unknow Column subclass?!?");
        }
        
        return;        
        
    } /* Database::replaceColumn(newCol) */
    
    
    /*************************************************************************/
    /************************ Listener Management ****************************/
    /*************************************************************************/
    
    /**
     * cascadeEnd()
     *
     * Note the end of a cascade of changes.  Note that such cascades
     * may be nested.
     *                                          JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void cascadeEnd()
        throws SystemErrorException
    {
        this.listeners.notifyListenersOfCascadeEnd();
        
        return;
        
    } /* Database::cascadeEnd() */
    
    
    /**
     * cascadeStart()
     *
     * Note the beginning of a cascade of changes.  Note that such cascades
     * may be nested.
     *                                          JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void cascadeStart()
        throws SystemErrorException
    {
        this.listeners.notifyListenersOfCascadeBegin();
        
        return;
        
    } /* Database::cascadeStart() */
    
    
    /**
     * deregisterCascadeListener()
     * 
     * Deregister a cascade listener.  The listener must implement the
     * ExternalCascadeListener interface, and must be registered with the 
     * Database on entry. 
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterCascadeListener(ExternalCascadeListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterCascadeListener()";
        
        this.listeners.deregisterExternalListener(el);
        
        return;
        
    } /* Database::deregisterCascadeListener() */
    
    
    /**
     * deregisterInternalCascadeListener()
     * 
     * Deregister an internal cascade listener.  The listener must implement the
     * ExternalCascadeListener interface, and must be registered with the 
     * Database on entry. 
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterInternalCascadeListener(long id)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterInternalCascadeListener()";
        
        this.listeners.deregisterInternalListener(id);
        
        return;
        
    } /* Database::deregisterInternalCascadeListener() */
    
    
    /**
     * deregisterColumnListListener()
     * 
     * Deregister a ColumnList listener.  The listener must implement the
     * ExternalColumnListListener interface, and must be registered with the 
     * column list on entry. 
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterColumnListListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterColumnListListener()";
        
        this.cl.deregisterExternalListener(el);
        
        return;
        
    } /* Database::deregisterColumnListListener() */
    

    /**
     * deregisterDataCellListener()
     * 
     * Deregister a DataCell listener.  The listener must implement the
     * ExternalDataCellListener interface, and must be registered with the 
     * target on entry. 
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterDataCellListener(long id, 
                                           ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterDataCellListener()";
        DBElement dbe;
        DataCell dc;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a DataCell");
        }
        
        dc = (DataCell)dbe;
        
        dc.deregisterExternalListener(el);
        
        return;
        
    } /* Database::deregisterDataCellListener() */
    

    /**
     * deregisterDataColumnListener()
     * 
     * Deregister a DataColumn listener.  The listener must implement the
     * ExternalDataColumnListener interface, and must be registered with the 
     * target on entry. 
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterDataColumnListener(long id, 
                                             ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterDataColumnListener()";
        DBElement dbe;
        DataColumn dc;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a DataColumn");
        }
        
        dc = (DataColumn)dbe;
        
        dc.deregisterExternalListener(el);
        
        return;
        
    } /* Database::deregisterDataColumnListener() */
    

    /**
     * deregisterVocabElementListener()
     * 
     * Deregister a vocab element listener.  The listener must implement the
     * ExternalVocabElementListener interface, and must be registered with the 
     * target on entry. 
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterVocabElementListener(long id, 
                                               ExternalVocabElementListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterVocabElementListener()";
        DBElement dbe;
        VocabElement ve;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof VocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a VocabElement");
        }
        
        ve = (VocabElement)dbe;
        
        ve.deregisterExternalListener(el);
        
        return;
        
    } /* Database::deregisterVocabElementListener() */
    
    
    /**
     * deregisterVocabListListener()
     * 
     * Deregister a vocab list change listener.  The listener must implement the
     * ExternalVocabListListener interface, and must be registered with the 
     * vocab list on entry. 
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void deregisterVocabListListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "Database::deregisterVocabListListener()";
        
        this.vl.deregisterExternalChangeListener(el);
        
        return;
        
    } /* Database::deregisterVocabListListener() */
    
    
    /**
     * registerCascadeListener()
     * 
     * Register a cascade listener.  The listener must implement the
     * ExternalCascadeListener interface.  The listener will be informed
     * of the beginning and end of cascades of changes.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerCascadeListener(ExternalCascadeListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerCascadeListener()";
        
        this.listeners.registerExternalListener(el);
        
        return;
        
    } /* Database::registerCascadeListener() */
    
    
    /**
     * registerColumnListListener()
     * 
     * Register a cascade listener.  The listener must implement the
     * ExternalCascadeListener interface.  The listener will be informed
     * of the beginning and end of cascades of changes.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerColumnListListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerColumnListListener()";
        
        this.cl.registerExternalListener(el);
        
        return;
        
    } /* Database::registerColumnListListener() */
    
    
    /**
     * registerDataCellListener()
     * 
     * Register a DataCell listener.  The listener must implement the
     * ExternalDataCellListener interface.  The listener will be informed
     * of changes in and deletions of data cells in the target column.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerDataCellListener(long id, 
                                         ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerDataCellListener()";
        DBElement dbe;
        DataCell dc;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a DataCell");
        }
        
        dc = (DataCell)dbe;
        
        dc.registerExternalListener(el);
        
        return;
        
    } /* Database::registerDataCellListener() */
    
    
    /**
     * registerDataColumnListener()
     * 
     * Register a DataColumn listener.  The listener must implement the
     * ExternalDataColumnListener interface.  The listener will be informed
     * of changes in and deletions of data cells in the target column.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerDataColumnListener(long id, 
                                           ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerDataColumnListener()";
        DBElement dbe;
        DataColumn dc;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a DataColumn");
        }
        
        dc = (DataColumn)dbe;
        
        dc.registerExternalListener(el);
        
        return;
        
    } /* Database::registerDataColumnListener() */
    
    
    /**
     * registerInternalCascadeListener()
     * 
     * Register an internal cascade listener.  The listener must implement the
     * InternalCascadeListener interface.  The listener will be informed
     * of the beginning and end of cascades of changes.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerInternalCascadeListener(long id)
        throws SystemErrorException
    {
        final String mName = "Database::registerInternalCascadeListener()";
        
        this.listeners.registerInternalListener(id);
        
        return;
        
    } /* Database::registerInternalCascadeListener() */
    
    
    /**
     * registerVocabElementListener()
     * 
     * Register a vocab element listener.  The listener must implement the
     * ExternalVocabElementListener interface.  The listener will be informed
     * of changes in and deletions of vocab elements.
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerVocabElementListener(long id, 
                                             ExternalVocabElementListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerVocabElementListener()";
        DBElement dbe;
        VocabElement ve;
        
        dbe = this.idx.getElement(id);
        
        if ( ! ( dbe instanceof VocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "id doesn't refer to a VocabElement");
        }
        
        ve = (VocabElement)dbe;
        
        ve.registerExternalListener(el);
        
        return;
        
    } /* Database::registerVocabElementListener() */
    
    
    /**
     * registerVocabListListener()
     * 
     * Register a vocab list listener.  The listener must implement the
     * ExternalVocabListListener interface.  The listener will be informed
     * of the insertion and deletion of vocab element into and from the 
     * vocab list.
     * 
     *                                          JRM -- 2/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void registerVocabListListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "Database::registerVLListener()";
        
        this.vl.registerExternalListener(el);
        
        return;
        
    } /* Database::registerVocabListListener() */
    
    
    /*************************************************************************/
    /*********************** Supported Features: *****************************/
    /*************************************************************************/
    /*                                                                       */
    /* The supported features methods are used to indicate which database    */
    /* features are supported by each subclass of Database.  Subclasses      */
    /* must override these methods as required.                              */
    /*                                                                       */
    /* Expect this set of methods to expand as the set of subclasses of      */
    /* Database expands.                                                     */
    /*                                                                       */
    /*************************************************************************/
    
    public boolean floatSubrangeSupported()         { return true; }
    public boolean integerSubrangeSupported()       { return true; }
    public boolean nominalSubrangeSupported()       { return true; }
    public boolean predSubrangeSupported()          { return true; }
    public boolean readOnly()                       { return false; }
    public boolean tickSizeAgjustmentSupported()    { return true; }
    public boolean typedFormalArgsSupported()       { return true; }
    
    
    /*************************************************************************/
    /********************** Vocab List Management ****************************/
    /*************************************************************************/
    /*                                                                       */
    /* The Vocab list is implemented as a single class, as both matricies    */
    /* (i.e. column variables) and predicates are vocab elements, and share  */
    /* a name space.                                                         */
    /*                                                                       */
    /* This shared name space is necessary for the old MacSHAPA query        */
    /* language, but we shouldn't need it elsewhere.                         */
    /*                                                                       */
    /* Thus, while I have implemented query list management methods that     */
    /* deal with instances of VocabElement, we shouldn't have to use them    */
    /* outside a possible implementation of the old MacSHAPA query language  */
    /* and associated editors.                                               */
    /*                                                                       */
    /* For all other purposes, I have implemented methods that deal with     */
    /* PredicateVocabElements or MatrixVocabElements.  Use these unless the  */
    /* more general methods are absolutely necessary.  This will make it     */
    /* easier to put predicates and matricies (i.e. column variables) in     */
    /* separate name spaces should we ever wish to.                          */
    /*                                                  JRM -- 6/11/07       */
    /*                                                                       */
    /* MatrixVocabElement based methods:                                     */
    /*                                                                       */
    /*      addMatrixVE(mve)  -- internal use only                           */
    /*      getMatrixVE(id)                                                  */
    /*      getMatrixVE(name)                                                */
    /*      getMatrixVEs()                                                   */
    /*      matrixNameInUse(name)                                            */
    /*      matrixVEExists(id)                                               */
    /*      matrixVEExists(name)                                             */
    /*      removeMatrixVE(id)  -- internal use only                         */
    /*      replaceMatrixVE(mve)                                             */
    /*                                                                       */
    /*                                                                       */
    /* PredicateVocabElement based methods:                                  */
    /*                                                                       */
    /*      addPredVE(pve)                                                   */
    /*      getPredVE(id)                                                    */
    /*      getPredVE(name)                                                  */
    /*      getPredVEs()                                                     */
    /*      predNameInUse(name)                                              */
    /*      predVEExists(id)                                                 */
    /*      predVEExists(name)                                               */
    /*      removePredVE(id)                                                 */
    /*      replacePredVE(pve)                                               */
    /*                                                                       */
    /*                                                                       */
    /* VocabElement based methods (use only when necessary):                 */
    /*                                                                       */
    /*      getVocabElement(id)                                              */
    /*      getVocabElement(name)                                            */
    /*      vocabElementExists(id)                                           */
    /*      vocabElementExists(name)                                         */
    /*                                                                       */
    /*************************************************************************/
    
    /*** MatrixVocabElement methods ***/
    
    /**
     * addMatrixVE(mve)
     *
     * Given a MatrixVocabElement, make a copy, add the copy to the 
     * vocab list and index, and return the id assigned to the copy. 
     * Throws a system error if any errors are detected.
     *
     * This method is private, and is used mostly for testing.  As matricies
     * are created as part of columns, there is no need for this routine
     * outside the database code.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    private long addMatrixVE(MatrixVocabElement mve)
        throws SystemErrorException
    {
        final String mName = "Database::addMatrixVE(mve): ";
        MatrixVocabElement local_mve = null;

        if ( mve == null )
        {
            throw new SystemErrorException(mName + "null mve.");
        }
        else if ( ! ( mve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                           "mve not a MatrixVocabElement");
        }
        else if ( (local_mve = new MatrixVocabElement(mve)) == null )
        {
            throw new SystemErrorException(mName + "couldn't copy mve");
        }
        else
        {
            this.vl.addElement(local_mve);
        }
        
        return local_mve.getID();
        
    } /* Database::addMatrixVE(mve) */
    
    
    /**
     * getMatrixVE(id)
     *
     * Given an matrix vocab element ID, return a copy of the associated 
     * MatrixVocabElement.  Throws a system error if no such 
     * MatrixVocabElement exists.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public MatrixVocabElement getMatrixVE(long targetID)
        throws SystemErrorException
    {
        final String mName = "Database::getMatrixVE(id): ";
        VocabElement ve = null;
        
        ve = this.vl.getVocabElement(targetID);
        
        if ( ! ( ve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                          "target not a MatrixVocabElement");
        }
        
        return new MatrixVocabElement((MatrixVocabElement)ve);
    
    } /* Database::getMatrixVE(id) */
    
    
    /**
     * getMatrixVE(name)
     *
     * Given an matrix vocab element name, return a copy of the associated 
     * MatrixVocabElement.  Throws a system error if no such 
     * MatrixVocabElement exists.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public MatrixVocabElement getMatrixVE(String targetName)
        throws SystemErrorException
    {
        final String mName = "Database::getMatrixVE(name): ";
        VocabElement ve = null;
        
        ve = this.vl.getVocabElement(targetName);
        
        if ( ! ( ve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                          "target not a MatrixVocabElement");
        }
        
        return new MatrixVocabElement((MatrixVocabElement)ve);
    
    } /* Database::getMatrixVE(name) */
    
    
    /**
     * getMatrixVEs()
     *
     * If the vocab list contains any non-system matricies of type 
     * matrixType.MATRIX, construct a vector containing copies of all such
     * entries, and return it.  If there are no such entries, return null.
     *
     *                                              JRM -- 6/18/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public java.util.Vector<MatrixVocabElement> getMatrixVEs()
        throws SystemErrorException
    {
        return this.vl.getMatricies();
    }
    
    
    /**
     * matrixNameInUse(name)
     *
     * Given a valid matrix (i.e. column variable) name, return true if it is
     * in use, and false if it is not.  Throws a system error on a null or 
     * invalid name.
     *                                              JRM -- 6/13/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean matrixNameInUse(String matrixName)
        throws SystemErrorException
    {
        return this.vl.inVocabList(matrixName);
    }
    
    
    /**
     * matrixVEExists(id)
     *
     * Given a matrix vocab element id, return true if the vocab list contains
     * a MatrixVocabElement with that id, and false otherwise.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean matrixVEExists(long targetID)
        throws SystemErrorException
    {
        return this.vl.matrixInVocabList(targetID);
    }
    
    
    /**
     * matrixVEExists(name)
     *
     * Given a matrix vocab element name, return true if the vocab list contains
     * a MatrixVocabElement with that name, and false otherwise.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean matrixVEExists(String targetName)
        throws SystemErrorException
    {
        return this.vl.matrixInVocabList(targetName);
    }
    
 
    /**
     * removeMatrixVE(id)
     *
     * Given a matrix vocab element id, remove the associated instance of
     * MatrixVocabElement from the vocab list.  Also delete the
     * MatrixVocabElement from the index, along with all of its formal
     * parameters.  Throws a system error if the taarget doesn't exist.
     *
     * This method is private, as matrix vocab elements are inserted and
     * deleted with columns.  Thus there should be no need of the method
     * outside the database code.
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */

    private void removeMatrixVE(long targetID)
        throws SystemErrorException
    {
        final String mName = "Database::removeMatrixVE(targetID): ";

        if ( ! matrixVEExists(targetID) )
        {
            throw new SystemErrorException(mName +
                                           "no such MatrixVocabElement");
        }

        this.vl.removeVocabElement(targetID);

    } /* Database::removeMatrixVE(id) */

    
    /**
     * replaceMatrixVE(mve)
     *
     * Given a (possibly modified) copy of a MatrixVocabElement that exists in
     * the vocab list, replace the old copy with a copy of the supplied 
     * MatrixVocabElement.  The old version is matched with the new via id.
     * Throws a system error if the old version doesn't exist.  Update the
     * index in passing, and adjust for changes in the formal argument list.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void replaceMatrixVE(MatrixVocabElement mve)
        throws SystemErrorException
    {
        final String mName = "Database::replaceMatrixVE(mve): ";
        MatrixVocabElement local_mve = null;
        
        if ( mve == null )
        {
            throw new SystemErrorException(mName + "mve == null");
        }
        else if ( ! ( mve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                           "mve not a MatrixVocabElement");
        }
        else if ( (local_mve = new MatrixVocabElement(mve)) == null )
        {
            throw new SystemErrorException(mName + "couldn't copy mve");
        }
        
        this.vl.replaceVocabElement(local_mve);
        
        return;
        
    } /* Database::replaceMatrixVE(mve) */
    
     
    /*** PredicateVocabElement methods ***/
    
    /**
     * addPredVE(mve)
     *
     * Given a PredicateVocabElement, make a copy and add the copy to the 
     * vocab list and index.  Return the ID assigned to the copy.
     * Throws a system error if any errors are detected.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public long addPredVE(PredicateVocabElement pve)
        throws SystemErrorException
    {
        final String mName = "Database::addPredVE(pve): ";
        PredicateVocabElement local_pve = null;

        if ( pve == null )
        {
            throw new SystemErrorException(mName + "null pve.");
        }
        else if ( ! ( pve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                           "pve not a PredicateVocabElement");
        }
        else if ( (local_pve = new PredicateVocabElement(pve)) == null )
        {
            throw new SystemErrorException(mName + "couldn't copy pve");
        }
        else
        {
            this.vl.addElement(local_pve);
        }
        
        return local_pve.getID();
        
    } /* Database::addPredVE(mve) */
    
    
    /**
     * getPredVE(id)
     *
     * Given an predicate vocab element ID, return a copy of the associated 
     * PredicateVocabElement.  Throws a system error if no such 
     * PredicateVocabElement exists.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public PredicateVocabElement getPredVE(long targetID)
        throws SystemErrorException
    {
        final String mName = "Database::getPredVE(id): ";
        VocabElement ve = null;
        
        ve = this.vl.getVocabElement(targetID);
        
        if ( ! ( ve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                          "target not a PredicateVocabElement");
        }
        
        return new PredicateVocabElement((PredicateVocabElement)ve);
    
    } /* Database::getPredVE(id) */
    
    
    /**
     * getPredVE(name)
     *
     * Given an predicate vocab element name, return a copy of the associated 
     * PredicateVocabElement.  Throws a system error if no such 
     * PredicateVocabElement exists.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public PredicateVocabElement getPredVE(String targetName)
        throws SystemErrorException
    {
        final String mName = "Database::getPredVE(name): ";
        VocabElement ve = null;
        
        ve = this.vl.getVocabElement(targetName);
        
        if ( ! ( ve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                          "target not a PredicateVocabElement");
        }
        
        return new PredicateVocabElement((PredicateVocabElement)ve);
    
    } /* Database::getPredVE(name) */
    
    
    /**
     * getPredVEs()
     *
     * If the vocab list contains any non-system predicates, construct a vector 
     * containing copies of all such entries, and return it.  If there are no 
     * such entries, return null.
     *
     *                                              JRM -- 6/18/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public java.util.Vector<PredicateVocabElement> getPredVEs()
        throws SystemErrorException
    {
        return this.vl.getPreds();
    }
     
    
    /**
     * predNameInUse(name)
     *
     * Given a valid predicate name, return true if it is in use, and false
     * if it is not.  Throws a system error on a null or invalid name.
     *
     *                                              JRM -- 6/13/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean predNameInUse(String predName)
        throws SystemErrorException
    {
        return this.vl.inVocabList(predName);
    }
    
    
    /**
     * predVEExists(id)
     *
     * Given a predicate vocab element id, return true if the vocab list 
     * contains a PredicateVocabElement with that id, and false otherwise.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean predVEExists(long targetID)
        throws SystemErrorException
    {
        return this.vl.predInVocabList(targetID);
    }
   
    
    /**
     * predVEExists(name)
     *
     * Given a predicate vocab element name, return true if the vocab list 
     * contain a PredicateVocabElement with that name, and false otherwise.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean predVEExists(String targetName)
        throws SystemErrorException
    {
        return this.vl.predInVocabList(targetName);
    }
    
    
    /**
     * removePredVE(id)
     *
     * Given a pred vocab element id, remove the associated instance of 
     * PredicateVocabElement from the vocab list.  Also delete the 
     * PredicateVocabElement from the index, along with all of its formal 
     * parameters.  Throws a system error if the target doesn't exist.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void removePredVE(long targetID)
        throws SystemErrorException
    {
        final String mName = "Database::removePredVE(targetID): ";
        
        if ( ! predVEExists(targetID) )
        {
            throw new SystemErrorException(mName + 
                                           "no such PredicateVocabElement");
        }
        
        this.vl.removeVocabElement(targetID);
        
    } /* Database::removePredVE(id) */
    
    
    /**
     * replacePredVE(mve)
     *
     * Given a (possibly modified) copy of a PredicateVocabElement that exists 
     * in the vocab list, replace the old copy with a copy of the supplied 
     * PredicateVocabElement.  The old version is matched with the new via id.
     * Throws a system error if the old version doesn't exist.  Update the
     * index in passing, and adjust for changes in the formal argument list.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void replacePredVE(PredicateVocabElement pve)
        throws SystemErrorException
    {
        final String mName = "Database::replacePredVE(pve): ";
        PredicateVocabElement local_pve = null;
        
        if ( pve == null )
        {
            throw new SystemErrorException(mName + "pve == null");
        }
        else if ( ! ( pve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                                           "pve not a PredicateVocabElement");
        }
        else if ( (local_pve = new PredicateVocabElement(pve)) == null )
        {
            throw new SystemErrorException(mName + "couldn't copy pve");
        }

        this.vl.replaceVocabElement(local_pve);
        
        return;
        
    } /* Database::replacePredVE(mve) */
    
    
   /*** VocabElement methods -- use only if type is unknown ***/
    
    /**
     * getVocabElement(id)
     *
     * Given a vocab element ID, return a copy of the associated vocab element.
     * Throws a system error if the target does not exist.
     *
     * Use this method only it the type (predicate or matrix) of the vocab
     * element is not know at the time of call.  This should seldom be the
     * case.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - none.
     */
    
    public VocabElement getVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "Database::getVocabElement(targetID): ";
        VocabElement ve;
        VocabElement ve_copy = null;
        
        ve = this.vl.getVocabElement(targetID);
        
        if ( ve == null )
        {
            throw new SystemErrorException(mName + 
                    "vl.getVocabElement() returned null");
        }

        if ( ve instanceof MatrixVocabElement )
        {
            ve_copy = new MatrixVocabElement((MatrixVocabElement)ve);
        }
        else if ( ve instanceof PredicateVocabElement )
        {
            ve_copy = new PredicateVocabElement((PredicateVocabElement)ve);
        }
        else
        {
            throw new SystemErrorException(mName + "Unknown ve type");
        }

        if ( ve_copy == null )
        {
            throw new SystemErrorException(mName + "can't copy ve");
        }
        
        return ve_copy;

    } /* Database::getVocabElement(targetID) */
    
    
    /**
     * getVocabElement(name)
     *
     * Given a vocab element name, return a copy of the associated vocab 
     * element.  Throws a system error if the target does not exist.
     *
     * Use this method only it the type (predicate or matrix) of the vocab
     * element is not know at the time of call.  This should seldom be the
     * case.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - none.
     */
    
    public VocabElement getVocabElement(String targetName)
       throws SystemErrorException
    {
        final String mName = "Database::getVocabElement(targetName): ";
        VocabElement ve;
        VocabElement ve_copy = null;
        
        ve = this.vl.getVocabElement(targetName);
        
        if ( ve == null )
        {
            throw new SystemErrorException(mName + 
                    "vl.getVocabElement() returned null");
        }
        
        if ( ve instanceof MatrixVocabElement )
        {
            ve_copy = new MatrixVocabElement((MatrixVocabElement)ve);
        }
        else if ( ve instanceof PredicateVocabElement )
        {
            ve_copy = new PredicateVocabElement((PredicateVocabElement)ve);
        }
        else
        {
            throw new SystemErrorException(mName + "Unknown ve type");
        }

        if ( ve_copy == null )
        {
            throw new SystemErrorException(mName + "can't copy ve");
        }
        
        return ve_copy;
        
    } /* Database::getVocabElement(targetName) */
    
    
    /**
     * vocabElementExists(id)
     *
     * Given a vocab element id, return true if a vocab element with that id
     * exists, and false otherwise.
     *
     * Use this method only it the type (predicate or matrix) of the vocab
     * element is not know at the time of call.  This should seldom be the
     * case.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - none.
     */
    
    public boolean vocabElementExists(long targetID)
       throws SystemErrorException
    {
        return this.vl.inVocabList(targetID);
    }
    
    
    /**
     * vocabElementExists(name)
     *
     * Given a vocab element name, return true if a vocab element with that 
     * name exists, and false otherwise.
     *
     * Use this method only it the type (predicate or matrix) of the vocab
     * element is not know at the time of call.  This should seldom be the
     * case.
     *
     *                                              JRM -- 6/12/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean vocabElementExists(String targetName)
       throws SystemErrorException
    {
        return this.vl.inVocabList(targetName);
    }
    
    
    /*** UID management ***/
    
    /**
     * isValidUID()
     *
     * Determine whether a user ID is valid.  Return true if it is, and
     * false otherwise.
     *
     * @param   uid:    User ID to be tested for validity
     *
     * @return  true if uid id valid, and false otherwise
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean isValidUID(int uid)
    {
        boolean isValid = true;
    
        /* TODO:
         *
         * For now, return true if the uid is non-negative.  
         *
         * Once we get the user table set up, return true iff uid is zero
         * or the uid appears in the user table.
         */
        
        if ( uid < 0 )
        {
            isValid = false;
        }
        
        return isValid;
        
    } /* Database::isValidUID(uid) */
    

    /*** Database element management ***/
//    
//    /**
//     * createCell()
//     *
//     * Creates a new cell in the given column.
//     * @param column the column in which to create the cell
//     * @return the newly created cell
//     */
//
//    public Cell createCell(Column column)
//    {
//        return (this.createCell(column.getID()));
//        
//    } /*  Database::createCell() */
//
//    
//    /**
//     * getCell() -- by column reference and cell ID
//     *
//     * Gets the cell associated with the given id in the given column
//     * @param column the column the cell is in
//     * @param cellID the id of the cell
//     * @return the cell associated with the given cell id
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//    
//    public Cell getCell(Column column, long cellID)
//    {
//        return (this.getCell(column.getID(), cellID));
//    
//    } /* Database::getCell() -- by column reference and cell ID */

     
//    /*** Listener Management ***/
//  
//    /**
//     * addChangeListener()
//     *
//     * Adds a database change listener
//     * @param listener the change listener to add
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//  
//    public void addChangeListener(DatabaseChangeListener listener)
//    {
//        this.changeListeners.add(listener);
//        
//        return;
//        
//    } /* Database::addChangeListener() */
//
//    /**
//     * removeChangeListener()
//     *
//     * Removes a database change listener
//     * @param listener the change listener to remove
//     *
//     * Changes:
//     *
//     *    - None
//     */
//    
//    public void removeChangeListener(DatabaseChangeListener listener)
//    {
//        this.changeListeners.remove(listener);
//        
//        return;
//        
//    } /* Database::removeChangeListener() */


    /*** Version String Construction ***/
    
    /**
     * getDBVersionString()
     *
     * Gets the database type and version string<br>
     * (eg ODB File v2.1)
     *
     * Changes:
     *
     *    - None.
     */
  
    public String getDBVersionString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getType());
        sb.append(" v");
        sb.append(this.getVersion());
    
        return (sb.toString());
    
    } /* Database::getDBVersionString() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/
      
    /**
     * IsGraphicalChar
     *
     * Test to see if the character passed in as a parameter is a graphical
     * character.  Return true if it is, and false otherwise.
     *
     * Eventually we will need to extend this method to work nicely with 
     * unicode, but for now, we will take a stict ASCII view of the issue.
     * Thus, for present purposes, a graphical character is a character with 
     * ASCII code 0x21 - 0x7E inclusive.
     *                                          JRM -- 1/23/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    protected static boolean IsGraphicalChar(char ch) {
        
        boolean retVal = false;
    
        if ( ( ch >= 0x21 ) && ( ch <= 0x7E ) ) {
            
            retVal = true;
        
        }
            
        return(retVal);
            
    } /* Database::IsGraphicalChar() */
    
    
    /**
     * IsValidFargName()
     * 
     * Test to see if a formal argument name is valid.  Return true if it
     * is, and false if it isn't.
     *
     * For now, use the old MacSHAPA definition of a formal argument name:
     *
     *  <graphic_char> --> ASCII codes 0x21 - 0x7E
     *
     *  <formal_arg_char> -->
     *      <graphic_char> - ( '(' | ')' | '<' | '>' | ',' | '"' )
     *
     *  <formal_arg> --> '<' (<formal_arg_char>)+ '>'
     *
     * At some point we will need to extend this to work nicely with unicode,
     * but we will stay with the old MacSHAPA definitions for now.  Anything
     * we do here will be an extension, so we shouldn't introduce any 
     * incompatibilities.
     *                                      JRM -- 1/23/07
     *
     * Changes:
     *
     *    - None.
     *      
     */

    public static boolean IsValidFargName(String name)
        throws SystemErrorException 
    {
   
        final String mName = "Database::IsValidFargName(): ";
        char ch;
        int i;
        int len;
        
        if ( name == null ) {
            
            throw new SystemErrorException(mName + "name null on entry.");
            
        } else if ( ! ( name instanceof String ) ) { 
        
            throw new SystemErrorException(mName + "name is not a string.");

        }
               
        len = name.length();
        
        if ( len <= 2 ) {
            
            // string is too short to be a valid formal argument name
            return false;
            
        } else if ( ( name.charAt(0) != '<' ) || 
                    ( name.charAt(len - 1) != '>' ) ) {
            
            // string either doesn't start with a '<' or doesn't end with '>'
            // Thus it is not a valid formal argument name.
            return false;
            
        } else {
            
            for ( i = 1; i < len - 1; i++ ) {
             
                ch = name.charAt(i);
                
                if ( ( ! IsGraphicalChar(ch)) ||
                     ( ch == '(' ) ||
                     ( ch == ')' ) ||
                     ( ch == '<' ) ||
                     ( ch == '>' ) ||
                     ( ch == ',' ) ||
                     ( ch == '"' ) ) {
                
                    return false;
                }
            }
        }
        
        return true;
        
    } /* Database::IsValidFargName() */
    
    
    /**
     * IsValidFloat()
     *
     * Test to see if the object passed in as a parameter is a Double that can
     * be used to replace a formal argument.
     *
     * Return true if it is, and false otherwise.
     *
     * The method name "IsValidFloat()" is a historical hold over from MacSHAPA.
     * Were it not for that issue, "IsValidDouble()" would make much more
     * sense.
     *
     *                                          JRM -- 2/7/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidFloat(Object o)
        throws SystemErrorException
    {
        final String mName = "Database::IsValidFoat(): ";
        
        if ( o == null ) 
        {
            throw new SystemErrorException(mName + "o null on entry.");
        }   
        
        if ( o instanceof Double )
        {
            return true;
        }
        else
        {
            return false;
        }
    } /* Database::IsValidFloat() */
    
     
    /**
     * IsValidInt()
     *
     * Test to see if the object passed in as a parameter is a Long that can
     * be used to replace a formal argument.
     *
     * Return true if it is, and false otherwise.
     *
     *                                          JRM -- 2/7/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidInt(Object o)
        throws SystemErrorException
    {
        final String mName = "Database::IsValidInt(): ";
        
        if ( o == null ) 
        {
            throw new SystemErrorException(mName + "o null on entry."); 
        }   
        
        if ( o instanceof Long )
        {
            return true;
        }
        else
        {
            return false;
        }
    } /* Database::IsValidInt() */

    
    /**
     * IsValidNominal()
     * 
     * Test to see if a string contains a valid nominal.  Return true if it
     * is, and false if it isn't.
     *
     * For now, we will use the old MacSHAPA definition of a nominal:
     *
     *  <graphic_char> --> ASCII codes 0x21 - 0x7E
     *
     *  <nominal_char> --> 
     *      ( ( <graphic_char> - ( '(' | ')' | '<' | '>' | ',' | '"' ) ) | ' '
     *
     *	<non_ws_nominal_char> --> <nominal_char> - ' '
     *
     *	<nominal> --> 
     *      <non_ws_nominal_char> [(<nominal_char>)* <non_ws_nominal_char>]
     *
     * Eventually we will have to extend this definition to make full use of
     * Unicode, but that can wait for now.
     *                                          JRM -- 1/24/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidNominal(Object obj)
        throws SystemErrorException
    {
   
        final String mName = "Database::IsValidNominal(): ";
        int len;
        
        
        if ( obj == null ) 
        {
            throw new SystemErrorException(mName + "obj null on entry.");
        } 
        else if ( ! ( obj instanceof String ) ) 
        { 
            return false;
        }
        
        String s = (String)obj;
               
        len = s.length();
        
        if ( len < 1 ) {
            
            // s is too short to be a valid nominal
            return false;
            
        } else if ( ( Character.isSpaceChar(s.charAt(0)) ) || 
                    ( Character.isSpaceChar(s.charAt(len - 1)) ) ) {
            
            // s either starts or ends with white space, and thus is 
            // not a valid nominal.
            return false;
            
        } else {
            
            char ch;
            int i;
            
            for ( i = 0; i < len; i++ ) {
             
                ch = s.charAt(i);
                
                if ( ! ( ( ch == ' ' ) 
                         ||
                         ( ( IsGraphicalChar(ch) ) 
                           &&
                           ( ch != '(' )
                           &&
                           ( ch != ')' )
                           &&
                           ( ch != '<' ) 
                           &&
                           ( ch != '>' ) 
                           &&
                           ( ch != ',' ) 
                           &&
                           ( ch != '"' ) 
                         )
                       )
                   ) {
                
                    // s contains a character that can't appear in a 
                    // nominal.
                    return false;
                }
            }
        }
        
        return true;
        
    } /* Database::IsValidNominal() */
 
    
    /**
     * IsValidPredName()
     * 
     * Test to see if a string contains a valid predicate name.  Return true 
     * if it does, and false if it isn't.
     *
     * For now, we will use the old MacSHAPA definition of a predicate name:
     *
     *  <graphic_char> --> ASCII codes 0x21 - 0x7E
     *
     *  <pred_name_char> --> 
     *      <graphic_char> - ( '(' | ')' | '<' | '>' | ',' | '"' )
     *
     *	<pred_name> --> (pred_name_char>)+
     *
     * Eventually we will have to extend this definition to make full use of
     * Unicode, but that can wait for now.
     *                                          JRM -- 1/24/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidPredName(String name)
        throws SystemErrorException 
    {
   
        final String mName = "Database::IsValidPredName(): ";
        int len;
        
        if ( name == null ) 
        {
            throw new SystemErrorException(mName + "name null on entry.");
        } 
        else if ( ! ( name instanceof String ) ) 
        { 
            throw new SystemErrorException(mName + "name is not a string.");
        }
               
        len = name.length();
        
        if ( len < 1 ) {
            
            // string is too short to be a valid predicate name
            return false;
                        
        } else {
            
            char ch;
            int i;
            
            for ( i = 0; i < len; i++ ) {
             
                ch = name.charAt(i);
                
                if ( ! ( ( IsGraphicalChar(ch) ) 
                         &&
                         ( ch != '(' )
                         &&
                         ( ch != ')' )
                         &&
                         ( ch != '<' ) 
                         &&
                         ( ch != '>' ) 
                         &&
                         ( ch != ',' ) 
                         &&
                         ( ch != '"' ) 
                       )
                   ) {
                
                    // string contains a character that can't appear in a 
                    // predicate name.
                    return false;
                }
            }
        }
        
        return true;
        
    } /* Database::IsValidPredName() */
 
    
    /**
     * IsValidSVarName()
     * 
     * Test to see if a string contains a valid spreadsheet variable name.  
     * Return true if it does, and false if it doesn't.
     *
     * For now, we will use the old MacSHAPA definition of a spreadsheet
     * variable name:
     *
     *  <graphic_char> --> ASCII codes 0x21 - 0x7E
     *
     *  <s_var_name_char> --> 
     *      ( ( <graphic_char> - ( '(' | ')' | '<' | '>' | ',' | '"' ) ) | ' '
     *
     *	<non_ws_s_var_name_char> --> <s_var_name_char> - ' '
     *
     *	<s_var_name> --> <non_ws_s_var_namel_char> 
     *                   [(<s_var_name_char>)* <non_ws_s_var_name_char>]
     *
     * Note that the definition of a spreadsheet variable name is identical
     * to that of a nominal.  However that may change, so we will maintain
     * a separate function to test for correctness.
     *
     * Eventually we will have to extend this definition to make full use of
     * Unicode, but that can wait for now.
     *                                          JRM -- 1/24/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidSVarName(String name)
        throws SystemErrorException 
    {
   
        final String mName = "Database::IsValidSVarName(): ";
        int len;
        
        if ( name == null ) {
            
            throw new SystemErrorException(mName + "name null on entry.");
            
        } else if ( ! ( name instanceof String ) ) { 
        
            throw new SystemErrorException(mName + "name is not a string.");

        }
               
        len = name.length();
        
        if ( len < 1 ) {
            
            // string is too short to be a valid spreadsheet variable name
            return false;
            
        } else if ( ( Character.isSpaceChar(name.charAt(0)) ) || 
                    ( Character.isSpaceChar(name.charAt(len - 1)) ) ) {
            
            // string either starts or ends with white space, and thus is 
            // not a valid spreadsheet variable name.
            return false;
            
        } else {
            
            char ch;
            int i;
            
            for ( i = 0; i < len; i++ ) {
             
                ch = name.charAt(i);
                
                if ( ! ( ( ch == ' ' ) 
                         ||
                         ( ( IsGraphicalChar(ch) ) 
                           &&
                           ( ch != '(' )
                           &&
                           ( ch != ')' )
                           &&
                           ( ch != '<' ) 
                           &&
                           ( ch != '>' ) 
                           &&
                           ( ch != ',' ) 
                           &&
                           ( ch != '"' ) 
                         )
                       )
                   ) {
                
                    // string contains a character that can't appear in a 
                    // spreadsheet variable name.
                    return false;
                }
            }
        }
        
        return true;
        
    } /* Database::IsValidSVarName() */
     
    
    /**
     * IsValidTextString()
     * 
     * Test to see if a string contains a valid text string -- that is a string
     * that can appear as the value of a cell in a text column variable.  
     * Return true if it does, and false if it doesn't.
     *
     * The old MacSHAPA definition of a text string is as follows:
     *
     *  <char> --> Any character in the standard roman character set,
     *		   hexadecimal values 0x00 to 0xFF.
     *
     *  <bs> --> back space (i.e. ASCII code 0x08)
     *
     *  <text_string_char> --> ( <char> - ( <bs> ) )
     *
     *  <text_string> --> (<text_string_char>)*
     *
     * Note that the MacSHAPA definition of the text string makes used of 
     * characters beyond 0x7F (the end point of the ASCII character set).
     * 
     * While we can hope that Java will use characters beyond the ASCII
     * character set uniformly across different platforms, at present I 
     * don't know how these characters will be managed.  Thus to begin with
     * I will redefine <char> as follows:
     *
     *   <char> --> Any character in the ASCII character set (hexadecimal
     *              values 0x00 to 0x7F)
     *
     * It is worth noting that the old MacSHAPA definition of a text string 
     * was driven by the character set used by the TextEdit utility provided
     * by MacOS.  I suspect that similar considerations will ultimately drive 
     * the definition of a text string in OpenSHAPA.
     *
     * Eventually we will have to extend this definition to make full use of
     * Unicode, but that can wait for now.
     *                                          JRM -- 1/25/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidTextString(Object obj)
        throws SystemErrorException 
    {
   
        final String mName = "Database::IsValidTextString(): ";
        char ch;
        int i;
        int len;
        
        if ( obj == null ) 
        {
            throw new SystemErrorException(mName + "obj null on entry.");
        } 
        else if ( ! ( obj instanceof String ) ) 
        { 
            return false;
        }
        
        /* If we get this far, we know that obj is a String */
        
        String s = (String)obj;
               
        len = s.length();
            
        for ( i = 0; i < len; i++ ) {
             
            ch = s.charAt(i);
            
            if ( ( ch < 0 ) || ( ch > 0x7F ) || ( ch == '\b') )
            {
                // string contains a character that can't appear in a 
                // text string.
                return false;
            }
        }
        
        return true;
        
    } /* Database::IsValidTextString() */
    
    
    /** 
     * IsValidTimeStamp()
     *
     * Test to see if the object is a valid time stamp.  For now that means
     * checking to see if it is an instance of Timestamp, verifying that the
     * number of ticks is non-negative, and that the number of ticks per 
     * second is positive.
     *
     *                                              JRM -- 2/11/07
     * 
     * Changes;
     *
     *    - None.
     *
     */
    
    public static boolean IsValidTimeStamp(Object obj)
        throws SystemErrorException
    {
        final String mName = "Database::IsValidTimeStamp(): ";

        if ( obj == null )
        {
            throw new SystemErrorException(mName + "obj null on entry.");
        }
        else if ( ! ( obj instanceof TimeStamp ) )
        {
            return false ;
        }
        
        TimeStamp s = (TimeStamp)obj;
        
        if ( ( s.getTime() < TimeStamp.MIN_TICKS ) || 
             ( s.getTime() > TimeStamp.MAX_TICKS ) ||
             ( s.getTPS() < TimeStamp.MIN_TPS ) ||
             ( s.getTPS() > TimeStamp.MAX_TPS ) )
            /* JRM */ 
            /* TODO: add a check to verify that the tps matches the db tps ?? */
        {
            return false;
        }
        
        return true;
        
    } /* Database::IsValidTimeStamp() */

     
    /**
     * IsValidQuoteString()
     * 
     * Test to see if a object contains a valid quote string -- that is a 
     * string that can appear as an argument in a matrix or predicate.
     * Return true if it does, and false if it doesn't.
     *
     * For now, we will use the old MacSHAPA definition of a quote string:
     *
     *  <graphic_char> --> ASCII codes 0x21 - 0x7E
     *
     *	<quote_string_char> --> ( <graphic_char> - ( '"' ) ) | ( ' ' )
     *
     *	<quote_string> --> (<quote_string_char>)*
     *
     * Eventually we will have to extend this definition to make full use of
     * Unicode, but that can wait for now.
     *                                          JRM -- 1/25/07
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    public static boolean IsValidQuoteString(Object obj)
        throws SystemErrorException
    {
   
        final String mName = "Database::IsValidQuoteString(): ";
        char ch;
        int i;
        int len;
        
        if ( obj == null ) 
        {
            throw new SystemErrorException(mName + "obj null on entry.");
        } 
        else if ( ! ( obj instanceof String ) ) 
        { 
            return false;
        }
        
        String s = (String)obj;
               
        len = s.length();
            
        for ( i = 0; i < len; i++ ) {
             
            ch = s.charAt(i);
            
            /* recall that 0x20 is space -- thus the lower end of
             * the following test is 0x20, not 0x21.
             */
            if ( ( ch < 0x20 ) || ( ch > 0x7E ) || ( ch == '\"' ) ) 
            { 
                    // string contains a character that can't appear in a 
                    // quote string.
                    return false;
            }
        }
        
        return true;
        
    } /* Database::IsValidQuoteString() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * The following test methods should probably go somewhere else.  However 
     * here will do for now.
     *
     * Since Database is an abstract class, the test code tests only the 
     * class methods.
     *
     *                                          JRM 3/05/07
     */

    /**
     * TestClassDatabase()
     *
     * Main routine for all test code for the Database class proper.
     *
     *                                          JRM 3/03/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestClassDatabase(java.io.PrintStream outStream,
                                            boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class Database:\n");
        
        if ( ! TestIsValidFargName(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestIsValidFloat(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidInt(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidNominal(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidPredName(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidSVarName(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidTextString(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidTimeStamp(outStream, verbose) )
        {
            failures++;
        }       
        
        if ( ! TestIsValidQuoteString(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestAddMatrixVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestGetMatrixVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestGetMatrixVEs(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestMatrixNameInUse(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestMatrixVEExists(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestRemoveMatrixVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestReplaceMatrixVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestAddPredVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestGetPredVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestGetPredVEs(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestPredNameInUse(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestPredVEExists(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestRemovePredVE(outStream, verbose) )
        {
            failures++;
        }       

        if ( ! TestReplacePredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetVocabElement(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestVocabElementExists(outStream, verbose) )
        {
            failures++;
        }

//        if ( ! AdHocTest(outStream, verbose) )
//        {
//            failures++;
//        }
        
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class Database.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class Database.\n\n");
        }
        
        return pass;
        
    } /* Database::TestClassDatabase() */
    
    
    /**
     * TestDatabase()
     *
     * Main routine for all OpenSHAPA database test code.
     *
     *                                          JRM 3/03/05
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestDatabase(java.io.PrintStream outStream)
        throws SystemErrorException
    {
        boolean pass = true;
        boolean verbose = false;
        int failures = 0;
        
        outStream.print("Testing OpenSHAPA database:\n\n");
        
        if ( ! TestClassDatabase(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! UnTypedFormalArg.TestClassUnTypedFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! IntFormalArg.TestClassIntFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! FloatFormalArg.TestClassFloatFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TimeStampFormalArg.TestClassTimeStampFormalArg(outStream, 
                                                              verbose) )
        {
            failures++;
        }
        
        if ( ! TextStringFormalArg.TestClassTextStringFormalArg(outStream, 
                                                                verbose) )
        {
            failures++;
        }
        
        if ( ! QuoteStringFormalArg.TestClassQuoteStringFormalArg(outStream, 
                                                                  verbose) )
        {
            failures++;
        }
        
        if ( ! NominalFormalArg.TestClassNominalFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! PredFormalArg.TestClassPredFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! ColPredFormalArg.TestClassColPredFormalArg(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! PredicateVocabElement.TestClassPredicateVocabElement(outStream, 
                                                                    verbose) )
        {
            failures++;
        }
        
        if ( ! MatrixVocabElement.TestClassMatrixVocabElement(outStream, 
                                                              verbose) )
        {
            failures++;
        }
        
        if ( ! VocabList.TestClassVocabList(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! DBIndex.TestClassDBIndex(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! ColPred.TestClassColPred(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Predicate.TestClassPredicate(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Matrix.TestClassMatrix(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! ColPredDataValue.TestClassColPredDataValue(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! PredDataValue.TestClassPredDataValue(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! QuoteStringDataValue.TestClassQuoteStringDataValue(outStream, 
                                                                  verbose) )
        {
            failures++;
        }

        if ( ! TimeStampDataValue.TestClassTimeStampDataValue(outStream, 
                                                              verbose) )
        {
            failures++;
        }
        
        if ( ! DataCell.TestClassDataCell(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! DataColumn.TestClassDataColumn(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestInternalListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! MacshapaDatabase.TestClassMacshapaDatabase(outStream, verbose) )
        {
            failures++;
        }
        
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d groups of tests failed for OpenSHAPA database.\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for OpenSHAPA database.\n");
        }
        return pass;
        
    } /* Database::TestDatabase() */


    /**
     * testIsValidFargName
     *
     * Run a variety of valid and invalid strings past IsValidFArgName, and
     * see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidFargName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidFargName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 15;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return false */ "<>",
            /* test  1 -- should return false */ "<",
            /* test  2 -- should return true  */ "<a>",
            /* test  3 -- should return false */ "<a(b>",
            /* test  4 -- should return false */ "<)a>",
            /* test  5 -- should return false */ "<a<b>",
            /* test  6 -- should return false */ "<>>",
            /* test  7 -- should return false */ "<a,b>",
            /* test  8 -- should return false */ "<\"a>",
            /* test  9 -- should return true  */ "<!#$%&'*+-./>",
            /* test 10 -- should return true  */ "<0123456789\072;=?>",
            /* test 11 -- should return true  */ "<@ABCDEFGHIJKLMNO>",
            /* test 12 -- should return true  */ "<PQRSTUVWXYZ[\\]^_>",
            /* test 13 -- should return true  */ "<`abcdefghijklmno>",
            /* test 14 -- should return true  */ "<pqrstuvwxyz{\174}~>"
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidFargName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidFargName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidFargName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidFargName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidFargName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidFargName(), so we will not bother to test that way
         * of generating a system error.
         */

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

    } /* Database::TestIsValidFarg() */

    
    /**
     * TestIsValidFloat
     *
     * Run a variety of valid and invalid objects past IsValidFloat, and 
     * see if it gets the right answer.  
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestIsValidFloat(java.io.PrintStream outStream,
                                           boolean verbose)
    {
        String testBanner =
            "Testing IsValidFloat()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 10;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return true  */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return true  */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
        };
        
        outStream.print(testBanner);
        
        if ( verbose )
        {
            outStream.print("\n");
        }
        
        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidFloat(%s) --> %b: ",
                        testNum, testDesc[testNum], 
                        expectedResult[testNum]);
            }
            
            threwException = false;
            result = false;
            
            try
            {
                result = IsValidFloat(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true; 
            }
            
            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }
            
            testNum++;
        }
        
        /* Now verify that we throw a system error exception when 
         * IsValidFloat is called with a null parameter.
         */
        
        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidFloat(null) --> exception: ",
                    testNum);
        }
        
        try
        {
            result = IsValidFloat(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true; 
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }
        
        testNum++;
         
        /* It seems that the compiler will not let me pass a non-string 
         * to IsValidFargName(), so we will not bother to test that way 
         * of generating a system error.
         */

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
        
    } /* Database::TestIsValidFloat() */

    
    /**
     * TestIsValidInt
     *
     * Run a variety of valid and invalid objects past IsValidInt, and 
     * see if it gets the right answer.  
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    
    public static boolean TestIsValidInt(java.io.PrintStream outStream,
                                         boolean verbose)
    {
        String testBanner =
            "Testing IsValidInt()                                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 10;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return true  */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return true  */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ true,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
        };
        
        outStream.print(testBanner);
        
        if ( verbose )
        {
            outStream.print("\n");
        }
        
        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidInt(%s) --> %b: ",
                        testNum, testDesc[testNum], 
                        expectedResult[testNum]);
            }
            
            threwException = false;
            result = false;
            
            try
            {
                result = IsValidInt(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true; 
            }
            
            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }
            
            testNum++;
        }
        
        /* Now verify that we throw a system error exception when 
         * IsValidFloat is called with a null parameter.
         */
        
        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidInt(null) --> exception: ",
                    testNum);
        }
        
        try
        {
            result = IsValidInt(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true; 
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }
        
        testNum++;
         
        /* It seems that the compiler will not let me pass a non-string 
         * to IsValidFargName(), so we will not bother to test that way 
         * of generating a system error.
         */

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
        
    } /* Database::TestIsValidInt() */


    /**
     * testIsValidNominal
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidNominal, and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidNominal(java.io.PrintStream outStream,
                                             boolean verbose)
    {
        String testBanner =
            "Testing IsValidNominal()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 29;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return false */ "(",
            /* test 11 -- should return false */ ")",
            /* test 12 -- should return false */ "<",
            /* test 13 -- should return false */ ">",
            /* test 14 -- should return false */ ",",
            /* test 15 -- should return false */ " leading white space",
            /* test 16 -- should return false */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&'*+-./",
            /* test 18 -- should return true  */ "0123456789\072;=?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "horizontal\ttab",
            /* test 24 -- should return false */ "embedded\bback space",
            /* test 25 -- should return false */ "embedded\nnew line",
            /* test 26 -- should return false */ "embedded\fform feed",
            /* test 27 -- should return false */ "embedded\rcarriage return",
            /* test 28 -- should return true  */ "a",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return false */ "(",
            /* test 11 -- should return false */ ")",
            /* test 12 -- should return false */ "<",
            /* test 13 -- should return false */ ">",
            /* test 14 -- should return false */ ",",
            /* test 15 -- should return false */ " leading white space",
            /* test 16 -- should return false */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&'*+-./",
            /* test 18 -- should return true  */ "0123456789\072;=?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "horizontal\ttab",
            /* test 24 -- should return false */ "embedded\bback space",
            /* test 25 -- should return false */ "embedded\nnew line",
            /* test 26 -- should return false */ "embedded\fform feed",
            /* test 27 -- should return false */ "embedded\rcarriage return",
            /* test 28 -- should return true  */ "a",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
            /* test 12 should return */ false,
            /* test 13 should return */ false,
            /* test 14 should return */ false,
            /* test 15 should return */ false,
            /* test 16 should return */ false,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ true,
            /* test 23 should return */ false,
            /* test 24 should return */ false,
            /* test 25 should return */ false,
            /* test 26 should return */ false,
            /* test 27 should return */ false,
            /* test 28 should return */ true,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidNominal(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidNominal(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidNominal is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidNominal(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidNominal(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidNominal() */


    /**
     * testIsValidPredName
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidPredName, and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidPredName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidPredName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 21;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return true  */ "A_Valid_Predicate_Name",
            /* test  1 -- should return false */ "(",
            /* test  2 -- should return false */ ")",
            /* test  3 -- should return false */ "<",
            /* test  4 -- should return false */ ">",
            /* test  5 -- should return false */ ",",
            /* test  6 -- should return false */ " leading white space",
            /* test  7 -- should return false */ "trailing while space ",
            /* test  8 -- should return true  */ "!#$%&'*+-./",
            /* test  9 -- should return true  */ "0123456789\072;=?",
            /* test 10 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 11 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 12 -- should return true  */ "`abcdefghijklmno",
            /* test 13 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 14 -- should return true  */ "a",
            /* test 15 -- should return false */ "embedded space",
            /* test 16 -- should return false */ "horizontal\ttab",
            /* test 17 -- should return false */ "embedded\bback_space",
            /* test 18 -- should return false */ "embedded\nnew_line",
            /* test 19 -- should return false */ "embedded\fform_feed",
            /* test 20 -- should return false */ "embedded\rcarriage_return",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ true,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ false,
            /* test 16 should return */ false,
            /* test 17 should return */ false,
            /* test 18 should return */ false,
            /* test 19 should return */ false,
            /* test 20 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidPredName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidPredName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidPredName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidPredName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidPredName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;
         
        /* It seems that the compiler will not let me pass a non-string 
         * to IsValidPredName(), so we will not bother to test that way 
         * of generating a system error.
         */

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

    } /* Database::TestIsValidPredName() */


    /**
     * testIsValidSVarName
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidSVarName, and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidSVarName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidSVarName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 21;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return true  */ "A Valid S-Var Name",
            /* test  1 -- should return false */ "(",
            /* test  2 -- should return false */ ")",
            /* test  3 -- should return false */ "<",
            /* test  4 -- should return false */ ">",
            /* test  5 -- should return false */ ",",
            /* test  6 -- should return false */ " leading white space",
            /* test  7 -- should return false */ "trailing while space ",
            /* test  8 -- should return true  */ "!#$%&'*+-./",
            /* test  9 -- should return true  */ "0123456789\072;=?",
            /* test 10 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 11 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 12 -- should return true  */ "`abcdefghijklmno",
            /* test 13 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 14 -- should return true  */ "a",
            /* test 15 -- should return true  */ "embedded space",
            /* test 16 -- should return false */ "horizontal\ttab",
            /* test 17 -- should return false */ "embedded\bback_space",
            /* test 18 -- should return false */ "embedded\nnew_line",
            /* test 19 -- should return false */ "embedded\fform_feed",
            /* test 20 -- should return false */ "embedded\rcarriage_return",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ true,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ false,
            /* test 17 should return */ false,
            /* test 18 should return */ false,
            /* test 19 should return */ false,
            /* test 20 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidSVarName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidSVarName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidPredName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidSVarName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidSVarName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;
         
        /* It seems that the compiler will not let me pass a non-string 
         * to IsValidSVarName(), so we will not bother to test that way 
         * of generating a system error.
         */

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

    } /* Database::TestIsValidSVarName() */


    /**
     * testIsValidTextString
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidTextString(), and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidTextString(java.io.PrintStream outStream,
                                                boolean verbose)
    {
        String testBanner =
            "Testing IsValidTextString()                                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 23;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Text String",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return false */ "an invalid text \b string",
            /* test 11 -- should return true  */ "",
            /* test 12 -- should return true  */ "/0/1/2/3/4/5/6/7/11/12/13",
            /* test 13 -- should return true  */ "/14/15/16/17/20/21/22/23",
            /* test 14 -- should return true  */ "/24/25/26/27/30/31/32/33",
            /* test 15 -- should return true  */ "/34/35/36/37 ",
            /* test 16 -- should return true  */ "!\"#$%&\'()*+,-./",
            /* test 17 -- should return true  */ "0123456789\072;<=>?",
            /* test 18 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 19 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 20 -- should return true  */ "`abcdefghijklmno",
            /* test 21 -- should return true  */ "pqrstuvwxyz{\174}~\177",
            /* test 22 -- should return false */ "\200",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Text String",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return false */ "an invalid text \b string",
            /* test 11 -- should return true  */ "",
            /* test 12 -- should return true  */ "/0/1/2/3/4/5/6/7/11/12/13",
            /* test 13 -- should return true  */ "/14/15/16/17/20/21/22/23",
            /* test 14 -- should return true  */ "/24/25/26/27/30/31/32/33",
            /* test 15 -- should return true  */ "/34/35/36/37 ",
            /* test 16 -- should return true  */ "!\"#$%&\'()*+,-./",
            /* test 17 -- should return true  */ "0123456789\072;<=>?",
            /* test 18 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 19 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 20 -- should return true  */ "`abcdefghijklmno",
            /* test 21 -- should return true  */ "pqrstuvwxyz{\174}~\177",
            /* test 22 -- should return false */ "\200",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ false,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidTextString(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidTextString(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidTextString is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidTextString(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidTextString(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidTextString() */


    /**
     * testIsValidTimeStamp
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidTextString(), and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidTimeStamp(java.io.PrintStream outStream,
                                               boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing IsValidTimeStamp()                                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 12;
        
        /* the tests with a TimeStamp object are a bit slim, but the 
         * TimeStamp class is supposed to prevent creation of an invalid
         * time stamp.
         */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return true  */ new TimeStamp(60),
            /* test 11 -- should return true  */ new TimeStamp(60,120),
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return true  */ "new TimeStamp(60)",
            /* test 11 -- should return true  */ "new TimeStamp(60,120)",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
        };
        
        outStream.print(testBanner);
        
        if ( verbose )
        {
            outStream.print("\n");
        }
        
        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidTimeStamp(%s) --> %b: ",
                        testNum, testDesc[testNum], 
                        expectedResult[testNum]);
            }
            
            threwException = false;
            result = false;
            
            try
            {
                result = IsValidTimeStamp(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true; 
            }
            
            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }
            
            testNum++;
        }
        
        /* Now verify that we throw a system error exception when 
         * IsValidFloat is called with a null parameter.
         */
        
        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidTimeStamp(null) --> exception: ",
                    testNum);
        }
        
        try
        {
            result = IsValidTimeStamp(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true; 
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }
        
        testNum++;
         
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
        
    } /* Database::TestIsValidTimeStamp() */


    /**
     * testIsValidQuoteString
     *
     * Run a variety of objects and valid and invalid strings past 
     * IsValidQuoteString, and see it it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidQuoteString(java.io.PrintStream outStream,
                                                 boolean verbose)
    {
        String testBanner =
            "Testing IsValidQuoteString()                                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 30;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Quote String",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return true  */ "(",
            /* test 11 -- should return true  */ ")",
            /* test 12 -- should return true  */ "<",
            /* test 13 -- should return true  */ ">",
            /* test 14 -- should return true  */ ",",
            /* test 15 -- should return true  */ " leading white space",
            /* test 16 -- should return true  */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&\'()*+,-./",
            /* test 18 -- should return true  */ "0123456789\072;<=>?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "\177",
            /* test 24 -- should return false */ "horizontal\ttab",
            /* test 25 -- should return false */ "embedded\bback space",
            /* test 26 -- should return false */ "embedded\nnew line",
            /* test 27 -- should return false */ "embedded\fform feed",
            /* test 28 -- should return false */ "embedded\rcarriage return",
            /* test 29 -- should return true  */ "a",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return true  */ "(",
            /* test 11 -- should return true  */ ")",
            /* test 12 -- should return true  */ "<",
            /* test 13 -- should return true  */ ">",
            /* test 14 -- should return true  */ ",",
            /* test 15 -- should return true  */ " leading white space",
            /* test 16 -- should return true  */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&\'()*+,-./",
            /* test 18 -- should return true  */ "0123456789\072;<=>?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "\177",
            /* test 24 -- should return false */ "horizontal\ttab",
            /* test 25 -- should return false */ "embedded\bback space",
            /* test 26 -- should return false */ "embedded\nnew line",
            /* test 27 -- should return false */ "embedded\fform feed",
            /* test 28 -- should return false */ "embedded\rcarriage return",
            /* test 29 -- should return true  */ "a",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ true,
            /* test 23 should return */ false,
            /* test 24 should return */ false,
            /* test 25 should return */ false,
            /* test 26 should return */ false,
            /* test 27 should return */ false,
            /* test 28 should return */ false,
            /* test 29 should return */ true,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidQuoteString(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = IsValidQuoteString(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else
                    {
                        outStream.print("failed.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
        }

        /* Now verify that we throw a system error exception when
         * IsValidQuoteString is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidQuoteString(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = IsValidQuoteString(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidQuoteString() */

    /**
     * TestAddMatrixVE()
     *
     * Test the addMatrixVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                              JRM -- 7/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAddMatrixVE(java.io.PrintStream outStream,
                                          boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing addMatrixVE()                                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
        }
        
        /* Now run a cursory set of tests:
         *
         * addMatrixVE() should succeed with mve, and fail with 
         * null.  Passing pve should fail at compile time.  Since 
         * addMatrixVE() is otherwise just a call to ve.addElement(), 
         * no further testing is needed.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mve_id = db.addMatrixVE(mve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( mve_id == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( ! completed )
                    {
                        outStream.print(
                                "addMatrixVE(mve) failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("addMatrixVE(mve) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else if ( db.vl.getVocabElement(mve_id) == mve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("addMatrixVE() failed to copy.\n");
                }
            }
        }

        /* now try to pass null to addMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.addMatrixVE(null);
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
                        outStream.print("addMatrixVE(null) completed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("addMatrixVE(null) failed to " +
                                "a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestAddMatrixVE() */
 

    /**
     * TestGetMatrixVE()
     *
     * Test the getMatrixVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetMatrixVE(java.io.PrintStream outStream,
                                          boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getMatrixVE()                                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            ve0 = null;
            ve1 = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Now run a cursory set of tests:
         *
         * getMatrixVE(mve_id) and getMatrixVE("matrix") should both return 
         * copies of mve, and getMatrixVE(pve_id) and getMatrixVE(INVALID_ID)
         * should both throw a system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getMatrixVE(mve_id);
                ve1 = db.getMatrixVE("matrix");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof MatrixVocabElement ) ) ||
                 ( ve0.getName().compareTo("matrix") != 0 ) ||
                 ( ve0.getID() != mve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof MatrixVocabElement ) ) ||
                 ( ve1.getName().compareTo("matrix") != 0 ) ||
                 ( ve1.getID() != mve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(mve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("matrix") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getMatrixVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof MatrixVocabElement ) ) ||
                         ( ve0.getName().compareTo("matrix") != 0 ) ||
                         ( ve0.getID() != mve_id ) ||
                         ( ve0 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof MatrixVocabElement ) ) ||
                         ( ve1.getName().compareTo("matrix") != 0 ) ||
                         ( ve1.getID() != mve_id ) ||
                         ( ve1 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getMatrixVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass pve_id to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getMatrixVE(pve_id);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getMatrixVE(pve_id) completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(pve_id) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass "pred" to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getMatrixVE("pred");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getMatrixVE(\"pred\") completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(\"pred\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getMatrixVE(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getMatrixVE(INVALID_ID) completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(3).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(INVALID_ID) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getMatrixVE() -- 
         * should fail 
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getMatrixVE("nonesuch");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getMatrixVE(\"nonesuch\") completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(4).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetMatrixVE() */

    /**
     * TestGetMatrixVEs()
     *
     * Test the getMatrixVEs() method.  Only cursory testing is needed, as
     * getMatrixVEs() just calls vl.getMatricies().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetMatrixVEs(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getMatrixVEs()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement inserted_mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        java.util.Vector<MatrixVocabElement> mves0;
        java.util.Vector<MatrixVocabElement> mves1;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run getMatrixVEs() --
         * should return null.  Add the matrix and predicate and run 
         * getMatrixVEs() again.  Should return a vector containing a 
         * copy of the matrix, but not the predicate.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            mves0 = new java.util.Vector<MatrixVocabElement>();
            mves1 = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mves0 = db.getMatrixVEs();
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                mves1 = db.getMatrixVEs();
                /* need a copy of the inserted mve for later testing, as
                 * adding the matix vocab element to the vocab list will
                 * assign ids to the ve and all its formal arguments.
                 */
                inserted_mve = db.getMatrixVE(mve_id);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( mves0 != null ) ||
                 ( mves1 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( mves0 != null )
                    {
                        outStream.print("mves0 != null.\n");
                    }
                    
                    if ( mves1 == null )
                    {
                        outStream.print("mves1 == null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else
            {
                MatrixVocabElement values[] = {inserted_mve};
                
                if ( ! VocabList.VerifyVectorContents(mves1, 1, values, 
                                                      outStream, verbose, 1) )
                {
                    failures++;
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

    } /* Database::TestGetMatrixVEs() */
    

    /**
     * TestMatrixNameInUse()
     *
     * Test the matrixNameInUse() method.  Only cursory testing is needed, as
     * matrixNameInUse() just calls vl.inVocabList().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMatrixNameInUse(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing matrixNameInUse()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        /* initialize the inUse booleans to the oposite of the expected values */
        boolean inUse0 = true;
        boolean inUse1 = true;
        boolean inUse2 = true;
        boolean inUse3 = false;
        boolean inUse4 = false;
        boolean inUse5 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run matrixNameInUse() 
         * on the predicate and matrix name, along with an unused valid name.
         * All should return false.  Add the matrix and predicate and run 
         * the set of calls to matrixNameInUse() again.  Should return true,
         * true and false respectively.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            inUse0 = true;
            inUse1 = true;
            inUse2 = true;
            inUse3 = false;
            inUse4 = false;
            inUse5 = true;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                inUse0 = db.matrixNameInUse("matrix");
                inUse1 = db.matrixNameInUse("pred");
                inUse2 = db.matrixNameInUse("nonesuch");
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                inUse3 = db.matrixNameInUse("matrix");
                inUse4 = db.matrixNameInUse("pred");
                inUse5 = db.matrixNameInUse("nonesuch");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( inUse0 != false ) ||
                 ( inUse1 != false ) ||
                 ( inUse2 != false ) ||
                 ( inUse3 != true ) ||
                 ( inUse4 != true ) ||
                 ( inUse5 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( inUse0 != false ) ||
                         ( inUse1 != false ) ||
                         ( inUse2 != false ) ||
                         ( inUse3 != true ) ||
                         ( inUse4 != true ) ||
                         ( inUse5 != false ) )
                    {
                        outStream.print(
                                "unexpected result(s) from mattrixNameInUse().\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the invalid string "<<invalid" to matrixNameInUse() -- 
         * should fail 
         */
        if ( failures == 0 )
        {
            completed = false;
            inUse0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                inUse0 = db.matrixNameInUse("<<invalid");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( inUse0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixNameInUse(\"<<invalid\") completed.\n");
                    }
                    
                    if ( inUse0 != true )
                    {
                        outStream.print("inUse0 != true.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixNameInUse(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestMatrixNameInUse() */

    /**
     * TestMatrixVEExists()
     *
     * Test the matrixVEExists() method.  Only cursory testing is needed, as
     * the function just calls vl.matrixInVocabList().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMatrixVEExists(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing matrixVEExists()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean mveExists0 = true;
        boolean mveExists1 = true;
        boolean mveExists2 = true;
        boolean mveExists3 = true;
        boolean mveExists4 = false;
        boolean mveExists5 = false;
        boolean mveExists6 = true;
        boolean mveExists7 = true;
        boolean mveExists8 = false;
        boolean mveExists9 = false;
        boolean mveExists10 = true;
        boolean mveExists11 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                mveExists0 = db.matrixVEExists("s-matrix");
                mveExists1 = db.matrixVEExists("matrix");
                mveExists2 = db.matrixVEExists("pred");
                mveExists3 = db.matrixVEExists("nonesuch");
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                mveExists4 = db.matrixVEExists("s-matrix");
                mveExists5 = db.matrixVEExists("matrix");
                mveExists6 = db.matrixVEExists("pred");
                mveExists7 = db.matrixVEExists("nonesuch");
                mveExists8 = db.matrixVEExists(smve_id);
                mveExists9 = db.matrixVEExists(mve_id);
                mveExists10 = db.matrixVEExists(pve_id);
                mveExists11 = db.matrixVEExists(1024);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( mveExists0 != false ) ||
                 ( mveExists1 != false ) ||
                 ( mveExists2 != false ) ||
                 ( mveExists3 != false ) ||
                 ( mveExists4 != true ) ||
                 ( mveExists5 != true ) ||
                 ( mveExists6 != false ) ||
                 ( mveExists7 != false ) ||
                 ( mveExists8 != true ) ||
                 ( mveExists9 != true ) ||
                 ( mveExists10 != false ) ||
                 ( mveExists11 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( mveExists0 != false ) ||
                         ( mveExists1 != false ) ||
                         ( mveExists2 != false ) ||
                         ( mveExists3 != false ) ||
                         ( mveExists4 != true ) ||
                         ( mveExists5 != true ) ||
                         ( mveExists6 != false ) ||
                         ( mveExists7 != false ) ||
                         ( mveExists8 != true ) ||
                         ( mveExists9 != true ) ||
                         ( mveExists10 != false ) ||
                         ( mveExists11 != false ) )
                    {
                        outStream.print(
                            "unexpected result(s) from matrixVEEsists().\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that matrixVEExists("<<invalid") and matrixVEExists(INVALID_ID)
         * throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            mveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mveExists0 = db.matrixVEExists("<<invalid");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( mveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixVEExists(\"<<invalid\") completed.\n");
                    }
                    
                    if ( mveExists0 != true )
                    {
                        outStream.print("mveExists0 != true(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixVEExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            mveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mveExists0 = db.matrixVEExists(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( mveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixVEExists(INVALID_ID) completed.\n");
                    }
                    
                    if ( mveExists0 != true )
                    {
                        outStream.print("mveExists0 != true(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixVEExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestMatrixVEExists() */


    /**
     * TestRemoveMatrixVE()
     *
     * Test the removeMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than calls vl.removeVocabElement().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestRemoveMatrixVE(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing removeMatrixVE()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now call removeMatrixVE(mve_id).  Should succeed */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.removeMatrixVE(mve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "removeMatrixVE(mve_id) failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that removeMatrixVE(pve_id) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.removeMatrixVE(pve_id);
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
                        outStream.print("removeMatrixVE(pve_id) completed.\n");
                    }


                    if ( threwSystemErrorException )
                    {
                        outStream.print("removeMatrixVE(pve_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestRemoveMatrixVE() */

    
    /**
     * TestReplaceMatrixVE()
     *
     * Test the replaceMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than call vl.replaceVocabElement().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestReplaceMatrixVE(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing replaceMatrixVE()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement mod_mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* now get a copy of mve, modify it, and then call replaceMatrixVE()
         * with the modified version.  Should succeed 
         */
        if ( failures == 0 )
        {
            completed = false;
            delta = null;
            mod_mve = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mod_mve = db.getMatrixVE(mve_id);
                mod_mve.setName("mod_matrix");
                delta = new UnTypedFormalArg(db, "<delta>");
                mod_mve.appendFormalArg(delta);
                db.replaceMatrixVE(mod_mve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( mod_mve == null ) ||
                 ( delta == null ) ||
                 ( mod_mve == db.vl.getVocabElement(mod_mve.getID()) ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "test 1 failed to complete.\n");
                    }
                    
                    if ( mod_mve == null )
                    {
                        outStream.print(
                                "getMatrixVE(mve_id) returned null.\n");
                    }
                    
                    if ( delta == null )
                    {
                        outStream.print("couldn't allocate delta.\n");
                    }
                 
                    if ( mod_mve == db.vl.getVocabElement(mod_mve.getID()) )
                    {
                        outStream.print("replacement isn't a copy.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (mod_matrix(<bravo>, <delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that replaceMatrixVE(null) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.replaceMatrixVE(null);
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
                        outStream.print("replaceMatrixVE(null) completed.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.print("replaceMatrixVE(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (mod_matrix(<bravo>, <delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestReplaceMatrixVE() */


    /**
     * TestAddPredVE()
     *
     * Test the addPredVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAddPredVE(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing addPredVE()                                              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
        }
        
        /* Now run a cursory set of tests:
         *
         * addPredVE() should succeed with mve, and fail with 
         * null.  Passing pve should fail at compile time.  Since 
         * addPredVE() is otherwise just a call to ve.addElement(), 
         * no further testing is needed.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( pve_id == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( ! completed )
                    {
                        outStream.print(
                                "addPredVE(pve) failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("addPredVE(pve) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (pred(<bravo>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else if ( db.vl.getVocabElement(pve_id) == pve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("addPredVE() failed to copy.\n");
                }
            }
        }

        /* now try to pass null to addPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.addPredVE(null);
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
                        outStream.print("addPredVE(null) completed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("addPredVE(null) failed to " +
                                "a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (pred(<bravo>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestAddPredVE() */
 

    /**
     * TestGetPredVE()
     *
     * Test the getPredVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetPredVE(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getPredVE()                                              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            ve0 = null;
            ve1 = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Now run a cursory set of tests:
         *
         * getPredVE(pve_id) and getPredVE("pred") should both return 
         * copies of pve, and getPredVE(mve_id) and getPredVE("matrix")
         * should both throw a system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getPredVE(pve_id);
                ve1 = db.getPredVE("pred");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                 ( ve0.getName().compareTo("pred") != 0 ) ||
                 ( ve0.getID() != pve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                 ( ve1.getName().compareTo("pred") != 0 ) ||
                 ( ve1.getID() != pve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(pve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("pred") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getPredVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                         ( ve0.getName().compareTo("pred") != 0 ) ||
                         ( ve0.getID() != pve_id ) ||
                         ( ve0 == db.vl.getVocabElement(pve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                         ( ve1.getName().compareTo("pred") != 0 ) ||
                         ( ve1.getID() != pve_id ) ||
                         ( ve1 == db.vl.getVocabElement("pred") ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getPredVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass mve_id to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getPredVE(mve_id);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getPredVE(mve_id) completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(mve_id) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass "matrix" to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getPredVE("matrix");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getPredVE(\"matrix\") completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"matrix\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getPredVE(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getPredVE(INVALID_ID) completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(3).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(INVALID_ID) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getPredVE() -- 
         * should fail 
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getPredVE("nonesuch");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getPredVE(\"nonesuch\") completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(4).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetPredVE() */

    /**
     * TestGetPredVEs()
     *
     * Test the getPredVEs() method.  Only cursory testing is needed, as
     * getPredVEs() just calls vl.getMatricies().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetPredVEs(java.io.PrintStream outStream,
                                         boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getPredVEs()                                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement inserted_pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        java.util.Vector<PredicateVocabElement> pves0;
        java.util.Vector<PredicateVocabElement> pves1;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run getPredVEs() --
         * should return null.  Add the matrix and predicate and run 
         * getPredVEs() again.  Should return a vector containing a 
         * copy of the predicate, but not the matrix.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            pves0 = new java.util.Vector<PredicateVocabElement>();
            pves1 = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                pves0 = db.getPredVEs();
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                pves1 = db.getPredVEs();
                /* need a copy of the inserted pve for later testing, as
                 * adding the predicate vocab element to the vocab list will
                 * assign ids to the ve and all its formal arguments.
                 */
                inserted_pve = db.getPredVE(pve_id);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( pves0 != null ) ||
                 ( pves1 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( pves0 != null )
                    {
                        outStream.print("pves0 != null.\n");
                    }
                    
                    if ( pves1 == null )
                    {
                        outStream.print("pves1 == null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else
            {
                PredicateVocabElement values[] = {inserted_pve};
                
                if ( ! VocabList.VerifyVectorContents(pves1, 1, values, 
                                                      outStream, verbose, 1) )
                {
                    failures++;
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

    } /* Database::TestGetPredVEs() */
    

    /**
     * TestPredNameInUse()
     *
     * Test the predNameInUse() method.  Only cursory testing is needed, as
     * predNameInUse() just calls vl.inVocabList().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPredNameInUse(java.io.PrintStream outStream,
                                            boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing predNameInUse()                                          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        /* initialize the inUse booleans to the opposite of the expected values */
        boolean inUse0 = true;
        boolean inUse1 = true;
        boolean inUse2 = true;
        boolean inUse3 = false;
        boolean inUse4 = false;
        boolean inUse5 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run matrixNameInUse() 
         * on the predicate and matrix name, along with an unused valid name.
         * All should return false.  Add the matrix and predicate and run 
         * the set of calls to matrixNameInUse() again.  Should return true,
         * true and false respectively.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            inUse0 = true;
            inUse1 = true;
            inUse2 = true;
            inUse3 = false;
            inUse4 = false;
            inUse5 = true;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                inUse0 = db.predNameInUse("matrix");
                inUse1 = db.predNameInUse("pred");
                inUse2 = db.predNameInUse("nonesuch");
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                inUse3 = db.predNameInUse("matrix");
                inUse4 = db.predNameInUse("pred");
                inUse5 = db.predNameInUse("nonesuch");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( inUse0 != false ) ||
                 ( inUse1 != false ) ||
                 ( inUse2 != false ) ||
                 ( inUse3 != true ) ||
                 ( inUse4 != true ) ||
                 ( inUse5 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( inUse0 != false ) ||
                         ( inUse1 != false ) ||
                         ( inUse2 != false ) ||
                         ( inUse3 != true ) ||
                         ( inUse4 != true ) ||
                         ( inUse5 != false ) )
                    {
                        outStream.print(
                                "unexpected result(s) from predNameInUse().\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the invalid string "<<invalid" to predNameInUse() -- 
         * should fail 
         */
        if ( failures == 0 )
        {
            completed = false;
            inUse0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                inUse0 = db.predNameInUse("<<invalid");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( inUse0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predNameInUse(\"<<invalid\") completed.\n");
                    }
                    
                    if ( inUse0 != true )
                    {
                        outStream.print("inUse0 != true.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("predNameInUse(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestPredNameInUse() */

    /**
     * TestPredVEExists()
     *
     * Test the predVEExists() method.  Only cursory testing is needed, as
     * the function just calls vl.predInVocabList().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPredVEExists(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing predVEExists()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pveExists0 = true;
        boolean pveExists1 = true;
        boolean pveExists2 = true;
        boolean pveExists3 = true;
        boolean pveExists4 = true;
        boolean pveExists5 = true;
        boolean pveExists6 = true;
        boolean pveExists7 = false;
        boolean pveExists8 = false;
        boolean pveExists9 = true;
        boolean pveExists10 = true;
        boolean pveExists11 = true;
        boolean pveExists12 = false;
        boolean pveExists13 = false;
        boolean pveExists14 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        long spve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement spve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            mve = null;
            smve = null;
            pve = null;
            spve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            spve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta = new UnTypedFormalArg(db, "<delta>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                spve = VocabList.ConstructTestPred(db, "s-pred", delta, null,
                                                  null, null);
                spve.setSystem();
                pveExists0 = db.predVEExists("s-matrix");
                pveExists1 = db.predVEExists("matrix");
                pveExists2 = db.predVEExists("pred");
                pveExists3 = db.predVEExists("s-pred");
                pveExists4 = db.predVEExists("nonesuch");
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                spve_id = db.addPredVE(spve);
                pveExists5 = db.predVEExists("s-matrix");
                pveExists6 = db.predVEExists("matrix");
                pveExists7 = db.predVEExists("pred");
                pveExists8 = db.predVEExists("s-pred");
                pveExists9 = db.predVEExists("nonesuch");
                pveExists10 = db.predVEExists(smve_id);
                pveExists11 = db.predVEExists(mve_id);
                pveExists12 = db.predVEExists(pve_id);
                pveExists13 = db.predVEExists(spve_id);
                pveExists14 = db.predVEExists(1024);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( pveExists0 != false ) ||
                 ( pveExists1 != false ) ||
                 ( pveExists2 != false ) ||
                 ( pveExists3 != false ) ||
                 ( pveExists4 != false ) ||
                 ( pveExists5 != false ) ||
                 ( pveExists6 != false ) ||
                 ( pveExists7 != true ) ||
                 ( pveExists8 != true ) ||
                 ( pveExists9 != false ) ||
                 ( pveExists10 != false ) ||
                 ( pveExists11 != false ) ||
                 ( pveExists12 != true ) ||
                 ( pveExists13 != true ) ||
                 ( pveExists14 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( pveExists0 != false ) ||
                         ( pveExists1 != false ) ||
                         ( pveExists2 != false ) ||
                         ( pveExists3 != false ) ||
                         ( pveExists4 != false ) ||
                         ( pveExists5 != false ) ||
                         ( pveExists6 != false ) ||
                         ( pveExists7 != true ) ||
                         ( pveExists8 != true ) ||
                         ( pveExists9 != false ) ||
                         ( pveExists10 != false ) ||
                         ( pveExists11 != false ) ||
                         ( pveExists12 != true ) ||
                         ( pveExists13 != true ) ||
                         ( pveExists14 != false ) )
                    {
                        outStream.print(
                            "unexpected result(s) from predVEEsists().\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that predVEExists("<<invalid") and predVEExists(INVALID_ID)
         * throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            pveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                pveExists0 = db.predVEExists("<<invalid");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( pveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predVEExists(\"<<invalid\") completed.\n");
                    }
                    
                    if ( pveExists0 != true )
                    {
                        outStream.print("pveExists0 != true(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("predVEExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            pveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                pveExists0 = db.predVEExists(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( pveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predVEExists(INVALID_ID) completed.\n");
                    }
                    
                    if ( pveExists0 != true )
                    {
                        outStream.print("pveExists0 != true(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("predVEExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestPredVEExists() */

    /**
     * TestRemovePredVE()
     *
     * Test the removePredVE() method.  Only cursory testing is needed, as
     * the function does little more than calls vl.removeVocabElement().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestRemovePredVE(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing removePredVE()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* now call removePredVE(pve_id).  Should succeed */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.removePredVE(pve_id);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "removePredVE(pve_id) failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that removePredVE(mve_id) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.removePredVE(mve_id);
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
                        outStream.print("removePredVE(mve_id) completed.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.print("removePredVE(mve_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestRemovePredVE() */


    /**
     * TestReplaceMatrixVE()
     *
     * Test the replaceMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than call vl.replaceVocabElement().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestReplacePredVE(java.io.PrintStream outStream,
                                            boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing replacePredVE()                                          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement mod_pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* now get a copy of pve, modify it, and then call replacePredVE()
         * with the modified version.  Should succeed 
         */
        if ( failures == 0 )
        {
            completed = false;
            delta = null;
            mod_pve = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mod_pve = db.getPredVE(pve_id);
                mod_pve.setName("mod_pred");
                delta = new UnTypedFormalArg(db, "<delta>");
                mod_pve.appendFormalArg(delta);
                db.replacePredVE(mod_pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( mod_pve == null ) ||
                 ( delta == null ) ||
                 ( mod_pve == db.vl.getVocabElement(mod_pve.getID()) ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "test 1 failed to complete.\n");
                    }
                    
                    if ( mod_pve == null )
                    {
                        outStream.print(
                                "getPredVE(pve_id) returned null.\n");
                    }
                    
                    if ( delta == null )
                    {
                        outStream.print("couldn't allocate delta.\n");
                    }
                 
                    if ( mod_pve == db.vl.getVocabElement(mod_pve.getID()) )
                    {
                        outStream.print("replacement isn't a copy.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "mod_pred(<charlie>, <delta>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that replacePredVE(null) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                db.replacePredVE(null);
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
                        outStream.print("replacePredVE(null) completed.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.print("replacePredVE(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }

            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "mod_pred(<charlie>, <delta>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestReplacePredVE() */

    /**
     * TestGetVocabElement()
     *
     * Test the getVocabElement() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to vl.getVocabElement().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetVocabElement(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getVocabElement()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        VocabElement ve2 = null;
        VocabElement ve3 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Now run a cursory set of tests:
         *
         * getVocabElement(pve_id) and getVocabElement("pred") should both 
         * return copies of pve, and getVocabElement(mve_id) and 
         * getVocabElement("matrix") should both return copies of mve.
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            ve1 = null;
            ve2 = null;
            ve3 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getVocabElement(pve_id);
                if ( ve0 == null )
                    outStream.print("it: ve0 == null\n");
                ve1 = db.getVocabElement("pred");
                ve2 = db.getVocabElement(mve_id);
                ve3 = db.getVocabElement("matrix");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                 ( ve0.getName().compareTo("pred") != 0 ) ||
                 ( ve0.getID() != pve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                 ( ve1.getName().compareTo("pred") != 0 ) ||
                 ( ve1.getID() != pve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(pve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("pred") ) ||
                 ( ve2 == null ) ||
                 ( ! ( ve2 instanceof MatrixVocabElement ) ) ||
                 ( ve2.getName().compareTo("matrix") != 0 ) ||
                 ( ve2.getID() != mve_id ) ||
                 ( ve3 == null ) ||
                 ( ! ( ve3 instanceof MatrixVocabElement ) ) ||
                 ( ve3.getName().compareTo("matrix") != 0 ) ||
                 ( ve3.getID() != mve_id ) ||
                 ( ve2 == ve3 ) ||
                 ( ve2 == db.vl.getVocabElement(mve_id) ) ||
                 ( ve3 == db.vl.getVocabElement("matrix") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getPredVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                         ( ve0.getName().compareTo("pred") != 0 ) ||
                         ( ve0.getID() != pve_id ) ||
                         ( ve0 == db.vl.getVocabElement(pve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                         ( ve1.getName().compareTo("pred") != 0 ) ||
                         ( ve1.getID() != pve_id ) ||
                         ( ve1 == db.vl.getVocabElement("pred") ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }


                    if ( ( ve2 == null ) ||
                         ( ! ( ve2 instanceof MatrixVocabElement ) ) ||
                         ( ve2.getName().compareTo("matrix") != 0 ) ||
                         ( ve2.getID() != mve_id ) ||
                         ( ve2 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve2.\n");
                    }

                    if ( ( ve3 == null ) ||
                         ( ! ( ve3 instanceof PredicateVocabElement ) ) ||
                         ( ve3.getName().compareTo("matrix") != 0 ) ||
                         ( ve3.getID() != mve_id ) ||
                         ( ve3 == db.vl.getVocabElement("matrix") ) )
                    {
                        outStream.print("unexpected ve3.\n");
                    }

                    if ( ve2 == ve3 )
                    {
                        outStream.print("ve2 == ve3.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getPredVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getVocabElement() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getVocabElement(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getVocabElement(INVALID_ID) completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getVocabElement(INVALID_ID) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getVocabElement() -- 
         * should fail 
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = db.getVocabElement("nonesuch");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getVocabElement(\"nonesuch\") completed.\n");
                    }
                    
                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetVocabElement() */


    /**
     * TestVocabElementExists()
     *
     * Test the vocabElementxists() method.  Only cursory testing is needed, 
     * as the function just calls vl.inVocabList().
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestVocabElementExists(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing vocabElementExists()                                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean veExists0 = true;
        boolean veExists1 = true;
        boolean veExists2 = true;
        boolean veExists3 = true;
        boolean veExists4 = true;
        boolean veExists5 = false;
        boolean veExists6 = false;
        boolean veExists7 = false;
        boolean veExists8 = false;
        boolean veExists9 = true;
        boolean veExists10 = false;
        boolean veExists11 = false;
        boolean veExists12 = false;
        boolean veExists13 = false;
        boolean veExists14 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        long spve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement spve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            mve = null;
            smve = null;
            pve = null;
            spve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            spve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta = new UnTypedFormalArg(db, "<delta>");
                smve = VocabList.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.matrixType.INTEGER, 
                        alpha, null, null, null);
                mve = VocabList.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.matrixType.MATRIX, 
                        bravo, null, null, null);
                pve = VocabList.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                spve = VocabList.ConstructTestPred(db, "s-pred", delta, null,
                                                  null, null);
                spve.setSystem();
                veExists0 = db.vocabElementExists("s-matrix");
                veExists1 = db.vocabElementExists("matrix");
                veExists2 = db.vocabElementExists("pred");
                veExists3 = db.vocabElementExists("s-pred");
                veExists4 = db.vocabElementExists("nonesuch");
                smve_id = db.addMatrixVE(smve);
                mve_id = db.addMatrixVE(mve);
                pve_id = db.addPredVE(pve);
                spve_id = db.addPredVE(spve);
                veExists5 = db.vocabElementExists("s-matrix");
                veExists6 = db.vocabElementExists("matrix");
                veExists7 = db.vocabElementExists("pred");
                veExists8 = db.vocabElementExists("s-pred");
                veExists9 = db.vocabElementExists("nonesuch");
                veExists10 = db.vocabElementExists(smve_id);
                veExists11 = db.vocabElementExists(mve_id);
                veExists12 = db.vocabElementExists(pve_id);
                veExists13 = db.vocabElementExists(spve_id);
                veExists14 = db.vocabElementExists(1024);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( veExists0 != false ) ||
                 ( veExists1 != false ) ||
                 ( veExists2 != false ) ||
                 ( veExists3 != false ) ||
                 ( veExists4 != false ) ||
                 ( veExists5 != true ) ||
                 ( veExists6 != true ) ||
                 ( veExists7 != true ) ||
                 ( veExists8 != true ) ||
                 ( veExists9 != false ) ||
                 ( veExists10 != true ) ||
                 ( veExists11 != true ) ||
                 ( veExists12 != true ) ||
                 ( veExists13 != true ) ||
                 ( veExists14 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }
                    
                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }
                    
                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }
                    
                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }
                    
                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }
                    
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }
                    
                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( veExists0 != false ) ||
                         ( veExists1 != false ) ||
                         ( veExists2 != false ) ||
                         ( veExists3 != false ) ||
                         ( veExists4 != false ) ||
                         ( veExists5 != true ) ||
                         ( veExists6 != true ) ||
                         ( veExists7 != true ) ||
                         ( veExists8 != true ) ||
                         ( veExists9 != false ) ||
                         ( veExists10 != true ) ||
                         ( veExists11 != true ) ||
                         ( veExists12 != true ) ||
                         ( veExists13 != true ) ||
                         ( veExists14 != false ) )
                    {
                        outStream.print("unexpected result(s) from " +
                                        "vocabElementExists().\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }
        
        /* Verify that vocabElementExists("<<invalid") and 
         * vocabElementExists(INVALID_ID) throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            veExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                veExists0 = db.vocabElementExists("<<invalid");
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( veExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("vocabElementExists(\"<<invalid\") " +
                                        "completed.\n");
                    }
                    
                    if ( veExists0 != true )
                    {
                        outStream.print("veExists0 != true(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("vocabElementExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            veExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                veExists0 = db.vocabElementExists(DBIndex.INVALID_ID);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( veExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "vocabElementExists(INVALID_ID) completed.\n");
                    }
                    
                    if ( veExists0 != true )
                    {
                        outStream.print("veExists0 != true(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("vocabElementExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestVocabElementExists() */
    
    

    /**
     * AdHocTest()
     *
     * Run Felix's test.
     *
     *                                              JRM -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean AdHocTest(java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
      // Create a database instance

      ODBCDatabase db = new ODBCDatabase();



      // Create a data column

      DataColumn column = new DataColumn(db, "TestColumn",
                                         MatrixVocabElement.matrixType.TEXT);

      //Add it to the database

      db.addColumn(column);

      // Not sure why this is necessary, column fields not set otherwise, so
      //have to retrieve a db copy of the column
      
      // Felix:  The addColumn call assigns IDs and creates the initial mve.

      column = db.getDataColumn("TestColumn");



     //Get the matrix vocab element for the column

      MatrixVocabElement mve = db.getMatrixVE(column.getItsMveID());



      // Create some data cells and add them to the database

      DataCell[] cells = new DataCell[4];

      for (int i=0; i<cells.length; i++) {

        cells[i] = new DataCell(db, column.getID(), mve.getID());

        long cid = db.appendCell(cells[i]);

        cells[i] = (DataCell)db.getCell(cid);

        System.out.printf("Initial cell[%d] = %s.\n", i, cells[i].toString());
      }



      // Modify the cells' data

      for (int i=0; i<cells.length; i++) {

        Matrix m = new Matrix(db, mve.getID());

        TextStringDataValue tsdv = new TextStringDataValue(db);

        tsdv.setItsValue("Testing. This is some more data. " +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + i);

        m.replaceArg(0, tsdv);

        // Hi Felix, 
        //
        // I expected you to just modify your copy and send it back to the 
        // database, as below.  The Database should make its own copy of your
        // cell, possibly making some additional notations.  
        // 
        // without modifying it.
        //
        // See example below.  Note that I have commented out your old code
        // as appropriate.
        
        //DataCell dc = new DataCell(db, column.getID(), mve.getID());
        DataCell dc = cells[i];

        //dc.setID(cells[i].getID());

        dc.setVal(m);

        dc.setOnset(new TimeStamp(60, i*60));

        dc.setOffset(new TimeStamp(60, i*60 + 59));
        
        db.replaceCell(dc);

        cells[i] = (DataCell)db.getCell(dc.getID());

        System.out.printf("mod 1 cell[%d] = %s.\n", i, cells[i].toString());
      }



      // Modify the cells' data again

      for (int i=0; i<cells.length; i++) {

        Matrix m = new Matrix(db, mve.getID());

        TextStringDataValue tsdv = new TextStringDataValue(db);

        tsdv.setItsValue("Testing " + i);

        m.replaceArg(0, tsdv);

        DataCell dc = cells[i];


        dc.setVal(m);

        db.replaceCell(dc);

        cells[i] = (DataCell)db.getCell(dc.getID());

        System.out.printf("mod 2 cell[%d] = %s.\n", i, cells[i].toString());
      }


        return true;
    }

    
    /*************************************************************************/
    /*********************** Listener Test Code: *****************************/
    /*************************************************************************/

    /**
     * TestInternalListeners()
     *
     * Main routine for all test code testing internal propagation of changes
     * through the database via the internal listeners.
     *
     *                                          JRM 3/25/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestInternalListeners(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing Internal Listeners:\n");
        
        if ( ! TestPVEModListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestPVEDeletionListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestMVEModListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestDataCellDeletionListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestDataCellInsertionListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestDataCellModListeners(outStream, verbose) )
        {
            failures++;
        }
        
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in internal listener tests.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All internal listener tests passed.\n\n");
        }
        
        return pass;
        
    } /* Database::TestInternalListeners() */


    /**
     * TestDataCellDeletionListeners()
     *
     * Verify that data cell deletions propogate through the database as 
     * expected.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellDeletionListeners(
                                                java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell deletion listeners                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here
        
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
        
        outStream.printf("          --- TEST NOT IMPLEMENTED ---\n");
        
        return pass;

    } /* Database::TestDataCellDeletionListeners() */


    /**
     * TestDataCellDeletionListeners()
     *
     * Verify that data cell deletions propogate through the database as 
     * expected.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellInsertionListeners(
                                                java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell insertion listeners                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here
        
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
        
        outStream.printf("          --- TEST NOT IMPLEMENTED ---\n");
        
        return pass;

    } /* Database::TestDataCellInsertionListeners() */


    /**
     * TestDataCellModListeners()
     *
     * Verify that modifications to data cells propogate through
     * the database as expected.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellModListeners(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell modification listeners                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here
        
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
        
        outStream.printf("          --- TEST NOT IMPLEMENTED ---\n");
        
        return pass;

    } /* Database::TestDataCellModListeners() */


    /**
     * TestMVEModListeners()
     *
     * Verify that modifications in matrix vocab elements propogate through
     * the database as expected.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMVEModListeners(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing matrix vocab element modification listeners              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        failures += TestMVEModListeners__test_01(outStream, verbose);
        
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

    } /* Database::TestMVEModListeners() */
    
    
    /**
     * TestPVEModListeners__test_01()
     *
     * Initial smoke check on the PVE mod listeners:
     *
     * Allocate a data base, and create several predicates and matrix data 
     * columns.  Insert a selection of cells in the columns with various 
     * predicate values.
     *
     * Add, & delete formal arguments in the matrix vocab elements associated
     * with the matrix data columns.  Verify that the changes are reflected 
     * correctly in the cells.  
     * 
     * Re-arrange formal arguemnst and verify that the changes are reflected 
     * correctly in the cells.  
     * 
     * Combine the above and verify the expected results.
     *
     * Return the number of failures.
     *
     *                                              JRM -- 4/25/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestMVEModListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
        throws SystemErrorException
    {
        final String header = "test 01: ";
        String systemErrorExceptionString = "";
        String expectedString0 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString1 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString2 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " + 
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString3 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString4 =  
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), " +
                      "0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.0, 0, , (), \"\", 00:00:00:000, <arg7>))))))))";
        String expectedString5 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), " +
                       "0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), " +
                       "0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.0, 0, , (), \"\", 00:00:00:000, <arg7>))))))))";
        String expectedString6 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, <arg0.5>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(11, 10.0, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, " +
                     "(TWENTY-ONE, 20.0, 20, TWENTY, " +
                      "pve1(<arg0>, <arg1>), " +
                      "\"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString7 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, <arg0.5>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.0, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.0, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString8 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.0, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.0, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString9 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>, <arg1>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.0, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.0, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString10 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.0, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.0, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.0, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString11 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg>, <arg1>, <arg2>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.0, 10, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.0, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString12 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg1>, <arg2>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.0, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (10.0, 10, pve0(<arg0>), \"ten\", 00:01:00:000, 11.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (20.0, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString13 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg1>, <arg2>, <arg4>, <arg5>, <arg6>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.0, 0, (), \"\", 00:00:00:000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.0, 0, (), \"\", 00:00:00:000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.0, 0, (), \"\", 00:00:00:000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.0, 0, (), \"\", 00:00:00:000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (10.0, 10, pve0(<arg0>), \"ten\", 00:01:00:000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (20.0, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000))))))))";
        String expectedString14 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg4>, <arg1>, <arg2>, <arg5>, <arg6>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, ((), 0.0, 0, \"\", 00:00:00:000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, ((), 0.0, 0, \"\", 00:00:00:000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, ((), 0.0, 0, \"\", 00:00:00:000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, ((), 0.0, 0, \"\", 00:00:00:000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (pve0(<arg0>), 10.0, 10, \"ten\", 00:01:00:000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (pve1(<arg0>, <arg1>), 20.0, 20, \"twenty\", 00:02:00:000))))))))";
        String expectedString15 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg6>, <arg5>, <arg2>, <arg4>, <arg1>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 2.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.0, 0, , (), \"\", 00:00:00:000, <arg>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.0, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.0)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.0, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (00:01:00:000, \"ten\", 10, pve0(<arg0>), 10.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (00:02:00:000, \"twenty\", 20, pve1(<arg0>, <arg1>), 20.0))))))))";
        String expectedString16 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg>), " +
                 "mdc3(<arg>), " +
                 "mdc1(<arg6>, <arg5>, <arg2>, <arg4>, <arg1>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg1>, <arg2>, <new_arg>, <arg4>, <arg5>, <arg6>, <arg3>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(5, 00:00:04:000, 00:00:05:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(6, 00:00:05:000, 00:00:06:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(7, 00:00:06:000, 00:00:07:000, (0.0, 0, , (), \"\", 00:00:00:000, )), " +
                   "(8, 00:00:04:000, 00:00:05:000, (100.0, 100, , pve0(<arg0>), \"hundred\", 00:10:00:000, HUNDRED)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (200.0, 200, , pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, TWO-HUNDRED)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (00:00:00:000, \"\", 0, (), 0.0)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (00:01:00:000, \"ten\", 10, pve0(<arg0>), 10.0)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (00:02:00:000, \"twenty\", 20, pve1(<arg0>, <arg1>), 20.0))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdc0ID = DBIndex.INVALID_ID;
        long mdc1ID = DBIndex.INVALID_ID;
        long mdc2ID = DBIndex.INVALID_ID;
        long mdc3ID = DBIndex.INVALID_ID;
        long mdc0_mveID = DBIndex.INVALID_ID;
        long mdc1_mveID = DBIndex.INVALID_ID;
        long mdc2_mveID = DBIndex.INVALID_ID;
        long mdc3_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc0 = null;
        DataColumn mdc1 = null;
        DataColumn mdc2 = null;
        DataColumn mdc3 = null;
        MatrixVocabElement mve0 = null;
        MatrixVocabElement mve1 = null;
        MatrixVocabElement mve2 = null;
        MatrixVocabElement mve3 = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                // allocate a new database
                
                db = new ODBCDatabase();
                
                
                // create a selection of predicates
                
                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);
                
                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                
                
                // create Data columns
                
                mdc0 = new DataColumn(db, "mdc0", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdc0ID = db.addColumn(mdc0);
                mdc0 = db.getDataColumn(mdc0ID);
                mdc0_mveID = mdc0.getItsMveID();
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                mdc1 = new DataColumn(db, "mdc1", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdc1ID = db.addColumn(mdc1);
                mdc1 = db.getDataColumn(mdc1ID);
                mdc1_mveID = mdc1.getItsMveID();
                mve1 = db.getMatrixVE(mdc1_mveID);
                
                mdc2 = new DataColumn(db, "mdc2", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdc2ID = db.addColumn(mdc2);
                mdc2 = db.getDataColumn(mdc2ID);
                mdc2_mveID = mdc2.getItsMveID();
                mve2 = db.getMatrixVE(mdc2_mveID);
                
                mdc3 = new DataColumn(db, "mdc3", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdc3ID = db.addColumn(mdc3);
                mdc3 = db.getDataColumn(mdc3ID);
                mdc3_mveID = mdc3.getItsMveID();
                mve3 = db.getMatrixVE(mdc3_mveID);
                                
                
                // create a selection of cells
                
                // cells for mdc0
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc0ID, 
                        mdc0_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc0ID, 
                        mdc0_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc0ID, 
                        mdc0_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc0ID, 
                        mdc0_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                
                
                // cells for mdc1
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                
                
                // cells for mdc2
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            IntDataValue.Construct(db, 1))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            FloatDataValue.Construct(db, 2.0))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            NominalDataValue.Construct(db, "THREE"))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 4))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            QuoteStringDataValue.Construct(db, "five"))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        300,
                        360,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            TimeStampDataValue.Construct(db, 3600))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            UndefinedDataValue.Construct(db))));
                
                
                // cells for mdc3 -- none or now
                
                
                // create the test string
                testStringA = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }
                    
                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }
                    
                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try adding some arguments -- all untyped for now */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                mve0.appendFormalArg(new UnTypedFormalArg(db, "<arg1>"));
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                // create the test string
                testStringA = db.toString();

                mve0.insertFormalArg(new UnTypedFormalArg(db, "<arg-1>"), 0);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                // create the test string
                testStringB = db.toString();
                
                mve0.insertFormalArg(new UnTypedFormalArg(db, "<arg0.5>"), 1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                testStringC = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* add some more arguments -- this time typed. */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                mve1.appendFormalArg(new FloatFormalArg(db, "<arg1>"));
                mve1.appendFormalArg(new IntFormalArg(db, "<arg2>"));
                mve1.appendFormalArg(new NominalFormalArg(db, "<arg3>"));
                mve1.appendFormalArg(new PredFormalArg(db, "<arg4>"));
                mve1.appendFormalArg(new QuoteStringFormalArg(db, "<arg5>"));
                mve1.appendFormalArg(new TimeStampFormalArg(db, "<arg6>"));
                mve1.appendFormalArg(new UnTypedFormalArg(db, "<arg7>"));
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringA = db.toString();

                
                mve2.insertFormalArg(new UnTypedFormalArg(db, "<arg0>"), 0);
                mve2.insertFormalArg(new FloatFormalArg(db, "<arg1>"), 1);
                mve2.insertFormalArg(new IntFormalArg(db, "<arg2>"), 2);
                mve2.insertFormalArg(new NominalFormalArg(db, "<arg3>"), 3);
                mve2.insertFormalArg(new PredFormalArg(db, "<arg4>"), 4);
                mve2.insertFormalArg(new QuoteStringFormalArg(db, "<arg5>"), 5);
                mve2.insertFormalArg(new TimeStampFormalArg(db, "<arg6>"), 6);
                
                db.replaceMatrixVE(mve2);
                mve2 = db.getMatrixVE(mdc2_mveID);
                
                // create the test string
                testStringB = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString4.compareTo(testStringA) != 0 ) ||
                 ( expectedString5.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 2 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString4.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString4.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString5.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString5.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 2: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
         
        
        /* In preparation for further tests, create some new cells and
         * insert them in the data columns.
         */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                /* add some cells to mdc1 */
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            IntDataValue.Construct(db, 11),
                            FloatDataValue.Construct(db, 10.0),
                            IntDataValue.Construct(db, 10),
                            NominalDataValue.Construct(db, "TEN"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    null)),
                            QuoteStringDataValue.Construct(db, "ten"),
                            TimeStampDataValue.Construct(db, 3600),
                            FloatDataValue.Construct(db, 11.0))));
                
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc1ID, 
                        mdc1_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            NominalDataValue.Construct(db, "TWENTY-ONE"),
                            FloatDataValue.Construct(db, 20.0),
                            IntDataValue.Construct(db, 20),
                            NominalDataValue.Construct(db, "TWENTY"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    null,
                                    null)),
                            QuoteStringDataValue.Construct(db, "twenty"),
                            TimeStampDataValue.Construct(db, 7200),
                            QuoteStringDataValue.Construct(db, "twentry-one"))));
                
                // create the test string
                testStringA = db.toString();
                
                
                /* add some cells to mdc2 */
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            IntDataValue.Construct(db, 110),
                            FloatDataValue.Construct(db, 100.0),
                            IntDataValue.Construct(db, 100),
                            NominalDataValue.Construct(db, "HUNDRED"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    null)),
                            QuoteStringDataValue.Construct(db, "hundred"),
                            TimeStampDataValue.Construct(db, 36000),
                            FloatDataValue.Construct(db, 110.0))));
                
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdc2ID, 
                        mdc2_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            NominalDataValue.Construct(db, "TWO-HUNDRED-ONE"),
                            FloatDataValue.Construct(db, 200.0),
                            IntDataValue.Construct(db, 200),
                            NominalDataValue.Construct(db, "TWO-HUNDRED"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    null,
                                    null)),
                            QuoteStringDataValue.Construct(db, "two-hundred"),
                            TimeStampDataValue.Construct(db, 72000),
                            QuoteStringDataValue.Construct(db, "two-hundred-one"))));
                
                // create the test string
                testStringB = db.toString();

                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString6.compareTo(testStringA) != 0 ) ||
                 ( expectedString7.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 3 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString6.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString6.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString7.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString7.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 3: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try deleting some arguments -- all untyped and all unset */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                mve0.deleteFormalArg(1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                // create the test string
                testStringA = db.toString();
                                
                mve0.deleteFormalArg(0);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                // create the test string
                testStringB = db.toString();

                mve0.deleteFormalArg(1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);
                
                // create the test string
                testStringC = db.toString();
                                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString8.compareTo(testStringA) != 0 ) ||
                 ( expectedString9.compareTo(testStringB) != 0 ) ||
                 ( expectedString10.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 4 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString8.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString8.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString9.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString9.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString10.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString10.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 4: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try deleting more arguments -- all typed and/or set */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                mve1.deleteFormalArg(3);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);
                
                // create the test string
                testStringA = db.toString();
                                
                mve1.deleteFormalArg(0);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);
                
                // create the test string
                testStringB = db.toString();

                mve1.deleteFormalArg(5);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);
                
                // create the test string
                testStringC = db.toString();
                                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString11.compareTo(testStringA) != 0 ) ||
                 ( expectedString12.compareTo(testStringB) != 0 ) ||
                 ( expectedString13.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 5 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString11.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString11.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString12.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString12.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString13.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString13.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 5: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try moving some arguments around */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                farg = mve1.getFormalArg(2);
                mve1.deleteFormalArg(2);
                mve1.insertFormalArg(farg, 0);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);
                
                // create the test string
                testStringA = db.toString();
                

                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 0);
                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 1);
                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 2);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);
                                 
                // create the test string
                testStringB = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString14.compareTo(testStringA) != 0 ) ||
                 ( expectedString15.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 6 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString14.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString14.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString15.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString15.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                     
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 6: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* finally, mix things up a bit and see if we get confused */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                /* move arg 3 to the end of the argument list */
                farg = mve2.getFormalArg(3);
                mve2.deleteFormalArg(3);
                mve2.insertFormalArg(farg, 7);
                
                /* insert an argument into the list */
                mve2.insertFormalArg(new NominalFormalArg(db, "<new_arg>"), 3);
                
                /* delete the old first & final arguments in the matrix */
                mve2.deleteFormalArg(7);
                mve2.deleteFormalArg(0);
                
                // apply the modified version to the db
                db.replaceMatrixVE(mve2);
                mve2 = db.getMatrixVE(mdc2_mveID);
                 
                // create the test string
                testStringA = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString16.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 7 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString16.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString16.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                     
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 7: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        return failures;
        
    } /* Datavase::TestMVEModListeners__test_01() */
    
    
    /**
     * TestPVEDeletionListeners()
     *
     * Verify that deletions of predicate vocab elements propagate through
     * the database as expected.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPVEDeletionListeners(java.io.PrintStream outStream,
                                                     boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing predicate vocab element deletion listeners               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        failures += TestPVEDeletionListeners__test_01(outStream, verbose);
        
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

    } /* Database::TestPVEDeletionListeners() */
    
    
    /**
     * TestPVEDeletionListeners__test_01()
     *
     * Initial smoke check on the PVE deletion listeners:
     *
     * Allocate a data base, create a predicate column and a matrix column,
     * several predicates, and a selection of cells in the column with various 
     * predicate values.
     *
     * Delete several predicates to see if the deletions are reflected
     * correctly in the cells.
     *
     * Return the number of failures.
     *
     *                                              JRM -- 4/18/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestPVEDeletionListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
        throws SystemErrorException
    {
        final String header = "test 01: ";
        String systemErrorExceptionString = null;
        String expectedString0 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(pve4(pve0(1.0), " +
                           "pve1(2.0, 2), " +
                           "pve2(3.0, 3, THREE), " +
                           "pve3(4.0, 4, FOUR, \"quarte\"), " +
                           "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(pve0(pve0(pve1(pve0(<arg0>), " +
                           "pve1(pve0(<arg0>), <arg1>)))))), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(pve2(pve0(pve1(pve0(<arg0>), " +
                                "pve1(pve0(<arg0>), " +
                                "<arg1>))), " +
                           "pve1(pve0(pve1(pve0(<arg0>), " +
                                     "pve1(pve0(<arg0>), <arg1>))), " +
                                "pve1(pve0(<arg0>), <arg1>)), " +
                           "\"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(pve4(pve0(10.0), " +
                           "pve1(20.0, 20), " +
                           "pve2(30.0, 30, THIRTY), " +
                           "pve3(40.0, 40, FOURTY, \"forty\"), " +
                           "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(pve0(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>)))))), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(pve2(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>))), " +
                           "pve1(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>))), " +
                                "pve1(pve0(<arg0>), <arg1>)), " +
                           "\"seventy\")))))))))";
        String expectedString1 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>), " +
                 "pve1(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg0>, " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "pve1(2.0, 2), " +
                          "pve2(3.0, 3, THREE), " +
                          "pve3(4.0, 4, FOUR, \"quarte\"), " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, pve1(<arg0>, <arg1>)), " +
                          "\"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, <arg1>), " +
                          "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "pve1(20.0, 20), " +
                          "pve2(30.0, 30, THIRTY), " +
                          "pve3(40.0, 40, FOURTY, \"forty\"), " +
                          "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (())), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, pve1(<arg0>, <arg1>)), " +
                          "\"seventy\")))))))))";
        String expectedString2 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(3.0, 3, THREE), " +
                          "pve3(4.0, 4, FOUR, \"quarte\"), " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                   "(2, 00:00:01:000, 00:00:02:000, (())), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(30.0, 30, THIRTY), " +
                          "pve3(40.0, 40, FOURTY, \"forty\"), " +
                          "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (())), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"seventy\")))))))))";
        String expectedString3 = 
          "(Undefined " +
            "((VocabList) " +
                "(vl_contents: " +
                  "(mdc(<arg>), " +
                   "pdc(<arg>), " +
                   "pve2(<arg0>, <arg1>, <arg2>), " +
                   "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(3.0, 3, THREE), " +
                          "<arg4>, " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                  "(2, 00:00:01:000, 00:00:02:000, (())), " +
                  "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                  "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                  "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(30.0, 30, THIRTY), " +
                          "<arg4>, " +
                         "\"fifty\"))), " +
                  "(6, 00:00:05:000, 00:00:06:000, (())), " +
                  "(7, 00:00:06:000, 00:00:07:000, " +
                   "(pve2(<arg0>, <arg1>, \"seventy\")))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdcID = DBIndex.INVALID_ID;
        long mdc_mveID = DBIndex.INVALID_ID;
        long pdcID = DBIndex.INVALID_ID;
        long pdc_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc = null;
        DataColumn pdc = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                // allocate a new database
                
                db = new ODBCDatabase();
                
                
                // create a selection of predicates
                
                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);
                
                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                
                pve3 = new PredicateVocabElement(db, "pve3");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg4>");
                pve3.appendFormalArg(farg);
                pve3ID = db.addPredVE(pve3);
                pve3 = db.getPredVE(pve3ID);
                
                pve4 = new PredicateVocabElement(db, "pve4");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg4>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg5>");
                pve4.appendFormalArg(farg);
                pve4ID = db.addPredVE(pve4);
                pve4 = db.getPredVE(pve4ID);
                
                
                // create a couple of Data columns
                
                mdc = new DataColumn(db, "mdc", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdcID = db.addColumn(mdc);
                mdc = db.getDataColumn(mdcID);
                mdc_mveID = mdc.getItsMveID();
                
                pdc = new DataColumn(db, "pdc", 
                                     MatrixVocabElement.matrixType.PREDICATE);
                pdcID = db.addColumn(pdc);
                pdc = db.getDataColumn(pdcID);
                pdc_mveID = pdc.getItsMveID();
                
                
                // create a selection of cells
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve4ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            FloatDataValue.Construct(db, 1.0))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            FloatDataValue.Construct(db, 2.0),
                                            IntDataValue.Construct(db, 2))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            FloatDataValue.Construct(db, 3.0),
                                            IntDataValue.Construct(db, 3),
                                            NominalDataValue.Construct(
                                                db, 
                                                "THREE"))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve3ID,
                                            FloatDataValue.Construct(db, 4.0),
                                            IntDataValue.Construct(db, 4),
                                            NominalDataValue.Construct(
                                                db, 
                                                "FOUR"),
                                            QuoteStringDataValue.Construct(
                                                db, "quarte"))),
                                    QuoteStringDataValue.Construct(
                                        db, "quint"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        300,
                        360,
                        Matrix.Construct(
                          db,
                          mdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve0ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        360,
                        420,
                        Matrix.Construct(
                          db,
                          mdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve2ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))),
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                              db,
                                              pve1ID,
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve0ID,
                                                  null)),
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve1ID,
                                                 PredDataValue.Construct(
                                                    db,
                                                    Predicate.Construct(
                                                    db,
                                                    pve0ID,
                                                    null)),
                                                  null)))))),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))),
                                  QuoteStringDataValue.Construct(
                                    db, "septime"))))));
                
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve4ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            FloatDataValue.Construct(
                                                db, 
                                                10.0))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            FloatDataValue.Construct(db, 20.0),
                                            IntDataValue.Construct(db, 20))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            FloatDataValue.Construct(db, 30.0),
                                            IntDataValue.Construct(db, 30),
                                            NominalDataValue.Construct(
                                                db, 
                                                "THIRTY"))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve3ID,
                                            FloatDataValue.Construct(db, 40.0),
                                            IntDataValue.Construct(db, 40),
                                            NominalDataValue.Construct(
                                                db, 
                                                "FOURTY"),
                                            QuoteStringDataValue.Construct(
                                                db, "forty"))),
                                    QuoteStringDataValue.Construct(
                                        db, "fifty"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        300,
                        360,
                        Matrix.Construct(
                          db,
                          pdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve0ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        360,
                        420,
                        Matrix.Construct(
                          db,
                          pdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve2ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))),
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                              db,
                                              pve1ID,
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve0ID,
                                                  null)),
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve1ID,
                                                 PredDataValue.Construct(
                                                    db,
                                                    Predicate.Construct(
                                                    db,
                                                    pve0ID,
                                                    null)),
                                                  null)))))),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))),
                                  QuoteStringDataValue.Construct(
                                    db, "seventy"))))));
                
                // create the test string
                testStringA = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }
                    
                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }
                    
                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* Now delete some predicate vocab elements */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                db.removePredVE(pve0ID);
                
                // create the test string
                testStringA = db.toString();
                

                db.removePredVE(pve1ID);;
                
                // create the test string
                testStringB = db.toString();
                

                db.removePredVE(pve3ID);;
                
                // create the test string
                testStringC = db.toString();
                
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
                
        return failures;
        
    } /* Datavase::TestPVEDeletionListeners__test_01() */


    /**
     * TestPVEModListeners()
     *
     * Verify that modifications in predicate vocab elements propogate through
     * the database as expected.
     *
     *
     * Tests are as follows.  Note that we start with relatively simple tests,
     * and add to the complexity.
     *
     * 1) Allocate a data base, create a predicate column, several predicates,
     *    and a selection of cells in the column with various predicate values.
     *    Add, & delete formal arguments to some of the predicates, and verify
     *    that the changes are reflected correctly in the cells.  Re-arrange
     *    formal arguemnst and verify that the changes are reflected correctly
     *    in the cells.  Combine the above and verify the expected results.
     *
     *                                              JRM -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPVEModListeners(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing predicate vocab element modification listeners           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        failures += TestPVEModListeners__test_01(outStream, verbose);
        
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

    } /* Database::TestPVEModListeners() */
    
    
    /**
     * TestPVEModListeners__test_01()
     *
     * Initial smoke check on the PVE mod listeners:
     *
     * Allocate a data base, create a predicate column, several predicates,
     * and a selection of cells in the column with various predicate values.
     * Add, & delete formal arguments to some of the predicates, and verify
     * that the changes are reflected correctly in the cells.  Re-arrange
     * formal arguemnst and verify that the changes are reflected correctly
     * in the cells.  Combine the above and verify the expected results.
     *
     * Return the number of failures.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestPVEModListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
        throws SystemErrorException
    {
        final String header = "test 01: ";
        String systemErrorExceptionString = "";
        String expectedString0 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), " +
                        "pve1(<arg0>, <arg1>), " +
                        "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString1 =  
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString2 =  
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                   "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString3 =  
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg0.5>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                          "pve1(<arg0>, <arg0.5>, <arg1>), " +
                          "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString4 =  
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>))))))))))";
        String expectedString5 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg0>, <arg1>), pve2(<arg0>, <arg2>))))))))))";
        String expectedString6 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve2(<arg0>, <arg2>))))))))))";
        String expectedString7 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, <arg1>, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg0>), " +
                          "<arg1>, " +
                          "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, <arg1>, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "<arg1>, " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString8 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg2>, <arg1>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                    "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                    "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3, <arg1>))), " +
                    "(4, 00:00:03:000, 00:00:04:000, " +
                      "(pve2(pve0(<arg0>), " +
                            "pve2(<arg0>, <arg2>, <arg1>), " +
                            "<arg1>))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>)))))))))";
        String expectedString9 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg0>, <arg2>, <arg1>), " +
                 "pve1(<arg0.5>, <arg1>, <arg0>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(<arg0.5>, 2, 1))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(<arg0.5>, bravo, alpha))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, charlie, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>)))))))))";
        String expectedString10 = 
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<arg>), " +
                 "pdc(<arg>), " +
                 "pve2(<arg3>, <arg1>, <arg0>), " +
                 "pve1(<arg0.5>, <arg1>, <arg0>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(<arg0.5>, 2, 1))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(<arg3>, <arg1>, 1))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg3>, <arg1>, pve0(<arg0>)))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(<arg0.5>, bravo, alpha))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(<arg3>, <arg1>, alpha))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg3>, <arg1>, pve0(<arg0>))))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdcID = DBIndex.INVALID_ID;
        long mdc_mveID = DBIndex.INVALID_ID;
        long pdcID = DBIndex.INVALID_ID;
        long pdc_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc = null;
        DataColumn pdc = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                // allocate a new database
                
                db = new ODBCDatabase();
                
                
                // create a selection of predicates
                
                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);
                
                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                
                
                // create a couple of Data columns
                
                mdc = new DataColumn(db, "mdc", 
                                     MatrixVocabElement.matrixType.MATRIX);
                mdcID = db.addColumn(mdc);
                mdc = db.getDataColumn(mdcID);
                mdc_mveID = mdc.getItsMveID();
                
                pdc = new DataColumn(db, "pdc", 
                                     MatrixVocabElement.matrixType.PREDICATE);
                pdcID = db.addColumn(pdc);
                pdc = db.getDataColumn(pdcID);
                pdc_mveID = pdc.getItsMveID();
                
                
                // create a selection of cells
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        mdcID, 
                        mdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db, 
                        pdcID, 
                        pdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                
                // create the test string
                testStringA = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }
                    
                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }
                    
                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try adding some arguments */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                pve0.appendFormalArg(new UnTypedFormalArg(db, "<arg1>"));
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                // create the test string
                testStringA = db.toString();

                pve0.insertFormalArg(new UnTypedFormalArg(db, "<arg-1>"), 0);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                // create the test string
                testStringB = db.toString();
                
                pve1.insertFormalArg(new UnTypedFormalArg(db, "<arg0.5>"), 1);
                db.replacePredVE(pve1);
                pve1 = db.getPredVE(pve1ID);
                
                testStringC = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try deleting some arguments */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                pve2.deleteFormalArg(1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                
                // create the test string
                testStringA = db.toString();

                pve0.deleteFormalArg(0);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                // create the test string
                testStringB = db.toString();
                
                pve0.deleteFormalArg(1);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);
                
                testStringC = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString4.compareTo(testStringA) != 0 ) ||
                 ( expectedString5.compareTo(testStringB) != 0 ) ||
                 ( expectedString6.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 2 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString4.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString4.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString5.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString5.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString6.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString6.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 2: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* try moving some arguments around -- will have to add first */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                pve2.insertFormalArg(new UnTypedFormalArg(db, "<arg1>"), 1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                
                // create the test string
                testStringA = db.toString();

                
                farg = pve2.getFormalArg(2);
                pve2.deleteFormalArg(2);
                pve2.insertFormalArg(farg, 1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                 
                // create the test string
                testStringB = db.toString();
                
                
                farg = pve1.getFormalArg(0);
                pve1.deleteFormalArg(0);
                pve1.insertFormalArg(farg, 2);
                db.replacePredVE(pve1);
                pve1 = db.getPredVE(pve1ID);
                 
                // create the test string
                testStringC = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString7.compareTo(testStringA) != 0 ) ||
                 ( expectedString8.compareTo(testStringB) != 0 ) ||
                 ( expectedString9.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 3 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString7.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString7.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                    
                    if ( expectedString8.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString8.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }
                    
                    if ( expectedString9.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString9.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }
                     
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 3: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        
        /* finally, mix things up a bit and see if we get confused */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                // move arg0 to the end of the arg list
                farg = pve2.getFormalArg(0);
                pve2.deleteFormalArg(0);
                pve2.insertFormalArg(farg, 2);
                
                // insert arg4 at the beginning of the arg list
                pve2.insertFormalArg(new UnTypedFormalArg(db, "<arg3>"), 0);
                
                // delete arg2
                pve2.deleteFormalArg(1);
                
                // apply the modified version to the db
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);
                 
                // create the test string
                testStringA = db.toString();
                
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( expectedString10.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 4 failed to complete.\n",
                                         header);
                    }
                    
                    if ( expectedString10.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString10.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }
                     
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 4: \"%s\".\n", 
                                header, systemErrorExceptionString);
                    }
                }
            }
        }
        
        return failures;
        
    } /* Datavase::TestPVEModListeners__test_01() */
    
} /* class Database */
