/*
 * ColumnList.java
 *
 * Created on August 31, 2007, 9:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

import java.util.ArrayList;
import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.openshapa.util.OpenHashtable;

/**
 * Class ColumnList
 *
 * A singleton instance of ColumnList is used to maintain the list of
 * columns (AKA spreadsheet variables) for an OpenSHAPA database.
 *
 * Note that while the ColumnList is quite similar to the DBIndex class, it
 * isn't close enough to be a subclass.
 *
 *                                           -- 8/31/07
 */
public class ColumnList
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /** Reference to the Database of which this instance is part */
    protected Database db = null;

    /** Index of all instances of Column in the column list */
    protected OpenHashtable<Long, Column> cl = new OpenHashtable<Long, Column>();

    /** Hashmap mapping column names to column IDs.  This mapping is used both
     * to allow lookups by column name, and to determine if a column name is
     * in use.
     */
    protected HashMap<String, Long> nameMap = new HashMap<String, Long>();

    /**
     * Vector of long used to allow the user to maintain an ordering on the
     * columns in the column list.
     *
     * The ID of each column in the column list must appear in the cov vector
     * exactly once.
     */
    protected Vector<Long> cov = null;

    /**
     * instance of ColumnListListeners used to maintain lists of listeners,
     *  and notify them as appropriate.
     */
    protected ColumnListListeners listeners = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    // ColumnList()
    /**
     * Constructor for the ColumnList class.
     *                                              -- 4/30/07
     *
     * Changes:
     *
     *    - Added initialization code for the new cov field.
     */
    protected ColumnList(Database db)
         throws SystemErrorException
    {
        super();

        final String mName = "ColumnList::ColumnList(db): ";

        if (db == null) {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;

        this.cov = new Vector<Long>();

        this.listeners = new ColumnListListeners(db, this);

        return;

    } /* ColumnList::ColumnList(db) */


    // hashCode()
    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        hash += HashUtils.Obj2H(db) * Constants.SEED1;
        hash += HashUtils.Obj2H(cl) * Constants.SEED2;
        hash += HashUtils.Obj2H(nameMap) * Constants.SEED3;
        hash += HashUtils.Obj2H(listeners) * Constants.SEED4;

        return hash;
    }


    // equals()
    /**
     * Compares this ColumnList against another.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this, false
     * otherwise.
     */
    @Override
    public boolean equals(final Object obj) 
    {
        boolean result;

        if (this == obj)
        {
            result = true;
        }
        else if ( ( obj == null ) || ( obj.getClass() != this.getClass() ) )
        {
            result = false;
        }
        else
        {
            // Must be this class to be here
            ColumnList l = (ColumnList) obj;
            result = super.equals(l)
                && (db == null ? l.db == null : db.equals(l.db))
                && (cl == null ? l.cl == null : cl.equals(l.cl))
                && (nameMap == null ? l.nameMap == null
                                    : nameMap.equals(l.nameMap))
                && (cov == null ? l.cov == null : cov.equals(l.cov))
                && (listeners == null ? l.listeners == null
                                      : listeners.equals(l.listeners));
        }

        return result;
    }


    // toString() -- override
    /**
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
    /**************************** Accessors: *********************************/
    /*************************************************************************/

    // getColOrderVector()
    /**
     * Validate the column order vector (this.cov) maintained by the class
     * and return a copy.
     *
     * @return copy of this.cov
     *
     * @throws org.openshapa.db.SystemErrorException if any errors are detected.
     */
    protected Vector<Long> getColOrderVector()
        throws SystemErrorException
    {
        final String mName = "ColumnList::getColOrderVector(): ";
        Vector<Long> cov_copy = null;

        if ( this.cov == null )
        {
            throw new SystemErrorException(mName + "this.cov null on entry.");
        }

        if ( ! this.validateColOrderVector(this.cov) )
        {
            throw new SystemErrorException(mName +
                    "invalid this.cov on entry,");
        }

        cov_copy = new Vector<Long>(this.cov);

        return cov_copy;

    } /* ColumnList::getColOrderVector() */


    // setColOrderVector()
    /**
     * Verify that the supplied new column order vector is valid.  Throw a
     * system error if it is not.
     *
     * Otherwise, replace the old column order vector with the new, and
     * notify the listeners (all external).
     *
     * @param new_cov new column order vector
     *
     * @throws org.openshapa.db.SystemErrorException if any error is detected.
     */
    protected void setColOrderVector(Vector<Long> new_cov)
        throws SystemErrorException
    {
        final String mName = "ColumnList::setColOrderVector(): ";
        Vector<Long> old_cov = null;

        if ( new_cov == null )
        {
            throw new SystemErrorException(mName + "new_cov null on entry.");
        }

        if ( ! this.validateColOrderVector(new_cov) )
        {
            throw new SystemErrorException(mName +
                    "new_cov invalid on entry,");
        }

        old_cov = new Vector<Long>(this.cov);

        // the column order vector is not used by the database, and thus, at
        // least as of this writing, there is not need to surround it with a
        // cascasde start and end.  However, it doesn't hurt, and it may be
        // needed some day.

        this.db.cascadeStart();

        this.cov = new Vector<Long>(new_cov);

        // inform listeners of the edit.

        this.listeners.notifyListenersOfColOrderVectorEdit(old_cov, new_cov);

        this.db.cascadeEnd();

        return;

    } /* ColumnList::setColOrderVector() */


    // validateColOrderVector()
    /**
     * Validate the supplied test column order vector, and return true if
     * it is valid, and false if it is not.
     *
     * To validate the column order vector we must:
     *
     * 1) verify that the cov contains the same number of elements
     *    column list and the nameMap.
     *
     * 2) verify that the cov contains no duplicate elements.
     *
     * 3) verify that all the elements in the cov appear in the column list.
     *
     * At present, this method is private, as I don't see any need for it out
     * side this method, as any external copies of the column order vector will
     * be maintained by the user interface code, and not be directly accessible
     * to the user.
     *
     * However, if the need presents itself, this method may be made protected,
     * and then be made accessible via a new public call in Database.java.
     *
     *                                              -- 7/31/09
     *
     * @param test_cov reference to the column order vector to be tested.
     *
     * @return true if valid, false otherwise
     *
     * @throws org.openshapa.db.SystemErrorException if any errors are detected.
     */

    private boolean validateColOrderVector(Vector<Long> test_cov)
        throws SystemErrorException
    {
        final String mName = "ColumnList::validateColOrderVector(): ";
        boolean valid = true;
        int i;
        int cl_len;
        int nameMap_len;
        int test_cov_len = 0;
        long col_id;
        Vector<Long> tmp = null;

        // do initial sanity checks

        if ( this.cl == null )
        {
            throw new SystemErrorException(mName + "this.cl null on entry.");
        }

        if ( this.nameMap == null )
        {
            throw new SystemErrorException(mName + "this.cl null on entry.");
        }

        if ( test_cov == null )
        {
            throw new SystemErrorException(mName + "test_cov null on entry.");
        }


        // Now validate the test column order vector.  To do this:
        //
        // 1) verify that the test cov contains the same number of elements
        //    column list and the nameMap.

        if ( valid )
        {
            cl_len = this.cl.size();
            nameMap_len = this.nameMap.size();
            test_cov_len = test_cov.size();

            if ( ( test_cov_len != cl_len ) ||
                 ( test_cov_len != nameMap_len ) )
            {
                valid = false;
            }
        }


        // 2) verify that the cov contains no duplicate elements.

        if ( valid )
        {
            tmp = new Vector<Long>(test_cov);

            java.util.Collections.sort(tmp);

            for ( i = 0; i < test_cov_len - 1; i++ )
            {
                if ( tmp.get(i) == tmp.get(i + 1) )
                {
                    // cov has duplicate entries
                    valid = false;
                }
            }

            tmp = null;
        }


        // 3) verify that all the elements in the cov appear in the column list.

        if ( valid )
        {
            for ( i = 0; i < test_cov_len; i++ )
            {
                col_id = test_cov.get(i);

                if ( ! this.inColumnList(col_id) )
                {
                    valid = false;
                }
            }
        }


        return valid;

    } /* ColumnList::validateColOrderVector() */


    /*************************************************************************/
    /********************* Listener Manipulation: ****************************/
    /*************************************************************************/

    // deregisterExternalListener()
    /**
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister external change listeners message on to
     * the instance of ColumnListListeners pointed to by this.listeners.
     *
     *                                           -- 2/11/08
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


    // deregisterInternalListener()
    /**
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister internal change listeners message on to
     * the instance of ColumnListListeners pointed to by this.listeners.
     *
     *                                           -- 2/11/08
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


    // registerExternalListener()
    /**
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register external change listeners message on to the
     * instance of ColumnListListeners pointed to by this.listeners.
     *
     *                                           -- 2/11/08
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


    // registerInternalListener()
    /**
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register internal change listeners message on to the
     * instance of ColumnListListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
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

    // addColumn()
    /**
     * If the column is a DataColumn, verify that its associated matrix
     * vocab element exists and is in the vocab list.
     *
     * Insert the Column into the index, and into the column list.  Call the
     * columns constructItsCells() method.
     *
     *                                                  -- 8/30/07
     *
     * Changes:
     *
     *    - Added code to create and assign an instance of DataColumnListeners
     *      to a newly inserted DataColumn.  Also added code to notify column
     *      list listeners of the column addition.  Finally, added calls to
     *      mark the beginning and end of any resulting cascade of changes.
     *
     *                                                   -- 2/11/08
     *
     *    - Added code to register the column as an internal listener of its
     *      matrix vocab element.
     *                                                   -- 3/22/08
     *
     *    - Added code to add the ID of the new column to the end of the
     *      cov Vector.
     *                                                 7/31/09
     */

    protected void addColumn(Column col)
       throws SystemErrorException
    {
        final String mName = "ColumnList::addColumn(col): ";
        long mveID;
        DataColumn dc = null;
        DBElement dbe;
        Vector<Long> old_cov = null;
        Vector<Long> new_cov = null;
        MatrixVocabElement mve = null;

        if (col == null) {
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
        else if ( this.cl.containsReference(col) )
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

        old_cov = new Vector<Long>(this.cov);
        this.cov.add(col.getID());
        new_cov = new Vector<Long>(this.cov);

        col.constructItsCells();

        if ( col instanceof DataColumn )
        {
            DataColumnListeners nl = null;

            nl = new DataColumnListeners(db, (DataColumn)col);
            ((DataColumn)col).setListeners(nl);
        }

        col.register();

        this.listeners.notifyListenersOfColInsertion(col.getID(),
                                                     old_cov,
                                                     new_cov);

        this.db.cascadeEnd();

        this.db.registerInternalCascadeListener(col.getID());

        return;

    } /* ColumnList::addColumn(col) */


    // applyTemporalOrdering()
    /**
     * Tell each column in the column list to sort its cells in onset order.
     *
     *                                               -- 3/20/08
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
     *                                                  -- 8/31/07
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
     *                                                   -- 8/30/07
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


    // getColumns()
    /**
     * Construct and return a vector containing copies of all Columns
     * in the column list.  If the column list is empty, return null.
     *
     *                                                   -- 8/31/07
     *
     * Changes:
     *
     *    - Modified method to return an empty vector instead of null if
     *      the column list is empty.
     *
     *    - Modified to use clone interface rather than instanceof CF-2009/01/12
     */

    protected java.util.Vector<Column> getColumns()
        throws SystemErrorException
    {
        Vector<Column> cols = new Vector<Column>();
        Enumeration<Column> entries = this.cl.elements();

        while (entries.hasMoreElements())
        {
            Column col = entries.nextElement();
            
            try
            {
                cols.add((Column) col.clone());
            } 
            
            catch (CloneNotSupportedException e)
            {
                throw new SystemErrorException("Unable to clone column.");
            }
        }

        return cols;

    } /* ColumnList::getColumns() */


    // getDataColumns()

    /**
     * Construct and return a vector containing copies of all DataColumns
     * in the column list.  If the column list is empty, return null.
     * Note that the ItsCells field will be null for each column!
     *
     *                                                   -- 8/31/07
     *
     * Changes:
     *
     *    - Modified the method to return an empty vector if there are
     *      no data columns in the column list.
     *                                                   -- 11/24/08
     */

    protected java.util.Vector<DataColumn> getDataColumns()
        throws SystemErrorException
    {
        Vector<DataColumn> cols = new Vector<DataColumn>();
        Enumeration<Column> entries = this.cl.elements();
        Column col;
        DataColumn dc;        

        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();

            if ( col instanceof DataColumn )
            {
                dc = (DataColumn)col;

                cols.add(new DataColumn(dc));
            }
        }

        return cols;

    }
    /* ColumnList::getDataColumns() */


    // getReferenceColumns()
    /**
     * Construct and return a vector containing copies of all DataColumns
     * in the column list.  If the column list is empty, return null.
     *
     *                                                   -- 8/31/07
     *
     * Changes:
     *
     *    - Modified the method to return an empty vector if there are
     *      no reference columns in the column list.
     *                                                   -- 11/24/08
     */

    protected java.util.Vector<ReferenceColumn> getReferenceColumns()
        throws SystemErrorException
    {
        java.util.Vector<ReferenceColumn> cols = null;
        Column col;
        ReferenceColumn rc;
        java.util.Enumeration<Column> entries;

        entries = this.cl.elements();

        cols = new java.util.Vector<ReferenceColumn>();

        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();

            if ( col instanceof ReferenceColumn )
            {
                rc = (ReferenceColumn)col;

                cols.add(new ReferenceColumn(rc));
            }
        }

        return cols;

    } /* ColumnList::getReferenceColumns() */


    // inColumnList(targetID)
    /**
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


    // inColumnList(targetName)
    /**
     * Return true if the supplied column name currently appears
     * in the column list, and false otherwise.
     *
     *                                               -- 6/3/07
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


    // dataColumnInColumnList(targetID)
    /**
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


    // dataColumnInColumnList(targetName)
    /**
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


    // referenceColumnInColumnList(targetID)
    /**
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


    // referenceColumnInColumnList(targetName)
    /**
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


    // removeColumn()
    /**
     * Verify that the Column indicated by the targetID is in the column list
     * and that it is empty (that is, it has no cells).  Throw a system error
     * if it is not.
     *
     * Then remove the target Column from the column list and from the index.
     *
     *                                                  -- 8/31/07
     *
     * Changes:
     *
     *    - Added code to remove the listeners just before a data column is
     *      removed and to notify its listeners of the impending deletion.
     *      Added code to notify column list listeners of the column removal.
     *      Finally added calls to mark the beginning and end of any resulting
     *      cascade of changes.
     *                                                   -- 2/11/08
     *
     *    - Added code to remove the ID of the column from the cov -- the
     *      column order vector.
     *                                                   -- 7/31/09
     */

    protected void removeColumn(long targetID)
       throws SystemErrorException
    {
        final String mName = "ColumnList::removeColumn(targetID): ";
        int i;
        long id;
        FormalArgument fArg;
        Vector<Long> old_cov = null;
        Vector<Long> new_cov = null;
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
        else if ( ! this.cov.contains(targetID) )
        {
            throw new SystemErrorException(mName +
                    "targetID not in column order list.");
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

        old_cov = new Vector<Long>(this.cov);
        new_cov = new Vector<Long>(this.cov);
        if ( ! new_cov.remove(col.getID()) )
        {
            throw new SystemErrorException(mName +
                    "can't remove target ID from new_cov");
        }

        this.listeners.notifyListenersOfColDeletion(col.getID(),
                                                    old_cov,
                                                    new_cov);

        col.deregister();


        /* go ahead and remove */

        if ( (col = this.cl.remove(targetID)) == null )
        {
            throw new SystemErrorException(mName + "cl.remove() failed.");
        }

        if ( ! this.cov.remove(targetID) )
        {
            throw new SystemErrorException(mName + "cov.remove() failed.");
        }

        this.db.idx.removeElement(targetID);

        this.nameMap.remove(col.getName());

        this.db.deregisterInternalCascadeListener(col.getID());

        this.db.cascadeEnd();

        return;

    } /* ColumnList::removeColumn(targetID) */


    // replaceDataColumn(new_dc)
    /**
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
     *                                                  -- 8/31/07
     *
     * Changes:
     *
     *    - Added code to transfer the listeners from the old incarnation of
     *      the data column to the new, and to notify the listeners of the
     *      changes.  Added calls to mark the beginning and end of any resulting
     *      cascade of changes.
     *                                                   -- 2/5/08
     */

    protected void replaceDataColumn(DataColumn new_dc, boolean cascade)
       throws SystemErrorException
    {
        final String mName = "ColumnList::replaceDataColumn(dbe): ";
        Column col;
        DataColumn old_dc = null;

        if (new_dc == null) {
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


    // replaceReferenceColumn(new_rc)
    /**
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
     *                                                  -- 8/31/07
     *
     * Changes:
     *
     *   - None.
     */

    protected void replaceReferenceColumn(ReferenceColumn new_rc)
       throws SystemErrorException
    {
        final String mName = "ColumnList::replaceReferenceColumn(dbe): ";
        Column col;
        ReferenceColumn old_rc = null;

        if (new_rc == null) {
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


    // toDBString()
    /**
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


    // toMODBFile_colDecs()
    /**
     * Write declarations of all non system data columns to the supplied file
     * in MacSHAPA ODB file format.  The output of this method is the
     * <s_var_dec_list> in the grammar defining the MacSHAPA ODB file
     * format.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     * Note that this method throws away a lot of information about each
     * data column, as this data is not used in MacSHAPA.
     *
     *                                              12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_colDecs(java.io.PrintStream output,
                                      String newLine,
                                      String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "VocabList::toMODBFile_colDecs()";
        String colIndent;
        Column col;
        DataColumn dc;
        java.util.Enumeration<Column> entries;

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

        output.printf("%s(%s", indent, newLine);

        colIndent = indent + "  ";

        entries = this.cl.elements();

        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();

            if ( col instanceof DataColumn )
            {
                dc = (DataColumn)col;

                if ( this.db.toMODBFile_includeDataColumnInUserSection(dc) )
                {
                    dc.toMODBFile_colDec(output, newLine, colIndent);
                }
            }
        }


        output.printf("%s)%s", indent, newLine);

        return;

    } /* ColumnList::toMODBFile_colDecs() */


    // toMODBFile_colDefs()
    /**
     * Write definitions of all user data columns to the supplied file
     * in MacSHAPA ODB file format.  The output of this method is the
     * <s_var_def_list> in the grammar defining the MacSHAPA ODB file
     * format.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     * Note that this method throws away a lot of information about each
     * data column, as this data is not used in MacSHAPA.
     *
     *                                              12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_colDefs(java.io.PrintStream output,
                                      String newLine,
                                      String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "VocabList::toMODBFile_colDefs()";
        String colIndent;
        Column col;
        DataColumn dc;
        java.util.Enumeration<Column> entries;

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

        output.printf("%s(%s", indent, newLine);

        colIndent = indent + "  ";

        entries = this.cl.elements();

        while ( entries.hasMoreElements() )
        {
            col = entries.nextElement();

            if ( col instanceof DataColumn )
            {
                dc = (DataColumn)col;

                if ( this.db.toMODBFile_includeDataColumnInUserSection(dc) )
                {
                    dc.toMODBFile_colDef(output, newLine, colIndent);
                }
            }
        }

        output.printf("%s)%s", indent, newLine);

        return;

    } /* ColumnList::toMODBFile_colDefs() */

} /* class ColumnList */

