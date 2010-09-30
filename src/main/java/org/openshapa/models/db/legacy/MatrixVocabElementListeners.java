/*
 * MatrixVocabElementListeners.java
 *
 * Created on August 25, 2008, 7:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

import org.openshapa.OpenSHAPA;

/**
 * Class MatrixVocabElementListeners
 *
 * Subclass of VocabElementListeners used to manage the mechanics registering
 * and de-registering internal and external listeners for changes in Matrix
 * Vocab Elements, and notifying the registered listeners when changes occur.
 *
 * This subclass is made necessary by the addition of support for column
 * predicates -- the predicates implied by data columns.  Column predicates
 * will not be permitted in all types of OpenSHAPA databases, but we must
 * be able to support them when they appear.
 *
 * The basic difference between the MatrixVocabElementListeners class and
 * the VocabElementListeners class is the addition of fields used to track
 * changes in the cpfArgList field of MatrixVocabElement.
 *
 * Recall that this field is used to store the formal argument list for the
 * column predicate implied by the matrix vocab element.  While this argument
 * list is completely derivative, and changes to it can be inferred from the
 * list of changes to the regular argument list, it is convenient to construct
 * change list for it here so as to simplify processing elsewhere.
 *
 *                                               -- 2/2/08
 *
 * Changes:
 *
 *    - Added the itsColID field to MatrixVocabElementListeners.
 *
 *      This field is used to ensure that the instance of DataColumn
 *      associated with itsVE is the first internal listener
 *      called when notifyInternalListenersOfChange() is called.
 *
 *      This in turn simplifies management of the case in which the column
 *      predicate implied by the associated MVE appears in a cell located
 *      in the data column associated with itsVE.
 *
 *                                              -- 9/20/09
 */

