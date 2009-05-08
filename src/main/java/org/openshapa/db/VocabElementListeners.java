/*
 * VocabElementListeners.java
 *
 * Created on February 3, 2008, 2:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

/**
 * Class VocabElementListeners
 *
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for changed in Vocab Elements,
 * and notifying the registered listeners when changes occur.
 *
 *                                              JRM -- 2/2/08
 *
 * @author mainzer
 */
public class VocabElementListeners extends Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * itsVE: Reference to the instance of VocabElement for which this
     *      instance of VocabElementListeners is managing change listeners.
     *
     * itsVEID: Long containing the ID assigned to itsVE.
     *
     * changeNoted:  Boolean flag used to note if any reportable changes were
     *      detected between the old and new versions of the target VocabElement
     *      were detected the last time the two versions were examined.
     *
     *      Thus, given the current set of reportable changes, changeNoted
     *      will be true iff (nameChanged || varLenChanged || argListChanged).
     *
     *      Note that this field, like all the other fields used to note changes
     *      between subsequent versions of the target VocabElement, is reset
     *      after the listeners are notified.
     *
     * nameChanged:  Boolean flag used to record the fact that the name of the
     *      associated vocab element has changed.
     *
     * oldName: String containing the old name if the associated vocab element
     *      name has changed, and null otherwise.
     *
     * newName: String containing the new name if the associated vocab element
     *      name has changed, and null otherwise.
     *
     * varLenChanged:  Boolean flag used to record the fact that the varLen
     *      field of the associated vocab element has changed value.
     *
     * oldVarLen: Boolean containing the old value of the varLen field of the
     *      associated vocab element if it has changed, and undefined otherwise.
     *
     * newVarLen: Boolean containing the new value of the varLen field of the
     *      assocated vocab element if it has changed, and undefined otherwise.
     *
     * fargListChanged: Boolean flag used to record the fact that the formal
     *      argument list of the associated vocab element has changed.
     *
     * oldFargList: Reference to a vector of FormalArgument containing a copy
     *      of the old formal argument list if it has changed, and null otherwise.
     *
     * newFargList: Reference to a vector of FormalArgument containing a copy
     *      of the new formal argument list if it has changed, and null
     *      otherwise.
     *
     * n2o: Reference to an array of long of length equal to the new formal
     *      argument list mapping the indicies of all formal arguments in the
     *      new formal argument list, to the index of that argument in the old
     *      formal argument list, or to -1 if the formal argument doesn't appear
     *      in the old formal argument list.
     *
     * o2n: Reference to an array of long of length equal to the old formal
     *      argument list mapping the indicies of all formal arguments in the
     *      old formal argument list, to the index of that argument in the new
     *      formal argument list, or to -1 if the formal argument doesn't appear
     *      in the new formal argument list.
     *
     * fargNameChanged: Reference to an array of boolean of length equal to
     *      the new formal argument list.  Cells in the array are set to true
     *      iff the formal argument at the corresponding location in the
     *      new formal argument list appeared in both the new and old formal
     *      argument lists, and has changed its name.
     *
     * fargSubRangeChanged: Reference to an array of boolean of length equal to
     *      the new formal argument list.  Cells in the array are set to true
     *      iff the formal argument at the corresponding location in the
     *      new formal argument list appeared in both the new and old formal
     *      argument lists, and has changed the value of its subRange field.
     *
     * fargRangeChanged: Reference to an array of boolean of length equal to
     *      the new formal argument list.  Cells in the array are set to true
     *      iff the formal argument at the corresponding location in the
     *      new formal argument list appeared in both the new and old formal
     *      argument lists, and has changed the value(s) of the fields
     *      specifying the range of permissable values.
     *
     * fargDeleted: Reference to an array of boolean of length equal to
     *      the old formal argument list.  Cells in the array are set to true
     *      iff the formal argument at the corresponding location in the
     *      old formal argument list does not appear in the new formal
     *      argument list, and thus is deleted.
     *
     * fargInserted: Reference to an array of boolean of length equal to
     *      the new formal argument list.  Cells in the array are set to true
     *      iff the formal argument at the corresponding location in the
     *      new formal argument list does not appear in the old formal
     *      argument list, and thus has been inserted.
     */

    protected VocabElement itsVE = null;
    protected long itsVEID = DBIndex.INVALID_ID;

    protected boolean changeNoted = false;

    protected boolean nameChanged = false;
    protected String oldName = null;
    protected String newName = null;

    protected boolean varLenChanged = false;
    protected boolean oldVarLen = false;
    protected boolean newVarLen = false;

    protected boolean fargListChanged = false;
    protected java.util.Vector<FormalArgument> oldFargList = null;
    protected java.util.Vector<FormalArgument> newFargList = null;
    protected long[] n2o = null;
    protected long[] o2n = null;
    protected boolean[] fargNameChanged = null;
    protected boolean[] fargSubRangeChanged = null;
    protected boolean[] fargRangeChanged = null;
    protected boolean[] fargDeleted = null;
    protected boolean[] fargInserted = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * VocabElementListeners
     *
     * For now at least, only one constructors:
     *
     * The initial constructor takes a db, and a reference to a VocabElement,
     * and sets up the new instance to start managing change listeners for the
     * instance of Vocab element.
     *
     * No copy constructor, as the plan is to use the same instance of
     * VocabElementListeners to manage listeners for all incarnations of a given
     * VocabElement.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    public VocabElementListeners(Database db,
                             VocabElement ve)
        throws SystemErrorException
    {
        super(db);

        final String mName = "VEChangeListeners::VEChangeListerners(db, ve)";

        if ( ve == null )
        {
            throw new SystemErrorException(mName + ": ve null on entry.");
        }

        if ( ve.getDB() != db )
        {
             throw new SystemErrorException(mName + ": ve.getDB() != db.");
        }

        if ( ve.getID() == DBIndex.INVALID_ID )
        {
             throw new SystemErrorException(mName + ": ve.getID() is invalid.");
        }

        this.itsVE = ve;
        this.itsVEID = ve.getID();

    } /* VocabElementListeners::VocabElementListeners(db, ve) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * UpdateItsVE()
     *
     * Update the itsVE field for a new incarnation of the target VocabElement.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateItsVE(VocabElement ve)
        throws SystemErrorException
    {
        final String mName = "VEChangeListeners::UpdateItsVE()";


        if ( ve == null )
        {
            throw new SystemErrorException(mName + ": ve null on entry.");
        }

        if ( ve.getDB() != this.db )
        {
             throw new SystemErrorException(mName + ": ve.getDB() != db.");
        }

        if ( ve.getID() != this.itsVEID )
        {
             throw new SystemErrorException(mName + ": ve.getID() != itsVEID.");
        }

        this.itsVE = ve;

        return;

    } /* VocabElementListeners::updateItsVE() */


    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/

    /**
     * discardChangeNotes()
     *
     * Discard all notes on changes that should be reported to the listeners.
     *
     *                                                  JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void discardChangeNotes()
    {
        this.changeNoted = false;
        this.nameChanged = false;
        this.oldName = null;
        this.newName = null;
        this.varLenChanged = false;
        this.fargListChanged = false;
        this.oldFargList = null;
        this.newFargList = null;
        this.n2o = null;
        this.o2n = null;
        this.fargNameChanged = null;
        this.fargSubRangeChanged = null;
        this.fargRangeChanged = null;
        this.fargDeleted = null;
        this.fargInserted = null;

        return;
    }

    /**
     * noteChange()
     *
     * Given references to the old and new versions of the target VocabElement,
     * make note of any changes that should be reported to the listeners.
     *
     *                                                  JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected boolean noteChange(VocabElement oldVE,
                                 VocabElement newVE)
        throws SystemErrorException
    {
        final String mName = "VEChangeListeners::noteChanges()";
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

        if ( ( oldVE == null ) || ( newVE == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldVE or newVE null on entry.");
        }

        if ( ( oldVE.getID() != this.itsVEID ) ||
             ( newVE.getID() != this.itsVEID ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldVE or newVE has bad ID.");
        }

        if ( ( this.changeNoted ) ||
             ( this.nameChanged ) ||
             ( this.oldName != null ) ||
             ( this.newName != null ) ||
             ( this.varLenChanged ) ||
             ( this.fargListChanged ) ||
             ( this.oldFargList != null ) ||
             ( this.newFargList != null ) ||
             ( this.n2o != null ) ||
             ( this.o2n != null ) ||
             ( this.fargNameChanged != null ) ||
             ( this.fargSubRangeChanged != null ) ||
             ( this.fargRangeChanged != null ) ||
             ( this.fargDeleted != null ) ||
             ( this.fargInserted != null ) )
        {
            throw new SystemErrorException(mName + ": change already noted?!?");
        }


        /*** test to see if the name has changed ***/

        this.oldName = oldVE.getName();
        this.newName = newVE.getName();

        if ( ( this.oldName == null ) || ( this.newName == null ) )
        {
            throw new SystemErrorException(mName +
                    ": oldVE.getName() and/or newVE.getName() returned null.");
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


        /*** test to see if the var len field has changed ***/

        this.oldVarLen = oldVE.getVarLen();
        this.newVarLen = newVE.getVarLen();

        if ( this.oldVarLen != this.newVarLen )
        {
            this.changeNoted = true;
            this.varLenChanged = true;
        }


        /*** test to see if the formal arguments list has changed ***/

        this.oldFargList = oldVE.copyFormalArgList();
        this.newFargList = newVE.copyFormalArgList();

        oldFargListLen = this.oldFargList.size();
        newFargListLen = this.newFargList.size();

        this.o2n = new long[oldFargListLen];
        this.fargDeleted = new boolean[oldFargListLen];

        for ( i = 0; i < oldFargListLen; i++ )
        {
            this.o2n[i] = -1;
            this.fargDeleted[i] = true;
        }

        this.n2o = new long[newFargListLen];
        this.fargNameChanged = new boolean[newFargListLen];
        this.fargSubRangeChanged = new boolean[newFargListLen];
        this.fargRangeChanged = new boolean[newFargListLen];
        this.fargInserted = new boolean[newFargListLen];

        for ( j = 0; j < newFargListLen; j++ )
        {
            this.n2o[j] = -1;
            this.fargNameChanged[j] = false;
            this.fargSubRangeChanged[j] = false;
            this.fargRangeChanged[j] = false;
            this.fargInserted[j] = true;
        }

        /* by this point, the old and new formal argument lists have been
         * checked for errors, so we don't need to do any error checking
         * here.
         */
        for ( j = 0; j < newFargListLen; j++ )
        {
            indexOfNewIncarnation = j;
            newIncarnation = this.newFargList.get(j);

            if ( newIncarnation == null )
            {
                throw new SystemErrorException(mName +
                                               "newIncarnation is null(1)?!?");
            }

            id = newIncarnation.getID();

            if ( id == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                          "newIncarnation has invalid ID?!?!?");
            }

            // This will throw a system error if the target id has not been
            // assigned.
            if ( ! ( this.db.idx.getElement(id) instanceof FormalArgument ) )
            {
                throw new SystemErrorException(mName +
                    "newIncarnation id doesn't reference a FormalArgument?!?");
            }

            // by this point, any newly inserted formal arguments must have
            // been assigned IDs (as otherwise the internal listeners would
            // be unable to adjust fully to the changes).  Thus we must find
            // newly inserted formal arguments the hard way -- by scanning the
            // old formal argument list and not finding an entry with the same
            // ID.

            oldIncarnation = null;      // i.e. we haven't found it yet -- if
            indexOfOldIncarnation = -1; //      it exists.

            i = 0;
            while ( ( i < oldFargListLen ) &&
                    ( oldIncarnation == null ) )
            {
                oldFarg = this.oldFargList.get(i);

                if ( oldFarg == null )
                {
                    throw new SystemErrorException(mName +
                                                   ": oldFarg is null!?!");
                }
                else if ( ! ( oldFarg instanceof FormalArgument ) )
                {
                    throw new SystemErrorException(mName +
                            "oldFarg not a FormalArgument?!?");
                }
                else if ( id == oldFarg.getID() )
                {
                    oldIncarnation = oldFarg;
                    indexOfOldIncarnation = i;
                }

                i++;
            } // end while loop over old formal argument list

            if ( oldIncarnation != null )
            {
                // set up the mappings between the locations of the old
                // and new incarnations of the formal argument.  Observe that
                // since we have initialized all entries in the n2o and
                // o2n arrays to -1, if we don't find a match for a new
                // incarnation, its entry in n2o will contain -1, indicating
                // that it is a new entry.  Similarly, for any old incarnation,
                // if no new incarnation matches it, its entry in o2n will
                // contain -1, indicating that it has been deleted.
                //
                // To make things easier for the poor developer who has been
                // three nights without sleep chasing bugs, we also set up the
                // fargDeleted and fargInserted arrays of boolean.

                this.o2n[indexOfOldIncarnation] = indexOfNewIncarnation;
                this.n2o[indexOfNewIncarnation] = indexOfOldIncarnation;
                this.fargDeleted[indexOfOldIncarnation] = false;

                // now check to see if the contents of the formal argument has
                // changed between the old and new incarnations.

                this.fargNameChanged[indexOfNewIncarnation] =
                        FormalArgument.FANameChanged(oldIncarnation,
                                                     newIncarnation);

                this.fargSubRangeChanged[indexOfNewIncarnation] =
                        FormalArgument.FASubRangeChanged(oldIncarnation,
                                                         newIncarnation);

                this.fargRangeChanged[indexOfNewIncarnation] =
                        FormalArgument.FARangeChanged(oldIncarnation,
                                                      newIncarnation);
            }
            else // it is a new formal argument
            {
                this.fargInserted[indexOfNewIncarnation] = true;
            }
        } // end for loop over new formal argument list


        // summerize the results from above for use by the external listeners

        if ( newFargListLen != oldFargListLen )
        {
            this.changeNoted = true;
            this.fargListChanged = true;
        }
        else
        {
            for ( j = 0 ; j < newFargListLen; j++ )
            {
                if ( ( this.n2o[j] != j ) ||
                     ( this.fargNameChanged [j] ) ||
                     ( this.fargSubRangeChanged [j] ) ||
                     ( this.fargRangeChanged [j] ) )
                {
                    this.changeNoted = true;
                    this.fargListChanged = true;
                }
            }

            // don't need to check o2n, as the old and new formal argument
            // list are of the same length, and thus if no fromal argument was
            // inserted, no formal argument was deleted.
        }

        return this.changeNoted;

    } /* VocabElementListeners::noteChange() */


    /**
     *
     * notifyExternalListenersOfChange()
     *
     * Call the external listeners to advise them of changes.
     *
     *                                              JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyExternalListenersOfChange()
        throws SystemErrorException
    {
        final String mName =
                "VEChangeListener::NotifyExternalListenersOfChanges()";
        DBElement dbe;
        ExternalVocabElementListener el;

        if ( ! this.changeNoted )
        {
            throw new SystemErrorException(mName + "no changes?!?");
        }

        for ( Object o : this.els )
        {
            el = (ExternalVocabElementListener)o;
            el.VEChanged(this.db,
                         this.itsVEID,
                         this.nameChanged,
                         this.oldName,
                         this.newName,
                         this.varLenChanged,
                         this.oldVarLen,
                         this.newVarLen,
                         this.fargListChanged,
                         this.oldFargList,
                         this.newFargList);
        }

        return;

    } /* VocabElementListeners::notifyExternalListenersOfChange() */


    /**
     *
     * notifyInternalListenersOfChange()
     *
     * Call the internal listeners to advise them of changes.
     *
     *                                          JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyInternalListenersOfChange()
        throws SystemErrorException
    {
        final String mName =
                "VEChangeListener::NotifyInternalListenersOfChanges()";
        DBElement dbe;
        InternalVocabElementListener il;

        if ( ! this.changeNoted )
        {
            throw new SystemErrorException(mName + "no changes?!?");
        }

        for ( Long id : this.ils )
        {
            dbe = this.db.idx.getElement(id); // throws system error on failure

            if ( ! ( dbe instanceof InternalVocabElementListener ) )
            {
                throw new SystemErrorException(mName +
                        ": dbe not a InternalVEChangeListener.");
            }

            il = (InternalVocabElementListener)dbe;

            il.VEChanged(this.db,
                         this.itsVEID,
                         this.nameChanged,
                         this.oldName,
                         this.newName,
                         this.varLenChanged,
                         this.oldVarLen,
                         this.newVarLen,
                         this.fargListChanged,
                         this.n2o,
                         this.o2n,
                         this.fargNameChanged,
                         this.fargSubRangeChanged,
                         this.fargRangeChanged,
                         this.fargDeleted,
                         this.fargInserted,
                         this.oldFargList,
                         this.newFargList);
        }

        return;

    } /* VocabElementListeners::notifyInternalListenersOfChange() */


    /**
     *
     * notifyListenersOfChange()
     *
     * If any notable changes have been noted since the last call to
     * NotifyListenersOfChanges(), call the listeners to advise them of the
     * changes, and then clear the fields used to note changes.
     *
     *                                                  JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfChange()
        throws SystemErrorException
    {
        if ( this.changeNoted )
        {
            // first, notify the intenal listeners...
            this.notifyInternalListenersOfChange();

            // then notify the external listeners...
            this.notifyExternalListenersOfChange();

            // and finally, discard the old change notes.

            this.discardChangeNotes();
        }

        return;

    } /* VocabElementListeners::notifyListenersOfChange() */


    /**
     * notifyExternalListenersOfDeletion()
     *
     * Advise any external listeners of the deletion of the associated
     * VocabElement.
     *
     *                                                  JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyExternalListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName =
                "VocabElementListeners::notifyExternalListenersOfDeletion()";
        DBElement dbe;
        ExternalVocabElementListener el;

        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalVocabElementListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalVEChangeListener.");
            }

            el = (ExternalVocabElementListener)o;

            el.VEDeleted(this.db, this.itsVEID);
        }

        return;

    } /* VocabElementListeners::notifyExternalListenersOfDeletion() */


    /**
     * notifyInternalListenersOfDeletion()
     *
     * Advise any internal listeners of the deletion of the associated
     * VocabElement.
     *
     *                                                  JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyInternalListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName =
                "VocabElementListeners::notifyInternalListenersOfDeletion()";
        DBElement dbe;
        InternalVocabElementListener il;

        for ( Long id : this.ils )
        {
            dbe = this.db.idx.getElement(id); // throws system error on failure

            if ( ! ( dbe instanceof InternalVocabElementListener ) )
            {
                throw new SystemErrorException(mName +
                        ": dbe not a InternalVEChangeListener.");
            }

            il = (InternalVocabElementListener)dbe;

            il.VEDeleted(this.db, this.itsVEID);
        }

        return;

    } /* VocabElementListeners::notifyInternalListenersOfDeletion() */


    /**
     * notifyListenersOfDeletion()
     *
     * Advise any listeners of the deletion of the associated VocabElement.
     *
     *                                                  JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName = "VEChangeListeners::notifyListenersOfDeletion()";

        // first, notify the intenal listeners...
        this.notifyInternalListenersOfDeletion();

        // then notify the external listeners...
        this.notifyExternalListenersOfDeletion();

        return;

    } /* VocabElementListeners::notifyListenersOfDeletion() */


    /*************************************************************************/
    /*********************** Listener Management: ****************************/
    /*************************************************************************/

    /**
     * checkExternalListenerType()
     *
     * Given a reference to an external listener, check to see if it is
     * correctly typed.  If it is, do nothing.  If it isn't, throw a system
     * error with the appropriate diagnostic message.
     *
     *                                              JRM -- 8/26/98
     *
     * Changes:
     *
     *    - None.
     */

    protected void checkExternalListenerType(Object el)
        throws SystemErrorException
    {
        final String mName =
                "VocabElementListeners::checkExternalListenerType():";

        if ( ! ( el instanceof ExternalVocabElementListener ) )
        {
            throw new SystemErrorException(mName +
                    "el is not an ExternalVEChangeListener");
        }

        return;

    } /* VocabElementListeners::checkExternalListenerType() */


    /**
     * checkInternalListenerType()
     *
     * Given a reference to an internal listener, check to see if it is
     * correctly typed.  If it is, do nothing.  If it isn't, throw a system
     * error with the appropriate diagnostic message.
     *
     *                                              JRM -- 8/26/98
     *
     * Changes:
     *
     *    - None.
     */

    protected void checkInternalListenerType(Object il)
        throws SystemErrorException
    {
        final String mName =
                "VocabElementListeners::checkInternalListenerType():";

        if ( ! ( il instanceof InternalVocabElementListener ) )
        {
            throw new SystemErrorException(mName +
                    "il is not an InternalVocabElementListener");
        }

        return;

    } /* VocabElementListeners::checkInternalListenerType() */


    /**
     * deregisterExternalListener()
     *
     * Deregister an external listener.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterExternalListener(Object el)
        throws SystemErrorException
    {
        final String mName = "VEChangeListener::deregisterExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.checkExternalListenerType(el);

        this.DeleteExternalListener(el);

        return;

    } /* VocabElementListeners::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * Deregister an external listener.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "VEChangeListener::deregisterInternalListener()";

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }

        this.DeleteInternalListener(ID);

        return;

    } /* VocabElementListeners::deregisterInternalListener() */


    /**
     * registerExternalListener()
     *
     * Register an external listener.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerExternalListener(Object el)
        throws SystemErrorException
    {
        final String mName = "VEChangeListeners::registerExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.checkExternalListenerType(el);

        this.AddExternalListener(el);

        return;

    } /* VocabElementListeners::registerExternalListener() */


    /**
     * registerInternalListener()
     *
     * Register an internal listener.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "VEChangeListeners::registerInternalListener()";
        DBElement dbe = null;

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }

        dbe = this.db.idx.getElement(ID); // throws system error on failure

        this.checkInternalListenerType(dbe);

        this.AddInternalListener(ID);

        return;

    } /* VocabElementListeners::registerExternalListener() */

} /* class VocabElementListeners */
