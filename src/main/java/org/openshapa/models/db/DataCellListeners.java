/*
 * DataCellListeners.java
 *
 * Created on February 7, 2008, 9:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

/**
 * Class DataCellListeners
 *
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for changes in DataCells,
 * and notifying the registered listeners when changes occur.
 *
 *                                               -- 2/7/08
 */
public class DataCellListeners extends Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * itsCell: Reference to the instance of DatCell for which this
     *      instance of DataCellListeners is managing change listeners.
     *
     * itsCellID: Long containing the ID assigned to itsCell.
     *
     * itsColID: Long containing the ID assigned to the DataColumn in which
     *      itsCell resides.
     *
     * changeNoted:  Boolean flag used to note if any reportable changes were
     *      detected between the old and new versions of the target DataCell
     *      were detected the last time the two versions were examined.
     *
     *      Thus, given the current set of reportable changes, changeNoted
     *      will be true iff (ordChanged || onsetChanged || offsetChanged ||
     *      valChanged).
     *
     *      Note that this field, like all the other fields used to note changes
     *      between subsequent versions of the target DataCell, is reset
     *      after the listeners are notified.
     *
     * ordChanged:  Boolean flag used to record the fact that the ord of the
     *      associated DataCell has changed.
     *
     * oldOrd: Int containing the old ord it the ord of the DataCell has
     *      changed.
     *
     * newOrd: Int containing the new ord it the ord of the DataCell has
     *      changed.
     *
     * onsetChanged:  Boolean flag used to record the fact that the onset
     *      of the associated DataCell has changed value.
     *
     * oldOnset: If the onset has changed, reference to an instance of TimeStamp
     *      containing a copy of the old onset.  null otherwise.
     *
     * newOnset: If the onset has changed, reference to an instance of TimeStamp
     *      containing a copy of the new onset.  null otherwise.
     *
     * offsetChanged:  Boolean flag used to record the fact that the offset
     *      of the associated DataCell has changed value.
     *
     * oldOffset: If the offset has changed, reference to an instance of
     *      TimeStamp containing a copy of the old offset.  null otherwise.
     *
     * newOffset: If the offset has changed, reference to an instance of
     *      TimeStamp containing a copy of the new offset.  null otherwise.
     *
     * valChanged:  Boolean flag used to record the fact that the val
     *      of the associated DataCell has changed value.
     *
     * oldValt: If the val has changed, reference to an copy of the instance
     *      of Matrix containing the old value.  null otherwise.
     *
     * newVal: If the val has changed, reference to an copy of the instance
     *      of Matrix containing the new value.  null otherwise.
     *
     * selectedChanged: Boolean flag used to record the fact that the selected
     *      field of the associated DataCell has changed value.
     *
     * oldSelected: if the selected field has changed, this field contains
     *      its old value.  Undefined otherwise.
     *
     * newSelected: if the selected field has changed, this field contains
     *      its new value.  Undefined otherwise.
     */

    DataCell itsCell = null;
    long itsCellID = DBIndex.INVALID_ID;
    long itsColID = DBIndex.INVALID_ID;

    boolean changeNoted = false;

    boolean ordChanged = false;
    int oldOrd = -1;
    int newOrd = -1;

    boolean onsetChanged = false;
    TimeStamp oldOnset = null;
    TimeStamp newOnset = null;

    boolean offsetChanged = false;
    TimeStamp oldOffset = null;
    TimeStamp newOffset = null;

    boolean valChanged = false;
    Matrix oldVal = null;
    Matrix newVal = null;

    boolean selectedChanged = false;
    boolean oldSelected;
    boolean newSelected;

    boolean commentChanged = false;
    String oldComment = null;
    String newComment = null;



    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DataCellListeners
     *
     * For now at least, only one constructor:
     *
     * The initial constructor takes a db, and a reference to a DataCell,
     * and sets up the new instance to start managing change listeners for the
     * instance of DataCell.
     *
     * No copy constructor, as the plan is to use the same instance of
     * DataCellListeners to manage listeners for all incarnations of a given
     * DataCell.
     *
     *                                               -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    public DataCellListeners(Database db,
                             DataCell dc)
        throws SystemErrorException
    {
        super(db);

        final String mName = "DCellChangeListeners::DCellChangeListeners(db, dc)";

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

        if ( dc.getItsColID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           ": dc.getItsColID() is invalid");
        }

        this.itsCell = dc;
        this.itsCellID = dc.getID();
        this.itsColID = dc.getItsColID();

    } /* DataCellListeners::DataCellListeners(db, dc) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * updateItsCell()
     *
     * Update the itsCell field for a new incarnation of the target DataCell.
     *
     *                                               -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateItsCell(DataCell dc)
        throws SystemErrorException
    {
        final String mName = "DCellChangeListeners::updateItsCell()";


        if ( dc == null )
        {
            throw new SystemErrorException(mName + ": dc null on entry.");
        }

        if ( dc.getDB() != this.db )
        {
             throw new SystemErrorException(mName + ": dc.getDB() != db.");
        }

        if ( dc.getID() != this.itsCellID )
        {
             throw new SystemErrorException(mName +
                     ": ve.getID() != itsCellID.");
        }

        if ( dc.getItsColID() != this.itsColID )
        {
             throw new SystemErrorException(mName +
                     ": ve.getItsColID() != itsColID.");
        }

        this.itsCell = dc;

        return;

    } /* DataCellListeners::updateItsCell() */


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

    protected boolean noteChange(DataCell oldCell,
                                 DataCell newCell,
                                 boolean cascadeMveMod,
                                 boolean cascadePveDel,
                                 boolean cascadePveMod,
                                 long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "DCellChangeListeners::noteChanges()";
        int i;
        int j;
        int oldFargListLen;
        int newFargListLen;
        int indexOfOldIncarnation;
        int indexOfNewIncarnation;
        long id;
        FormalArgument oldFarg;
        FormalArgument oldIncarnation;
        FormalArgument newIncarnation;

        if ( ( oldCell == null ) || ( newCell == null ) )
        {
            throw new SystemErrorException(mName +
                    ": oldCell or newCell null on entry.");
        }

        if ( ( oldCell.getID() != this.itsCellID ) ||
             ( newCell.getID() != this.itsCellID ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldCell or newCell has bad ID.");
        }

        if ( ( this.changeNoted ) ||
             ( this.ordChanged ) ||
             ( this.onsetChanged ) ||
             ( this.oldOnset != null ) ||
             ( this.newOnset != null ) ||
             ( this.offsetChanged ) ||
             ( this.oldOffset != null ) ||
             ( this.newOffset != null ) ||
             ( this.valChanged ) ||
             ( this.oldVal != null ) ||
             ( this.newVal != null ) ||
             ( this.selectedChanged ) )
        {
            throw new SystemErrorException(mName + ": change already noted?!?");
        }


        /*** test to see if the ord has changed ***/

        if ( oldCell.getOrd() != newCell.getOrd() )
        {
            this.changeNoted = true;
            this.ordChanged = true;
            this.oldOrd = oldCell.getOrd();
            this.newOrd = newCell.getOrd();
        }


        /*** test to see if the onset has changed ***/

        if ( oldCell.getOnset().ne(newCell.getOnset()) )
        {
            this.changeNoted = true;
            this.onsetChanged = true;
            this.oldOnset = oldCell.getOnset();
            this.newOnset = newCell.getOnset();
        }


        /*** test to see if the offset has changed ***/

        if ( oldCell.getOffset().ne(newCell.getOffset()) )
        {
            this.changeNoted = true;
            this.offsetChanged = true;
            this.oldOffset = oldCell.getOffset();
            this.newOffset = newCell.getOffset();
        }


        /*** test to see if the cell value has changed ***/

        if ( ( cascadeMveMod ) || ( cascadePveDel ) || ( cascadePveMod ) )
        {
            this.oldVal = oldCell.getValBlind();
        }
        else
        {
            this.oldVal = oldCell.getVal();
        }
        this.newVal = newCell.getVal();

        if (!this.oldVal.equals(this.newVal))
        {
            this.changeNoted = true;
            this.valChanged = true;
        }
        else
        {
            this.oldVal = null;
            this.newVal = null;
        }


        /*** test to see if the cell selection status has changed ***/

        this.oldSelected = oldCell.getSelected();
        this.newSelected = newCell.getSelected();

        if ( this.oldSelected != this.newSelected )
        {
            this.changeNoted = true;
            this.selectedChanged = true;
        }


        /*** test to see if the cell comment has changes ***/

        this.oldComment = oldCell.getComment();
        this.newComment = newCell.getComment();

        if ( ( (Object)this.oldComment != this.newComment ) &&
             ( ( this.oldComment == null ) ||
               ( this.newComment == null ) ||
               ( this.oldComment.compareTo(this.newComment) != 0 ) ) )
        {
            this.changeNoted = true;
            this.commentChanged = true;
        }

        return this.changeNoted;

    } /* DataCellListeners::noteChange() */


    /**
     * noteOrdChangeOnly()
     *
     * Called when we know that at most a change in ord has occured.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected boolean noteOrdChangeOnly(DataCell cell,
                                        int oldOrd,
                                        int newOrd)
        throws SystemErrorException
    {
        final String mName = "DCellChangeListeners::noteOrdChangeOnly()";
        int i;
        int j;
        int oldFargListLen;
        int newFargListLen;
        int indexOfOldIncarnation;
        int indexOfNewIncarnation;
        long id;
        FormalArgument oldFarg;
        FormalArgument oldIncarnation;
        FormalArgument newIncarnation;

        if ( cell == null )
        {
            throw new SystemErrorException(mName + ": cell null on entry.");
        }

        if ( ( this.changeNoted ) ||
             ( this.ordChanged ) ||
             ( this.onsetChanged ) ||
             ( this.oldOnset != null ) ||
             ( this.newOnset != null ) ||
             ( this.offsetChanged ) ||
             ( this.oldOffset != null ) ||
             ( this.newOffset != null ) ||
             ( this.valChanged ) ||
             ( this.oldVal != null ) ||
             ( this.newVal != null ) ||
             ( this.selectedChanged ) )
        {
            throw new SystemErrorException(mName + ": change already noted?!?");
        }


        /*** test to see if the ord has changed ***/

        if ( oldOrd != newOrd )
        {
            this.changeNoted = true;
            this.ordChanged = true;
            this.oldOrd = oldOrd;
            this.newOrd = newOrd;
        }

        return this.changeNoted;

    } /* DataCellListeners::noteOrdChangeOnly() */


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
        final String mName = "DataCellListeners::NotifyListenersOfChanges()";
        DBElement dbe;
        ExternalDataCellListener el;
        InternalDataCellListener il;

        if ( this.changeNoted )
        {
            boolean nonTrivial = (
                    this.commentChanged ||
                    this.offsetChanged ||
                    this.onsetChanged ||
                    this.ordChanged ||
                    this.valChanged);

            if(nonTrivial) {
                // The database has been modified!
                db.modifyDatabase();
            } nonTrivial = false;

            // first, notify the intenal listeners...
            for ( Long id : this.ils )
            {
                dbe = this.db.idx.getElement(id); // throws system error on failure

                if ( ! ( dbe instanceof InternalDataCellListener ) )
                {
                    throw new SystemErrorException(mName +
                            ": dbe not a InternalDCellChangeListener.");
                }

                il = (InternalDataCellListener)dbe;

                il.DCellChanged(this.db,
                                this.itsColID,
                                this.itsCellID,
                                this.ordChanged,
                                this.oldOrd,
                                this.newOrd,
                                this.onsetChanged,
                                this.oldOnset,
                                this.newOnset,
                                this.offsetChanged,
                                this.oldOffset,
                                this.newOffset,
                                this.valChanged,
                                this.oldVal,
                                this.newVal,
                                this.commentChanged,
                                this.oldComment,
                                this.newComment);
            }


            // then notify the external listeners...
            for ( Object o : this.els )
            {
                el = (ExternalDataCellListener)o;
                el.DCellChanged(this.db,
                                this.itsColID,
                                this.itsCellID,
                                this.ordChanged,
                                this.oldOrd,
                                this.newOrd,
                                this.onsetChanged,
                                this.oldOnset,
                                this.newOnset,
                                this.offsetChanged,
                                this.oldOffset,
                                this.newOffset,
                                this.valChanged,
                                this.oldVal,
                                this.newVal,
                                this.selectedChanged,
                                this.oldSelected,
                                this.newSelected,
                                this.commentChanged,
                                this.oldComment,
                                this.newComment);
            }

            // and finally, discard the old change notes.
            this.changeNoted = false;
            this.ordChanged = false;
            this.oldOrd = -1;
            this.newOrd = -1;
            this.onsetChanged = false;
            this.oldOnset = null;
            this.newOnset = null;
            this.offsetChanged = false;
            this.oldOffset = null;
            this.newOffset = null;
            this.valChanged = false;
            this.oldVal = null;
            this.newVal = null;
            this.selectedChanged = false;
            this.oldSelected = false;
            this.newSelected = false;
            this.commentChanged = false;
            this.oldComment = null;
            this.newComment = null;
        }

        return;

    } /* DataCellListeners::notifyListenersOfChange() */


    /**
     * notifyListenersOfDeletion()
     *
     * Advise any listeners of the deletion of the associated DataCell.
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
        final String mName = "DataCellListeners::notifyListenersOfDeletion()";
        DBElement dbe;
        ExternalDataCellListener el;
        InternalDataCellListener il;

        // The database has been modified!
        db.modifyDatabase();

        // first, notify the internal listeners...
        for ( Long id : this.ils )
        {
            dbe = this.db.idx.getElement(id); // throws system error on failure

            if ( ! ( dbe instanceof InternalDataCellListener ) )
            {
                throw new SystemErrorException(mName +
                        ": dbe not a InternalDataCellListener.");
            }

            il = (InternalDataCellListener)dbe;

            il.DCellDeleted(this.db, this.itsColID, this.itsCellID);
        }


        // then notify the external listeners...
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalDataCellListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalDCellChangeListener.");
            }

            el = (ExternalDataCellListener)o;

            el.DCellDeleted(this.db, this.itsColID, this.itsCellID);
        }

        return;

    } /* DataCellListeners::notifyListenersOfDeletion() */


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

    protected void deregisterExternalListener(ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "DCellChangeListeners::deregisterExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.DeleteExternalListener(el);

        return;

    } /* DataCellListeners::deregisterExternalListener() */


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
        final String mName = "DCellChangeListeners::deregisterInternalListener()";

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }

        this.DeleteInternalListener(ID);

        return;

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

    protected void registerExternalListener(ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "DCellChangeListeners::registerExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.AddExternalListener(el);

        return;

    } /* DataCellListeners::registerExternalListener() */


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
        final String mName = "DCellChangeListeners::registerInternalListener()";
        DBElement dbe = null;

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }

        dbe = this.db.idx.getElement(ID); // throws system error on failure

        if ( ! ( dbe instanceof InternalDataCellListener ) )
        {
            throw new SystemErrorException(mName +
                    ": dbe not a InternalDCellChangeListener.");
        }

        this.AddInternalListener(ID);

        return;

    } /* DataCellListeners::registerExternalListener() */

} /* class DataCellListeners */
