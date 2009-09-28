/*
 * ColumnListListeners.java
 *
 * Created on February 11, 2008, 10:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

/**
 * Class ColumnListListeners
 *
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for changes in the column
 * list, and sending notifications.
 *
 *                                               -- 2/11/08
 */
public class ColumnListListeners extends Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /* itsCL:   Pointer to the instance of ColumnList with which this
     *      instance of ColumnListListeners is associated.
     */

    ColumnList itsCL = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * ColumnListListeners
     *
     * For now at least, only one constructor.  The column List listeners
     * class is very simple, so all the construtor does is call the super and
     * then set itsCL.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    public ColumnListListeners(Database db,
                               ColumnList cl)
        throws SystemErrorException
    {
        super(db);

        final String mName = "ColumnListListeners::ColumnListListeners()";

        if ( cl == null )
        {
            throw new SystemErrorException(mName + "cl null on entry.");
        }

        this.itsCL = cl;

        return;

    } /* ColumnListListeners::ColumnListListeners(db, cl) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /*** none ***/


    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/

    // notifyListenersOfColDeletion()
    /**
     * Advise the listeners of the deletion of a Column of the specified
     * id from the column list.  Also send along copies of the old and
     * new column order vector.
     *
     *                                                   -- 2/11/08
     *
     * Changes:
     *
     *    - Added old_cov and new_cov parameters, and related code.
     *                                                  -- 7/31/09
     */

    protected void notifyListenersOfColDeletion(long colID,
                      final java.util.Vector<Long> old_cov,
                      final java.util.Vector<Long> new_cov)
        throws SystemErrorException
    {
        final String mName =
                "ColumnListListeners::notifyListenersOfColDeletion()";
        ExternalColumnListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalColumnListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not an ExternalColumnListListener.");
            }

            el = (ExternalColumnListListener)o;

            el.colDeletion(this.db, colID, old_cov, new_cov);
        }

        return;

    } /* ColumnListListeners::notifyListenersOfColDeletion() */


    // notifyListenersOfColInsertion()
    /**
     * Advise the listeners of the inserion of a VocabElement of the specified
     * id into the vocab list.  Also send along copies of the old and
     * new column order vector.
     *
     *                                                   -- 2/11/08
     *
     * Changes:
     *
     *    - Added old_cov and new_cov parameters, and related code.
     *                                                  -- 7/31/09
     */

    protected void notifyListenersOfColInsertion(long colID,
                      final java.util.Vector<Long> old_cov,
                      final java.util.Vector<Long> new_cov)
        throws SystemErrorException
    {
        final String mName =
                "ColumnListListeners::notifyListenersOfColInsertion()";
        ExternalColumnListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalColumnListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not an ExternalColumnListListener.");
            }

            el = (ExternalColumnListListener)o;

            el.colInsertion(this.db, colID, old_cov, new_cov);
        }

        return;

    } /* ColumnListListeners::notifyListenersOfColInsertion() */


    // notifyListenersOfColOrderVectorEdit()
    /**
     * Advise the listeners that the column order vector has been edited --
     * meaning that it has been re-arranged, without entries being either
     * added or deleted.
     *
     *                                                   -- 2/11/08
     *
     * Changes:
     *
     *    - Added old_cov and new_cov parameters, and related code.
     *                                                  -- 7/31/09
     */

    protected void notifyListenersOfColOrderVectorEdit(
                      final java.util.Vector<Long> old_cov,
                      final java.util.Vector<Long> new_cov)
        throws SystemErrorException
    {
        final String mName =
                "ColumnListListeners::notifyListenersOfColOrderVectorEdit()";
        ExternalColumnListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalColumnListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not an ExternalColumnListListener.");
            }

            el = (ExternalColumnListListener)o;

            el.colOrderVectorEdited(this.db, old_cov, new_cov);
        }

        return;

    } /* ColumnListListeners::notifyListenersOfColOrderVectorEdit() */


    /*************************************************************************/
    /*********************** Listener Management: ****************************/
    /*************************************************************************/

    /**
     * deregisterExternalListener()
     *
     * Deregister an external listener.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterExternalListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::deregisterExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.DeleteExternalListener(el);

        return;

    } /* ColumnListListeners::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * Deregister an internal listener.  Internal listeners not implemented
     * at present, so just throw a SystemErrorException.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::deregisterInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");

        // return; /* commented out to keep the compiler happy */

    } /* ColumnListListeners::deregisterInternalListener() */


    /**
     * registerExternalListener()
     *
     * Register an external listener.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerExternalListener(ExternalColumnListListener el)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::registerExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.AddExternalListener(el);

        return;

    } /* ColumnListListeners::registerExternalListener() */


    /**
     * registerInternalListener()
     *
     * Register an internal listener.  Internal listeners not implemented
     * at present, so just throw a SystemErrorException.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::registerInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");

        // return; /* commented out to keep the compiler happy */

    } /* ColumnListListeners::registerExternalListener() */

} /* class ColumnListListeners */
