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
    // TODO: must create listener class for changes in db configuration.
    //       Listener should report changes in name, description, curUID,
    //       others?

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
    throws SystemErrorException, LogicErrorException {
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
        throws SystemErrorException, LogicErrorException
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
        else if ( dc.getItsMveType() == MatrixVocabElement.MatrixType.UNDEFINED )
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
        if ( dc.getItsMveType() != MatrixVocabElement.MatrixType.MATRIX )
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
     *    - Modified the method to return an empty vector if there are not
     *      Columns.
     *                                              JRM -- 11/24/08
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
     *    - Modified the method to return an empty vector if there are no
     *      DataColumns.
     *                                              JRM -- 11/24/08
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
     *    - Modified the method to return an empty vector if there are no
     *      DataColumns.
     *                                              JRM -- 11/24/08
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
                      MatrixVocabElement.MatrixType.UNDEFINED )
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
    throws SystemErrorException, LogicErrorException {
        final String mName = "Database::addMatrixVE(mve): ";
        MatrixVocabElement local_mve = null;

        if ( mve == null )
        {
            throw new SystemErrorException(mName + "null mve.");
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
        else if ( (local_mve = new MatrixVocabElement(mve)) == null )
        {
            throw new SystemErrorException(mName + "couldn't copy mve");
        }

        this.vl.replaceVocabElement(local_mve);

        return;

    } /* Database::replaceMatrixVE(mve) */

    public void replaceVocabElement(final VocabElement ve)
    throws SystemErrorException {       
        try {
            if (ve == null) {
                throw new SystemErrorException("Cant to replace vocab element");
            }

            VocabElement local_mve = (VocabElement) ve.clone();
            this.vl.replaceVocabElement(local_mve);

        } catch (CloneNotSupportedException e) {
            throw new SystemErrorException("Cant to replace vocab element");
        }
    }


    /**
     * Adds a vocab element to the database.
     *
     * @param ve The vocab element to add to the database.
     * @return The ID of the vocab element within the database.
     * @throws au.com.nicta.openshapa.db.SystemErrorException If unable to add
     * the vocab element to the database.
     */
    public long addVocabElement(final VocabElement ve)
    throws SystemErrorException, LogicErrorException {
        try {
            // Throw an error if the vocab element is null.
            if (ve == null) {
                throw new SystemErrorException("Unable to add ve - it is null");
            }

            // Create a copy of the vocab element and add it to the database
            //vocab.
            VocabElement copy = (VocabElement) ve.clone();
            this.vl.addElement(copy);
            return copy.getID();
        } catch (CloneNotSupportedException e) {
            throw new SystemErrorException(e.toString());
        }
    }


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
    throws SystemErrorException, LogicErrorException {
        final String mName = "Database::addPredVE(pve): ";
        PredicateVocabElement local_pve = null;

        if ( pve == null )
        {
            throw new SystemErrorException(mName + "null pve.");
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

    public void removeVE(long targetID) throws SystemErrorException {
        this.vl.removeVocabElement(targetID);
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

} /* class Database */
