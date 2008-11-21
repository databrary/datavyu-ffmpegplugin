/*
 * ColumnList.java
 *
 * Created on August 31, 2007, 9:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Class ColumnList
 *
 * A singleton instance of ColumnList is used to maintain the list of 
 * columns (AKA spreadsheet variables) for an OpenSHAPA database.
 *
 * Note that while the ColumnList is quite similar to the DBIndex class, it 
 * isn't close enough to be a subclass.
 *
 *                                          JRM -- 8/31/07
 *
 * @author mainzer
 */
public class ColumnList
{
    
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     *
     * db:  Reference to the instance of Database of which this vocab list
     *      is part.
     *
     * cl:  Hashtable containg references to all instances of Column that
     *      constitute the column list.
     *
     * nameMap: Hashmap mapping column names to column IDs.
     *      This mapping is used both to allow lookups by column name,
     *      and to determine if a column name is in use.
     *
     * listeners: Instance of ColumnListListeners use to maintain lists of 
     *      listeners for ColumnList insertions and deletions, and issue
     *      notifications as appropriate.
     */
    
    /** Reference to the Database of which this instance is part */
    protected Database db = null;

    /** Index of all instances of Column in the column list */
    protected Hashtable<Long, Column> cl = new Hashtable<Long, Column>();
    
    protected HashMap<String, Long> nameMap = new HashMap<String, Long>();
          
    /**
     * instance of ColumnListListeners used to maintain lists of listeners,
     *  and notify them as appropriate.
     */
    protected ColumnListListeners listeners = null;
          
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
     
