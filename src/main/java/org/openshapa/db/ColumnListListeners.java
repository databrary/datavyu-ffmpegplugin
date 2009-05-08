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
 *                                              JRM -- 2/11/08
 *
 *
 * @author mainzer
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
     *                                              JRM -- 2/11/08
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

    /**
     * notifyListenersOfColDeletion()
     *
     * Advise the listeners of the deletion of a Column of the specified
     * id from the column list.
     *
     *                                                  JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfColDeletion(long colID)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::notifyListenersOfColDeletion()";
        ExternalColumnListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalColumnListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalColumnListListener.");
            }

            el = (ExternalColumnListListener)o;

            el.colDeletion(this.db, colID);
        }

        return;

    } /* ColumnListListeners::notifyListenersOfColDeletion() */


    /**
     * notifyListenersOfColInsertion()
     *
     * Advise the listeners of the inserion of a VocabElement of the specified
     * id into the vocab list.
     *
     *                                                  JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfColInsertion(long colID)
        throws SystemErrorException
    {
        final String mName = "ColumnListListeners::notifyListenersOfColInsertion()";
        ExternalColumnListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalColumnListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalColumnListListener.");
            }

            el = (ExternalColumnListListener)o;

            el.colInsertion(this.db, colID);
        }

        return;

    } /* ColumnListListeners::notifyListenersOfColInsertion() */


    /*************************************************************************/
    /*********************** Listener Management: ****************************/
    /*************************************************************************/

    /**
     * deregisterExternalListener()
     *
     * Deregister an external listener.
     *
     *                                              JRM -- 2/11/08
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
     *                                              JRM -- 2/11/08
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
     *                                              JRM -- 2/11/08
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
     *                                              JRM -- 2/11/08
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
