/*
 * DataColumnListeners.java
 *
 * Created on February 11, 2008, 8:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

/**
 * Class DataColumnListeners
 *
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for changes in DataColumns,
 * and notifying the registered listeners when changes occur.
 *
 *                                               -- 2/7/08
 */
public class DataColumnListeners extends Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * itsCol: Reference to the instance of DataColumn for which this
     *      instance of DataColumnListeners is managing listeners.
     *
     * itsColID: Long containing the ID assigned to the DataColumn in which
     *      itsCell resides.
     *
     * changeNoted:  Boolean flag used to note if any reportable changes were
     *      detected between the old and new versions of the target DataColumn
     *      were detected the last time the two versions were examined.
     *
     *      Thus, given the current set of reportable changes, changesNoted
     *      will be true iff (nameChanged || varLenChanged || argListChanged).
     *
     *      Note that this field, like all the other fields used to note changes
     *      between subsequent versions of the target VocabElement, is reset
     *      after the listeners are notified.
     *
     * nameChanged:  Boolean flag used to record the fact that the name of the
     *      data column has changed.
     *
     * oldName:  Reference to a String containing the old name of the DataColumn
     *      if the name has changed, or null otherwise.
     *
     * newName: Reference to a String containing the new name of the DataColumn
     *      if the name has changed, or null otherwise.
     *
     * hiddenChanged:  Boolean flag used to record the fact that the hidden
     *      field of the associated DataColumn has changed value.
     *
     * oldHidden: Old value of the target DataColumn's hidden field if it has
     *      changed, or undefined otherwise.
     *
     * newHidden: New value of the target DataColumn's hidden field if it has
     *      changed, or undefined otherwise.
     *
     * readOnlyChanged:  Boolean flag used to record the fact that the readOnly
     *      field of the associated DataColumn has changed value.
     *
     * oldReadOnly: Old value of the target DataColumn's readOnly field if it
     *      has changed, or undefined otherwise.
     *
     * newReadOnly: New value of the target DataColumn's readOnly field if it
     *      has changed, or undefined otherwise.
     *
     * varLenChanged:  Boolean flag used to record the fact that the varLen
     *      field of the associated DataColumn has changed value.
     *
     * oldVarLen: Old value of the target DataColumn's varLen field if it
     *      has changed, or undefined otherwise.
     *
     * newVarLen: New value of the target DataColumn's varLen field if it
     *      has changed, or undefined otherwise.
     *
     * selectedChanged: Boolean flag used to record the fact that the selected
     *      field of the associated DataColumn has changed value.
     *
     * oldSelected: if the selected field has changed, this field contains
     *      its old value.  Undefined otherwise.
     *
     * newSelected: if the selected field has changed, this field contains
     *      its new value.  Undefined otherwise.
     */

    DataColumn itsCol = null;
    long itsColID = DBIndex.INVALID_ID;

    boolean changeNoted = false;

    boolean nameChanged = false;
    String oldName = null;
    String newName = null;

    boolean hiddenChanged = false;
    boolean oldHidden = false;
    boolean newHidden = false;

    boolean readOnlyChanged = false;
    boolean oldReadOnly = false;
    boolean newReadOnly = false;

    boolean varLenChanged = false;
    boolean oldVarLen = false;
    boolean newVarLen = false;

    boolean selectedChanged = false;
    boolean oldSelected = false;
    boolean newSelected = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DataColumnListeners
     *
     * For now at least, only one constructor:
     *
     * The initial constructor takes a db, and a reference to a DataColumn,
     * and sets up the new instance to start managing change listeners for the
     * instance of DataColumn.
     *
     * No copy constructor, as the plan is to use the same instance of
     * DataColumnListeners to manage listeners for all incarnations of a given
     * DataColumn.
     *
     *                                               -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    public DataColumnListeners(Database db,
                               DataColumn dc)
        throws SystemErrorException
    {
        super(db);

        final String mName = "DataColumnListeners::DataColumnListeners(db, dc)";

        if ( dc == null )
        {
            throw new SystemErrorException(mName + ": dc null on entry.");
        }

        if ( dc.getDB() != db )
        {
             throw new SystemErrorException(mName + ": dc.getDB() != db.");
        }

        if ( dc.getID() == DBIndex.INVALID_ID )
        {
             throw new SystemErrorException(mName + ": dc.getID() is invalid.");
        }

        this.itsCol = dc;
        this.itsColID = dc.getID();

    } /* DataColumnListeners::DataColumnListeners(db, dc) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * updateItsCol()
     *
     * Update the itsCell field for a new incarnation of the target DataCell.
     *
     *                                               -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateItsCol(DataColumn dc)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::updateItsCol()";


        if ( dc == null )
        {
            throw new SystemErrorException(mName + ": dc null on entry.");
        }

        if ( dc.getDB() != this.db )
        {
             throw new SystemErrorException(mName + ": dc.getDB() != db.");
        }

        if ( dc.getID() != this.itsColID )
        {
             throw new SystemErrorException(mName +
                     ": dc.getID() != itsCellID.");
        }

        this.itsCol = dc;

        return;

    } /* DataColumnListeners::updateItsCol() */


    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/

    /**
     * noteChange()
     *
     * Given references to the old and new versions of the target DataColumn,
     * make note of any changes that should be reported to the listeners.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected boolean noteChange(DataColumn oldCol,
                                 DataColumn newCol)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::noteChanges()";

        if ( ( oldCol == null ) || ( newCol == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldCol or newCol null on entry.");
        }

        if ( ( oldCol.getID() != this.itsColID ) ||
             ( newCol.getID() != this.itsColID ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldCol or newCol has bad ID.");
        }

        if ( ( this.changeNoted ) ||
             ( this.nameChanged ) ||
             ( this.oldName != null ) ||
             ( this.newName != null ) ||
             ( this.hiddenChanged ) ||
             ( this.readOnlyChanged ) ||
             ( this.selectedChanged ) )
        {
            throw new SystemErrorException(mName + ": change already noted?!?");
        }


        /*** test to see if the name has changed ***/

        this.oldName = oldCol.getName();
        this.newName = newCol.getName();

        if ( ( this.oldName == null ) || ( this.newName == null ) )
        {
            if ( this.oldName != this.newName )
            {
                this.changeNoted = true;
                this.nameChanged = true;
            }
        }
        else if ( this.oldName.compareTo(this.newName) != 0 )
        {
            this.changeNoted = true;
            this.nameChanged = true;
        }
        else
        {
            this.oldName = null;
            this.newName = null;
        }


        /*** test to see if hidden has changed ***/

        this.oldHidden = oldCol.getHidden();
        this.newHidden = newCol.getHidden();

        if ( this.oldHidden != this.newHidden )
        {
            this.changeNoted = true;
            this.hiddenChanged = true;
        }


        /*** test to see if readOnly has changed ***/

        this.oldReadOnly = oldCol.getReadOnly();
        this.newReadOnly = newCol.getReadOnly();

        if ( this.oldReadOnly != this.newReadOnly )
        {
            this.changeNoted = true;
            this.readOnlyChanged = true;
        }


        /*** test to see if the column selection status has changed ***/

        this.oldSelected = oldCol.getSelected();
        this.newSelected = newCol.getSelected();

        if ( this.oldSelected != this.newSelected )
        {
            this.changeNoted = true;
            this.selectedChanged = true;
        }

        return this.changeNoted;

    } /* DataColumnListeners::noteChange() */


    /**
     * notifyListenersOfCellDeletion()
     *
     * Advise the listeners of the deletion of a DataCell of the specified
     * id from the DataColumn.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfCellDeletion(long cellID)
        throws SystemErrorException
    {
        final String mName =
                "DataColumnListeners::notifyListenersOfCellDeletion()";
        ExternalDataColumnListener el;


        // The database has been modified!
        db.markAsChanged();

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalDataColumnListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalDataColumnListener.");
            }

            el = (ExternalDataColumnListener)o;

            el.DColCellDeletion(this.db, this.itsColID, cellID);
        }

        return;

    } /* DataColumnListeners::notifyListenersOfCellDeletion() */


    /**
     * notifyListenersOfCellInsertion()
     *
     * Advise the listeners of the deletion of a DataCell of the specified
     * id from the DataColumn.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfCellInsertion(long cellID)
        throws SystemErrorException
    {
        final String mName =
                "DataColumnListeners::notifyListenersOfCellInsertion()";
        ExternalDataColumnListener el;


        // The database has been modified!
        db.markAsChanged();

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalDataColumnListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalDataColumnListener.");
            }

            el = (ExternalDataColumnListener)o;

            el.DColCellInsertion(this.db, this.itsColID, cellID);
        }

        return;

    } /* DataColumnListeners::notifyListenersOfCellInsertion() */


    /**
     * notifyListenersOfChange()
     *
     * If any notable changes have been noted since the last call to
     * notifyListenersOfChanges(), call the listeners to advise them of the
     * changes, and then clear the fields used to note changes.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfChange()
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::NotifyListenersOfChange()";
        ExternalDataColumnListener el;

        if ( this.changeNoted )
        {

            boolean nonTrivial = (
                    this.nameChanged ||
                    this.hiddenChanged ||
                    this.readOnlyChanged ||
                    this.varLenChanged);

            if(nonTrivial) {
                // The database has been modified!
                db.markAsChanged();
            } nonTrivial = false;

            // first, notify the internal listeners... none at present, so
            // nothing to do here.


            // then notify the external listeners...
            for ( Object o : this.els )
            {
                el = (ExternalDataColumnListener)o;
                el.DColConfigChanged(this.db,
                                     this.itsColID,
                                     this.nameChanged,
                                     this.oldName,
                                     this.newName,
                                     this.hiddenChanged,
                                     this.oldHidden,
                                     this.newHidden,
                                     this.readOnlyChanged,
                                     this.oldReadOnly,
                                     this.newReadOnly,
                                     this.varLenChanged,
                                     this.oldVarLen,
                                     this.newVarLen,
                                     this.selectedChanged,
                                     this.oldSelected,
                                     this.newSelected);
            }

            // and finally, discard the old change notes.
            this.changeNoted = false;
            this.nameChanged = false;
            this.oldName = null;
            this.newName = null;
            this.hiddenChanged = false;
            this.oldHidden = false;
            this.newHidden = false;
            this.readOnlyChanged = false;
            this.oldReadOnly = false;
            this.newReadOnly = false;
            this.varLenChanged = false;
            this.oldVarLen = false;
            this.newVarLen = false;
            this.selectedChanged = false;
            this.oldSelected = false;
            this.newSelected = false;
        }

        return;

    } /* DataColumnListeners::notifyListenersOfChange() */


    /**
     * notifyListenersOfDeletion()
     *
     * Advise any listeners of the deletion of the associated DataColumn.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::notifyListenersOfDeletion()";
        ExternalDataColumnListener el;


        // The database has been modified!
        db.markAsChanged();

        // first, notify the intenal listeners... none at present

        // then notify the external listeners...
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalDataColumnListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalDataColumnListener.");
            }

            el = (ExternalDataColumnListener)o;

            el.DColDeleted(this.db, this.itsColID);
        }

        return;

    } /* DataColumnListeners::notifyListenersOfDeletion() */


    /*************************************************************************/
    /*********************** Listener Management: ****************************/
    /*************************************************************************/

    /**
     * deregisterExternalListener()
     *
     * Deregister an external listener.
     *
     *                                               -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterExternalListener(ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::deregisterExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.DeleteExternalListener(el);

        return;

    } /* DataColumnListeners::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * Deregister an external listener.
     *
     *                                               -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::deregisterInternalListener()";

        throw new SystemErrorException(mName +
                                       ": No internal listeners at present");

        // return; /* commented out to keep the compiler happy */

    } /* DataCellListeners::deregisterInternalListener() */


    /**
     * registerExternalListener()
     *
     * Register an external listener.
     *
     *                                               -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerExternalListener(ExternalDataColumnListener el)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::registerExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.AddExternalListener(el);

        return;

    } /* DataColumnListeners::registerExternalListener() */


    /**
     * registerInternalListener()
     *
     * Register an internal listener.
     *
     *                                               -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "DataColumnListeners::registerInternalListener()";

        throw new SystemErrorException(mName +
                                       ": No internal listeners at present");

        // return; /* commented out to keep the compiler happy */

    } /* DataColumnListeners::registerExternalListener() */

} /* Class DataColumnListeners */