    /**
     * ColumnList()
     *
     * Constructor for the ColumnList class.  
     *                                             JRM -- 4/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected ColumnList(Database db)
         throws SystemErrorException
    {
        super();

        final String mName = "ColumnList::ColumnList(db): ";

        if ( ( db == null ) ||
             ( ! ( db instanceof Database ) ) )
        {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;
        
        this.listeners = new ColumnListListeners(db, this);

        return;
        
    } /* ColumnList::ColumnList(db) */
     
     
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * toString() -- overrride 
     * 
     * Returns a String representation of the contents of the column list.<br>
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
    public String toString() 
    {
        boolean first = true;
        String s;
        Column col;
        java.util.Enumeration<Column> columns;
        
        s = "((ColumnList) (cl_contents: (";
        
        columns = cl.elements();

        while ( columns.hasMoreElements() )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                s += ", ";
            }
            col = columns.nextElement();
            s += col.toString();
        }
        s += ")))";
        
        return s;
        
    } /* ColumnList::toString() */
        
        
    /*************************************************************************/
    /********************* Listener Manipulation: ****************************/
    /*************************************************************************/
    
    /**
     * deregisterExternalListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the deregister external change listeners message on to  
     * the instance of ColumnListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterExternalListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "ColumnList::deregisterExternalChangeListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "this.listeners is null!?!");
        }
        
        this.listeners.deregisterExternalListener(el);
        
        return;
        
    } /* ColumnList::deregisterExternalListener() */
    
    
    /**
     * deregisterInternalListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the deregister internal change listeners message on to  
     * the instance of ColumnListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "ColumnList::deregisterInternalListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + 
                "Attempt to add internal listener to non-cannonical version.");
        }
        
        this.listeners.deregisterInternalListener(id);
        
        return;
        
    } /* ColumnList::deregisterInternalListener() */
    
    
    /**
     * registerExternalListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the register external change listeners message on to the 
     * instance of ColumnListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/11/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerExternalListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "ColumnList::registerExternalListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "listeners == null!?!?");
        }
        
        this.listeners.registerExternalListener(el);
        
        return;
        
    } /* ColumnList::registerExternalListener() */
    
    
    /**
     * registerInternalListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the register internal change listeners message on to the 
     * instance of ColumnListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "ColumnList::registerInternalListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "this.listeners == null?!?!");
        }
        
        this.listeners.registerInternalListener(id);
        
        return;
        
    } /* ColumnList::addInternalListener() */
    

    /*************************************************************************/
    /****************************** Methods: *********************************/
    /*************************************************************************/
         
    /**
     * addColumn()
     *
     * If the column is a DataColumn, verify that its associated matrix 
     * vocab element exists and is in the vocab list.
     *
     * Insert the Column into the index, and into the column list.  Call the
     * columns constructItsCells() method.
     *
     *                                                 JRM -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to create and assign an instance of DataColumnListeners
     *      to a newly inserted DataColumn.  Also added code to notify column
     *      list listeners of the column addition.  Finally, added calls to 
     *      mark the beginning and end of any resulting cascade of changes.
     *
     *                                                  JRM -- 2/11/08
     *
     *    - Added code to register the column as an internal listener of its
     *      matrix vocab element.
     *                                                  JRM -- 3/22/08
     */
     
    protected void addColumn(Column col)
       throws SystemErrorException
    {
        final String mName = "ColumnList::addColumn(col): ";
        long mveID;
        DataColumn dc = null;
        DBElement dbe;
        MatrixVocabElement mve = null;
          
        if ( ( col == null ) ||
             ( ! ( col instanceof Column ) ) )
        {
            throw new SystemErrorException(mName + "Bad col param");
        }
        else if ( col.getDB() != db )
        {
            throw new SystemErrorException(mName + "col.getDB() != db");
        }
        else if ( col.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "col.getID() != INVALID_ID");
        }
        else if ( this.cl.containsValue(col) )
        {
            throw new SystemErrorException(mName + "col already in cl?!?");
        }
        else if ( this.nameMap.containsKey(col.name) )
        {
            throw new SystemErrorException(mName + "col name in use!?!");
        }
        
        if ( col instanceof DataColumn )
        {
            dc = (DataColumn)col;
            mveID = dc.getItsMveID();
            
            if ( mveID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "dc.itsMveID is invalid");
            }
            
            dbe = this.db.idx.getElement(mveID);
            
            if ( dbe == null )
            {
                throw new SystemErrorException(mName + 
                        "dc.itsMveID has no referent");
            }
            
            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName + 
                        "dc.itsMveID does not refer to a MatrixVocabElement");
            }
            
            dc = (DataColumn)col;
            mve = (MatrixVocabElement)dbe;
            
            if ( dc.name.compareTo(mve.getName()) != 0 )
            {
                throw new SystemErrorException(mName + "name mismatch");
            }
        
            if ( mve.getType() != dc.getItsMveType() )
            {
                throw new SystemErrorException(mName + "type mismatch");
            }
        }
        
        this.db.cascadeStart();
        
        this.db.idx.addElement(col);
        
        this.cl.put(col.getID(), col);
        
        this.nameMap.put(col.getName(), col.getID());
        
        col.constructItsCells();
        
        if ( col instanceof DataColumn )
        {
            DataColumnListeners nl = null;

            nl = new DataColumnListeners(db, (DataColumn)col);
            ((DataColumn)col).setListeners(nl);
        }
        
        col.register();
        
        this.listeners.notifyListenersOfColInsertion(col.getID());
        
        this.db.cascadeEnd();
        
        this.db.registerInternalCascadeListener(col.getID());
        
        return;
       
    } /* ColumnList::addColumn(col) */
    
    
    /**
     * applyTemporalOrdering()
     *
     * Tell each column in the column list to sort its cells in onset order.
     *
     *                                              JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void applyTemporalOrdering()
        throws SystemErrorException
    {
        final String mName = "ColumnList::applyTemporalOrdering(): ";
        Column col;
        java.util.Enumeration<Column> columns;

        this.db.cascadeStart();
        
        columns = cl.elements();
        
        while ( columns.hasMoreElements() )
        {
            col = columns.nextElement();
            col.sortItsCells();
        }
        
        this.db.cascadeEnd();
        
    } /* ColumnList::applyTemporalOrdering() */
     
    /**
     * getColumn(targetID)
     *
     * Get the instance of Column corresponding with the supplied id.
     *
     *                                                 JRM -- 8/31/07
     *
     * Changes:
     *
     *   - None.
     */
     
    protected Column getColumn(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::getVocabElement(targetID): ";
        Column col = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
         
        col = cl.get(targetID);
         
        if ( col == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
         
        return col;
         
    } /* ColumnList::getColumn(targetID) */
 
    
    /**
     * getColumn(targetName)
     *
     * Get the instance of Column corresponding with the supplied name.
     *
     *                                                  JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected Column getColumn(String targetName)
        throws SystemErrorException
    {
        final String mName = "ColumnList::getColumn(targetName): ";
        Long targetID;
        Column col = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ! Database.IsValidSVarName(targetName) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( ! nameMap.containsKey(targetName) )
        {
            throw new SystemErrorException(mName + "targetName not in nameMap");
        }

        targetID = this.nameMap.get(targetName);
            
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "targetID == INVALID_ID");
        }
          
        col = cl.get(targetID);
         
        if ( col == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        return col;
        
    } /* ColumnList::getColumn(targetName) */
    
    
    /**
     * getColumns
     *
     * Construct and return a vector containing copies of all Columns 
     * in the column list.  If the column list is empty, return null.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - none.
     */
    
    protected java.util.Vector<Column> getColumns()
        throws SystemErrorException
    {
        final String mName = "ColumnList::getColumns(): ";
        java.util.Vector<Column> cols = new java.util.Vector<Column>();
        Column col;
        Column copy;

        java.util.Enumeration<Column> entries;
        
        entries = this.cl.elements();
        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();
            
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
                throw new SystemErrorException(mName + "Unknown Column type");
            }
            
            if ( cols == null )
            {
                cols = new java.util.Vector<Column>();
            }

            cols.add(copy);
        }
        
        return cols;
        
    } /* ColumnList::getColumns() */
    
    
    /**
     * getDataColumns
     *
     * Construct and return a vector containing copies of all DataColumns 
     * in the column list.  If the column list is empty, return null.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - none.
     */
    
    protected java.util.Vector<DataColumn> getDataColumns()
        throws SystemErrorException
    {
        java.util.Vector<DataColumn> cols = null;
        Column col;
        DataColumn dc;
        java.util.Enumeration<Column> entries;
        
        entries = this.cl.elements();
        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();
            
            if ( col instanceof DataColumn )
            {
                dc = (DataColumn)col;
                
                if ( cols == null )
                {
                    cols = new java.util.Vector<DataColumn>();
                }

                cols.add(new DataColumn(dc));
            }
        }
        
        return cols;
        
    } /* ColumnList::getDataColumns() */
    
    
    /**
     * getReferenceColumns
     *
     * Construct and return a vector containing copies of all DataColumns 
     * in the column list.  If the column list is empty, return null.
     *
     *                                                  JRM -- 8/31/07
     *
     * Changes:
     *
     *    - none.
     */
    
    protected java.util.Vector<ReferenceColumn> getReferenceColumns()
        throws SystemErrorException
    {
        java.util.Vector<ReferenceColumn> cols = null;
        Column col;
        ReferenceColumn rc;
        java.util.Enumeration<Column> entries;
        
        entries = this.cl.elements();
        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();
            
            if ( col instanceof ReferenceColumn )
            {
                rc = (ReferenceColumn)col;
                
                if ( cols == null )
                {
                    cols = new java.util.Vector<ReferenceColumn>();
                }

                cols.add(new ReferenceColumn(rc));
            }
        }
        
        return cols;
        
    } /* ColumnList::getReferenceColumns() */
    
    
    /**
     * inColumnList(targetID)
     *
     * Return true if the column list contains an entry matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean inColumnList(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::inColumnList(targetID): ";
        boolean inCL = false;
        
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.cl.containsKey(targetID) )
        {
            inCL = true;
        }
        
        return inCL;
        
    } /* ColumnList::inColumnList(targetID) */
    
    
    /**
     * inColumnList(targetName)
     *
     * Return true if the supplied column name currently appears
     * in the column list, and false otherwise.
     *
     *                                              JRM -- 6/3/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean inColumnList(String targetName)
        throws SystemErrorException
    {
        final String mName = "ColumnList::inColumnList(targetName): ";
        boolean inUse = false;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ! Database.IsValidSVarName(targetName) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( nameMap.containsKey(targetName) )
        {
            inUse = true;
        }
        
        return inUse;
        
    } /* ColumnList::inColumnList(targetName) */
    
    
    /**
     * dataColumnInColumnList(targetID)
     *
     * Return true if the column list contains a data column matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean dataColumnInColumnList(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::dataColumnInColumnList(targetID): ";
        boolean inCL = false;
        Column col = null;
                
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.cl.containsKey(targetID) )
        {
            col = this.cl.get(targetID);
            
            if ( col == null )
            {
                throw new SystemErrorException(mName + "col == null");
            }
            else if ( col instanceof DataColumn )
            {
                inCL = true;
            }
        }
        
        return inCL;
        
    } /* ColumnList::dataColumnInColumnList(targetID) */
    
    
    /**
     * dataColumnInColumnList(targetName)
     *
     * Return true if the column list contains a data column matching the 
     * provided name.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean dataColumnInColumnList(String targetName)
       throws SystemErrorException
    {
        final String mName = "ColumnList::dataColumnInColumnList(targetName): ";
        boolean inCL = false;
        long targetID;
        Column col = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ! Database.IsValidSVarName(targetName) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( this.nameMap.containsKey(targetName) )
        {
            targetID = this.nameMap.get(targetName);

            if ( targetID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "targetID == INVALID_ID");
            }

            col = this.cl.get(targetID);

            if ( col == null )
            {
                throw new SystemErrorException(mName + "col == null");
            }
            else if ( col instanceof DataColumn )
            {
                inCL = true;
            }
        }
        
        return inCL;
        
    } /* ColumnList::dataColumnInColumnList(targetName) */
    
    
    /**
     * referenceColumnInColumnList(targetID)
     *
     * Return true if the column list contains a reference column matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean referenceColumnInColumnList(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::referenceColumnInColumnList(targetID): ";
        boolean inCL = false;
        Column col = null;
                
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.cl.containsKey(targetID) )
        {
            col = this.cl.get(targetID);
            
            if ( col == null )
            {
                throw new SystemErrorException(mName + "col == null");
            }
            else if ( col instanceof ReferenceColumn )
            {
                inCL = true;
            }
        }
        
        return inCL;
        
    } /* ColumnList::referenceColumnInColumnList(targetID) */
    
    
    /**
     * referenceColumnInColumnList(targetName)
     *
     * Return true if the column list contains a data column matching the 
     * provided name.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean referenceColumnInColumnList(String targetName)
       throws SystemErrorException
    {
        final String mName = 
                "ColumnList::referenceColumnInColumnList(targetName): ";
        boolean inCL = false;
        long targetID;
        Column col = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ! Database.IsValidSVarName(targetName) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( this.nameMap.containsKey(targetName) )
        {
            targetID = this.nameMap.get(targetName);

            if ( targetID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "targetID == INVALID_ID");
            }

            col = this.cl.get(targetID);

            if ( col == null )
            {
                throw new SystemErrorException(mName + "col == null");
            }
            else if ( col instanceof ReferenceColumn )
            {
                inCL = true;
            }
        }
        
        return inCL;
        
    } /* ColumnList::referenceColumnInColumnList(targetName) */
    
     
    /**
     * removeColumn()
     *
     * Verify that the Column indicated by the targetID is in the column list
     * and that it is empty (that is, it has no cells).  Throw a system error 
     * if it is not.
     *
     * Then remove the target Column from the column list and from the index.
     *
     *                                                 JRM -- 8/31/07
     *
     * Changes:
     *
     *    - Added code to remove the listeners just before a data column is
     *      removed and to notify its listeners of the impending deletion.
     *      Added code to notify column list listeners of the column removal.
     *      Finally added calls to mark the beginning and end of any resulting
     *      cascade of changes.
     *                                                  JRM -- 2/11/08
     */
     
    protected void removeColumn(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::removeColumn(targetID): ";
        int i;
        long id;
        FormalArgument fArg;
        Column col = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( ! this.cl.containsKey(targetID) )
        {
            throw new SystemErrorException(mName + 
                                           "targetID not in vocab list.");
        }
        
        /* verify that the column is empty before proceeding
         */
        col = this.cl.get(targetID);
        
        if ( col == null )
        {
            throw new SystemErrorException(mName + "cl.get(targetID) == null??");
        }
        else if ( col.getNumCells() != 0 )
        {
            throw new SystemErrorException(mName + "col.numCells != 0");
        }
        
        
        /* notify listeners */
        this.db.cascadeStart();
        
        if ( col instanceof DataColumn )
        {
            ((DataColumn)col).notifyListenersOfDeletion();
            ((DataColumn)col).setListeners(null);
        }
        
        this.listeners.notifyListenersOfColDeletion(col.getID());
        
        col.deregister();
        
        
        /* go ahead and remove */
        
        if ( (col = this.cl.remove(targetID)) == null )
        {
            throw new SystemErrorException(mName + "cl.remove() failed.");
        }
        
        this.db.idx.removeElement(targetID);
        
        this.nameMap.remove(col.getName());
         
        this.db.deregisterInternalCascadeListener(col.getID());
       
        this.db.cascadeEnd();
         
        return;
     
    } /* ColumnList::removeColumn(targetID) */
    
     
    /**
     * replaceDataColumn(new_dc)
     *
     * Search the column list for an instance of DataColumn with the same id 
     * as that of the supplied instance.  Verify that this instance has the 
     * the same value in its itsMveID field as the supplied value.
     *
     * Replace the old version of the DataColumn with the new, but copy the 
     * itsCells field into the new version, and update the numCells fields of 
     * the new version with the correct value.
     * 
     * Replace the old representation of the DataColumn header with the new 
     * representation in the column list and in the index.
     *
     * N.B. This method must be used to replace a DataColumn with a modified
     *      version of itself only.  If you play with IDs and try to use it to 
     *      replace one DataColumn with another, it will choke.
     *
     *      If the column name has changed, this change must be made in the 
     *      associated MatrixVocabElement as well, and vise versa.  This 
     *      method doesn't deal with this issue -- we handle it at a higher 
     *      level.
     *
     *                                                 JRM -- 8/31/07
     *
     * Changes:
     *
     *    - Added code to transfer the listeners from the old incarnation of
     *      the data column to the new, and to notify the listeners of the
     *      changes.  Added calls to mark the beginning and end of any resulting
     *      cascade of changes.
     *                                                  JRM -- 2/5/08
     */

    protected void replaceDataColumn(DataColumn new_dc, boolean cascade)
       throws SystemErrorException
    {
        final String mName = "ColumnList::replaceDataColumn(dbe): ";
        boolean matchFound = false;
        Column col;
        DataColumn old_dc = null;
          
        if ( ( new_dc == null ) || ( ! ( new_dc instanceof DataColumn ) ) )
        {
            throw new SystemErrorException(mName + "Bad new_dc param");
        }
        else if ( new_dc.getDB() != this.db )
        {
            throw new SystemErrorException(mName + "new_dc.getDB() != this.db");
        }
        else if ( new_dc.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "new_dc.getID() == INVALID_ID");
        }
        else if ( new_dc.getItsCells() != null )
        {
            throw new SystemErrorException(mName + 
                                           "new_dc.getItsCells() != null");
        }
         
        col = this.cl.get(new_dc.getID());
         
        if ( col == null )
        {
            throw new SystemErrorException(mName +
                    "can't replace -- not in column list.");
        }
        else if ( ! ( col instanceof DataColumn) )
        {
            throw new SystemErrorException(mName + "type mis-match.");
        }
        
        old_dc = (DataColumn)col;
        
        if ( new_dc.getItsMveID() != old_dc.getItsMveID() )
        {
            throw new SystemErrorException(mName + "itsMveID mis-match.");
        }

        if ( new_dc.getItsMveType() != old_dc.getItsMveType() )
        {
            throw new SystemErrorException(mName + "itsMveType mis-match.");
        }
        
        /* Verify that the name of the old_dc is mapped to the ID of the 
         * old_dc in the name map.  If new_dc.name != old_dc.name, we will
         * verify that new_dc.name is valid and that it is not in use shortly.
         */
        if ( ( ! this.nameMap.containsKey(old_dc.getName()) ) ||
             ( this.nameMap.get(old_dc.name) != old_dc.getID() ) )
        {
            throw new SystemErrorException(mName + 
                    "old_dc.name not mapped to old_dc.id in name map.");
        }
        
        if ( new_dc.getName().compareTo(old_dc.getName()) != 0 )
        {
            // verify that the new name is valid and not in use. 
            // Here we just check validity and whether the name appears
            // in the column list.  Will check VocabElement list later
            // if the associated MatrixVocabElement hasn't already been 
            // updated for the change.
            
            if ( ( ! ( this.db.IsValidSVarName(new_dc.getName() ) ) ) ||
                 ( this.inColumnList(new_dc.getName() ) ) ) 
            {
                throw new SystemErrorException(mName + 
                        "new_dc.name invalid or in use");
            }
            
            // name change that must be propagated to the mve associated 
            // with the column.  Verify that this has been done.
            MatrixVocabElement mve;
            
            // mve is a reference to the actual entry in the VocabList.
            // we must be careful not to modify it.
            mve = this.db.vl.getMatrixVocabElement(new_dc.getItsMveID());
            
            if ( mve == null )
            {
                throw new SystemErrorException(mName + 
                        "new_dc.itsMveID has no referent?!?");
            }
            
            if ( mve.getName().compareTo(new_dc.getName()) != 0 )
            {
                throw new SystemErrorException(mName + 
                        "vl hasn't been updated for a column name change");
            }
        }
        
        this.db.cascadeStart();
        
        /* Copy the reference to the cells vector from the old version of 
         * the DataColumn into the new.  SetItsCells() updates new_dc.numCells
         * in passing.
         */
        new_dc.setItsCells(old_dc.getItsCells());

        
        /* Move the listeners from the old incarnation to the new */
        
        new_dc.setListeners(old_dc.getListeners());
        old_dc.setListeners(null);

        
        /* finally, replace the old DataColumn with the new in the 
         * column list.  Similarly, replace the DataColumn in the index.  If 
         * the name has changed, replace the old name with the new in the name 
         * index.
         */

        if ( this.cl.put(new_dc.getID(), new_dc) != old_dc )
        {
            throw new SystemErrorException(mName + "replace failed.");
        }
        
        this.db.idx.replaceElement(new_dc);
        
        if ( old_dc.getName().compareTo(new_dc.getName()) != 0 )
        {
            this.nameMap.remove(old_dc.getName());
            
            if ( this.nameMap.put(new_dc.getName(), new_dc.getID()) != null )
            {
                throw new SystemErrorException(mName + 
                        "Unexpected return from this.nameMap.put().");
            }
        }
        
        /* null out the itsCells field of the old instance of DataColumn
         * just to ensure that they don't get changed by accident.
         */
        old_dc.setItsCells(null);
        
        
        /* Note changes between the old and new incarnations of the 
         * DataColumn, and notify the listeners.
         */
        new_dc.noteChange(old_dc, new_dc);
        new_dc.notifyListenersOfChange();
        
        if ( ! cascade )
        {
            /* copy the cascade related fields from the old to the new incarnation
             * so that the data column will be able to exit the cascade gracefully.
             */
            new_dc.cascadeInProgress = old_dc.cascadeInProgress;
            old_dc.cascadeInProgress = false;

            new_dc.pendingSet = old_dc.pendingSet;
            old_dc.pendingSet = null;
        }
        
        this.db.cascadeEnd();
         
        return;
         
    } /* ColumnList::replaceDataColumn(new_dc) */
    
     
    /**
     * replaceReferenceColumn(new_rc)
     *
     * Search the column list for an instance of ReferenceColumn with the same 
     * id as that of the supplied instance.  
     *
     * Replace the old version of the ReferenceColumn with the new, but copy the 
     * itsCells field into the new version, and update the numCells fields of 
     * the new version with the correct value.
     * 
     * Replace the old representation of the ReferenceColumn header with the new 
     * representation in the column list and in the index.
     *
     * N.B. This method must be used to replace a ReferenceColumn with a modified
     *      version of itself only.  If you play with IDs and try to use this to 
     *      replace one ReferenceColumn with another, it will choke.
     *
     *                                                 JRM -- 8/31/07
     *
     * Changes:
     *
     *   - None.
     */

    protected void replaceReferenceColumn(ReferenceColumn new_rc)
       throws SystemErrorException
    {
        final String mName = "ColumnList::replaceReferenceColumn(dbe): ";
        boolean matchFound = false;
        Column col;
        ReferenceColumn old_rc = null;
          
        if ( ( new_rc == null ) || ( ! ( new_rc instanceof ReferenceColumn ) ) )
        {
            throw new SystemErrorException(mName + "Bad new_rc param");
        }
        else if ( new_rc.getDB() != this.db )
        {
            throw new SystemErrorException(mName + "new_rd.getDB() != this.db");
        }
        else if ( new_rc.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "new_rc.getID() == INVALID_ID");
        }
        else if ( new_rc.getItsCells() != null )
        {
            throw new SystemErrorException(mName + 
                                           "new_rc.getItsCells() != null");
        }
         
        col = this.cl.get(new_rc.getID());
         
        if ( col == null )
        {
            throw new SystemErrorException(mName +
                    "can't replace -- not in column list.");
        }
        else if ( ! ( col instanceof ReferenceColumn) )
        {
            throw new SystemErrorException(mName + "type mis-match.");
        }
        
        old_rc = (ReferenceColumn)col;
        
        /* Verify that the name of the old_rc is mapped to the ID of the 
         * old_rc in the name map.  If new_rd.name != old_rd.name, we will
         * verify that new_rc.name is valid and that it is not in use shortly.
         */
        if ( ( ! this.nameMap.containsKey(old_rc.getName()) ) ||
             ( this.nameMap.get(old_rc.name) != old_rc.getID() ) )
        {
            throw new SystemErrorException(mName + 
                    "old_rc.name not mapped to old_rc.id in name map.");
        }
        
        if ( new_rc.getName().compareTo(old_rc.getName()) != 0 )
        {
            // verify that the new name is valid and not in use. 
            
            if ( ( ! ( this.db.IsValidSVarName(new_rc.getName() ) ) ) ||
                 ( this.db.vl.inVocabList(new_rc.getName() ) ) ||
                 ( this.inColumnList(new_rc.getName() ) ) ) 
            {
                throw new SystemErrorException(mName + 
                        "new_rc.name invalid or in use");
            }
        }
        
        /* Copy the reference to the cells vector from the old version of 
         * the ReferenceColumn into the new.  SetItsCells() updates 
         * new_rc.numCells in passing.
         */
        new_rc.setItsCells(old_rc.getItsCells());
        
        
        /* finally, replace the old ReferenceColumn with the new in the 
         * column list.  Similarly, replace the ReferenceColumn in the index.  
         * If the name has changed, replace the old name with the new in the 
         * name index.
         */

        if ( this.cl.put(new_rc.getID(), new_rc) != old_rc )
        {
            throw new SystemErrorException(mName + "replace failed.");
        }
        
        this.db.idx.replaceElement(new_rc);
        
        if ( old_rc.getName().compareTo(new_rc.getName()) != 0 )
        {
            this.nameMap.remove(old_rc.getName());
            
            if ( this.nameMap.put(new_rc.getName(), new_rc.getID()) != null )
            {
                throw new SystemErrorException(mName + 
                        "Unexpected return from this.nameMap.put().");
            }
        }
         
        return;
         
    } /* ColumnList::replaceReferenceColumn(new_dc) */
    

    /**
     * toDBString()
     * 
     * Returns a String representation of the contents of the ColumnList.<br>
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
        boolean first = true;
        String s;
        Column col;
        java.util.Enumeration<Column> entries;
        
        try
        {
            s = "((ColumnList) (cl_size: ";
            s += this.cl.size();
            s += ") (cl_contents: (";
            entries = this.cl.elements();
            while ( entries.hasMoreElements() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    s += ", ";
                }
                col = entries.nextElement();
                s += col.toDBString();
            }
            s += ")))";
        }
        
        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
       
        return s;
        
    } /* ColumnList::toDBString() */
    
} /* class ColumnList */
    