public class MatrixVocabElementListeners extends VocabElementListeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * cpFargListChanged: Boolean flag used to record the fact that the
     *      column predicate formal argument list of the associated matrix
     *      vocab element has changed.
     *
     * oldCPFargList: Reference to a vector of FormalArgument containing a copy
     *      of the old column predicate formal argument list if it has changed,
     *      and null otherwise.
     *
     * newCPFargList: Reference to a vector of FormalArgument containing a copy
     *      of the new column predicate formal argument list if it has changed,
     *      and null otherwise.
     *
     * cpn2o: Reference to an array of long of length equal to the new column
     *      predicate formal argument list mapping the indicies of all formal
     *      arguments in the new column predicate formal argument list, to the
     *      index of that argument in the old column predicate formal argument
     *      list, or to -1 if the formal argument doesn't appear in the old
     *      column predicate formal argument list.
     *
     * cpo2n: Reference to an array of long of length equal to the old column
     *      predicate formal argument list mapping the indicies of all formal
     *      arguments in the old column predicate formal argument list, to the
     *      index of that argument in the new column predicate formal argument
     *      list, or to -1 if the formal argument doesn't appear in the new
     *      column predicate formal argument list.
     *
     * cpFargNameChanged: Reference to an array of boolean of length equal to
     *      the new column predicate formal argument list.  Cells in the array
     *      are set to true iff the formal argument at the corresponding
     *      location in the new column predicate formal argument list appeared
     *      in both the new and old column predicate formal argument lists,
     *      and has changed its name.
     *
     * cpFargSubRangeChanged: Reference to an array of boolean of length equal to
     *      the new column predicate formal argument list.  Cells in the array
     *      are set to true iff the formal argument at the corresponding
     *      location in the new column predicate formal argument list appeared
     *      in both the new and old column predicate formal argument lists, and
     *      has changed the value of its subRange field.
     *
     * cpFargRangeChanged: Reference to an array of boolean of length equal to
     *      the new column predicate formal argument list.  Cells in the array
     *      are set to true iff the formal argument at the corresponding
     *      location in the new column predicate formal argument list appeared
     *      in both the new and old column predicate formal argument lists,
     *      and has changed the value(s) of the fields specifying the range
     *      of permissable values.
     *
     * cpFargDeleted: Reference to an array of boolean of length equal to
     *      the old column predicate formal argument list.  Cells in the array
     *      are set to true iff the formal argument at the corresponding
     *      location in the old column predicate formal argument list does not
     *      appear in the new column predicate formal argument list, and thus
     *      is deleted.
     *
     * cpFargInserted: Reference to an array of boolean of length equal to
     *      the new column predicate formal argument list.  Cells in the array
     *      are set to true iff the formal argument at the corresponding
     *      location in the new column predicate formal argument list does
     *      not appear in the old column predicate formal argument list, and
     *      thus has been inserted.
     */

    boolean cpFargListChanged = false;
    java.util.Vector<FormalArgument> oldCPFargList = null;
    java.util.Vector<FormalArgument> newCPFargList = null;
    long[] cpn2o = null;
    long[] cpo2n = null;
    boolean[] cpFargNameChanged = null;
    boolean[] cpFargSubRangeChanged = null;
    boolean[] cpFargRangeChanged = null;
    boolean[] cpFargDeleted = null;
    boolean[] cpFargInserted = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * MatrixVocabElementListeners
     *
     * For now at least, only one constructors:
     *
     * The initial constructor takes a db, and a reference to a
     * MatrixVocabElement, and sets up the new instance to start managing
     * change listeners for the instance of matrix vocab element.
     *
     * No copy constructor, as the plan is to use the same instance of
     * MatrixVocabElementListeners to manage listeners for all incarnations
     * of a given MatrixVocabElement.
     *
     *                                               -- 8/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public MatrixVocabElementListeners(Database db,
                                       VocabElement mve)
        throws SystemErrorException
    {
        super(db, mve);

        final String mName =
            "MatrixVocabElementListeners::MatrixVocabElementListeners(db, mve)";

        if ( ! ( mve instanceof MatrixVocabElement ) )
        {
             throw new SystemErrorException(mName +
                     ": mve not a MatrixVocabElement.");
        }

    } /* MatrixVocabElementListeners::MatrixVocabElementListeners(db, mve) */



    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * UpdateItsVE() -- Override
     *
     * Update the itsVE field for a new incarnation of the target VocabElement.
     *
     * Verify that ve is an instance of MatrxiVocabElement, and then call the
     * inherited method.
     *
     *                                               -- 8/25/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateItsVE(VocabElement ve)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElementListeners::UpdateItsVE()";

        if ( ve == null )
        {
            throw new SystemErrorException(mName + ": ve null on entry.");
        }

        if ( ! ( this.itsVE instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           ": ve not a MatrixVocabElement.");
        }

        super.updateItsVE(ve);

        return;

    } /* VocabElementListeners::updateItsVE() */


    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/

    /**
     * discardChangeNotes() -- override
     *
     * Discard all notes on changes that should be reported to the listeners.
     *
     *                                                   -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void discardChangeNotes()
    {
        super.discardChangeNotes();

        this.cpFargListChanged = false;
        this.oldCPFargList = null;
        this.newCPFargList = null;
        this.cpn2o = null;
        this.cpo2n = null;
        this.cpFargNameChanged = null;
        this.cpFargSubRangeChanged = null;
        this.cpFargRangeChanged = null;
        this.cpFargDeleted = null;
        this.cpFargInserted = null;

        return;

    } /* MatrixVocabElementListeners::discardChangeNotes() */

    /**
     * noteChange() -- override
     *
     * Given references to the old and new versions of the target
     * MatrixVocabElement, make note of any changes that should be reported
     * to the listeners.
     *
     * To do this, first verifying that the supplied vocab elements are matrix
     * vocab elements.  Then call the inherited method, and use the results
     * of this call to construct the column predicate formal argument list
     * related data structures
     *
     *                                                   -- 8/25/08
     *
     * Changes:
     *
     *    - None.
     */

    protected boolean noteChange(VocabElement oldVE,
                                 VocabElement newVE)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElementListeners::noteChanges()";

        if ( ( oldVE == null ) || ( newVE == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": oldVE or newVE null on entry.");
        }

        if ( ( ! ( oldVE instanceof MatrixVocabElement ) ) ||
             ( ! ( newVE instanceof MatrixVocabElement ) ) )
        {
            throw new SystemErrorException(mName +
                    ": oldVE or newVE not a MatrixVocabElement.");
        }

        if ( ( this.oldCPFargList != null ) ||
             ( this.newCPFargList != null ) ||
             ( this.cpn2o != null ) ||
             ( this.cpo2n != null ) ||
             ( this.cpFargNameChanged != null ) ||
             ( this.cpFargSubRangeChanged != null ) ||
             ( this.cpFargRangeChanged != null ) ||
             ( this.cpFargDeleted != null ) ||
             ( this.cpFargInserted != null ) )
        {
            throw new SystemErrorException(mName + ": change already noted?!?");
        }

        super.noteChange(oldVE, newVE);

        /* At this point, all the note change related fields defined in
         * VocabElementListeners should have been initialized.  Use them to
         * initialize the similar column predicate related fields defined
         * in this class.
         *
         * Since we have already determined if the regular matrix formal
         * argument list has changed, and if so set up the arrays describing
         * the change, we need only look at this.changeNoted and
         * this.fargListChanged to see if there is anything to do.
         *
         * If there is, use the fact that the formal argument list of the
         * column predicate implied by a matrix definition is simply the
         * matrix formal argument list with "<ord>, <onset>, <offset>, "
         * prepended.  These initial formal arguments can't be altered, so we
         * can use this fact to infer the column predicate versions of the
         * change descriptions from the matrix versions.
         */

        if ( ( this.changeNoted ) && ( this.fargListChanged ) )
        {
            int i;
            int oldCPFargListLen;
            int newCPFargListLen;
            int oldFargListLen;
            int newFargListLen;
            MatrixVocabElement oldMVE = (MatrixVocabElement)oldVE;
            MatrixVocabElement newMVE = (MatrixVocabElement)newVE;

            assert( this.oldFargList != null );
            assert( this.newFargList != null );
            assert( this.n2o != null );
            assert( this.o2n != null );
            assert( this.fargNameChanged != null );
            assert( this.fargSubRangeChanged != null );
            assert( this.fargRangeChanged != null );
            assert( this.fargDeleted != null );
            assert( this.fargInserted != null );

            oldFargListLen = this.oldFargList.size();
            newFargListLen = this.newFargList.size();

            assert( oldFargListLen >= 1 );
            assert( newFargListLen >= 1 );

            this.oldCPFargList = oldMVE.copyCPFormalArgList();
            this.newCPFargList = newMVE.copyCPFormalArgList();

            oldCPFargListLen = this.oldCPFargList.size();
            newCPFargListLen = this.newCPFargList.size();

            assert( oldCPFargListLen == (oldFargListLen + 3) );
            assert( newCPFargListLen == (newFargListLen + 3) );

            assert( oldCPFargListLen >= 4 );
            assert( newCPFargListLen >= 4 );

            this.cpo2n = new long[oldCPFargListLen];
            this.cpFargDeleted = new boolean[oldCPFargListLen];

            for ( i = 0; i < 3; i++ )
            {
                this.cpo2n[i] = i;
                this.cpFargDeleted[i] = false;
            }

            for ( i = 3; i < oldCPFargListLen; i++ )
            {
                this.cpo2n[i] = this.o2n[i - 3] + 3;
                this.cpFargDeleted[i] = this.fargDeleted[i - 3];
            }

            this.cpn2o = new long[newCPFargListLen];
            this.cpFargNameChanged = new boolean[newCPFargListLen];
            this.cpFargSubRangeChanged = new boolean[newCPFargListLen];
            this.cpFargRangeChanged = new boolean[newCPFargListLen];
            this.cpFargInserted = new boolean[newCPFargListLen];

            for ( i = 0; i < 3; i++ )
            {
                this.cpn2o[i] = i;
                this.cpFargNameChanged[i] = false;
                this.cpFargSubRangeChanged[i] = false;
                this.cpFargRangeChanged[i] = false;
                this.cpFargInserted[i] = false;
            }

            for ( i = 3; i < newCPFargListLen; i++ )
            {
                this.cpn2o[i] = this.n2o[i - 3] + 3;
                this.cpFargNameChanged[i] = this.fargNameChanged[i - 3];
                this.cpFargSubRangeChanged[i] = this.fargSubRangeChanged[i - 3];
                this.cpFargRangeChanged[i] = this.fargRangeChanged[i - 3];
                this.cpFargInserted[i] = this.fargInserted[i - 3];
            }
        }

        return this.changeNoted;

    } /* MatrixVocabElementListeners::noteChange() */


    /**
     *
     * notifyInternalListenersOfChange()
     *
     * Call the internal listeners to advise them of changes.
     *
     *                                           -- 8/26/08
     *
     * Changes:
     *
     *    - None
     */

    protected void notifyInternalListenersOfChange()
        throws SystemErrorException
    {
        final String mName =
                "MatrixVocabElementListener::NotifyInternalListenersOfChange()";
        DBElement dbe;
        InternalMatrixVocabElementListener il;

        if ( ! this.changeNoted )
        {
            throw new SystemErrorException(mName + "no changes?!?");
        }

        // The database has been modified!
        db.markAsChanged();

        for ( Long id : this.ils )
        {
            dbe = this.db.idx.getElement(id); // throws system error on failure

            this.checkInternalListenerType(dbe);

            il = (InternalMatrixVocabElementListener)dbe;

            il.MVEChanged(this.db,
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
                          this.newFargList,
                          this.cpn2o,
                          this.cpo2n,
                          this.cpFargNameChanged,
                          this.cpFargSubRangeChanged,
                          this.cpFargRangeChanged,
                          this.cpFargDeleted,
                          this.cpFargInserted,
                          this.oldCPFargList,
                          this.newCPFargList);
        }

        return;

    } /* MatrixVocabElementListeners::notifyInternalListenersOfChange() */


    /**
     * notifyInternalListenersOfDeletion()
     *
     * Advise any internal listeners of the deletion of the associated
     * MatrixVocabElement.
     *
     *                                                   -- 8/26/08
     *
     * Changes:
     *
     *    - None
     */

    protected void notifyInternalListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName =
            "MatrixVocabElementListeners::notifyInternalListenersOfDeletion()";
        DBElement dbe;
        InternalMatrixVocabElementListener il;

        // The database has been modified!
        db.markAsChanged();


        for ( Long id : this.ils )
        {
            dbe = this.db.idx.getElement(id); // throws system error on failure

            this.checkInternalListenerType(dbe);

            il = (InternalMatrixVocabElementListener)dbe;

            il.MVEDeleted(this.db, this.itsVEID);
        }

        return;

    } /* MatrixVocabElementListeners::notifyInternalListenersOfDeletion() */


    /*************************************************************************/
    /*********************** Listener Management: ****************************/
    /*************************************************************************/

    /**
     * checkInternalListenerType()
     *
     * Given a reference to an internal listener, check to see if it is
     * correctly typed.  If it is, do nothing.  If it isn't, throw a system
     * error with the appropriate diagnostic message.
     *
     *                                               -- 8/26/98
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

        if ( ! ( il instanceof InternalMatrixVocabElementListener ) )
        {
            throw new SystemErrorException(mName +
                    "il is not an InternalMatrixVocabElementListener");
        }

        return;

    } /* MatrixVocabElementListeners::checkInternalListenerType() */

} /* Class MatrixVocabElementListeners */
