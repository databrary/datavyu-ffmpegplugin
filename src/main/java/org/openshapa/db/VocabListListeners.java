/*
 * VocabListListeners.java
 *
 * Created on February 7, 2008, 1:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;

/**
 * Class VocabListListeners
 *
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for changes in the vocab
 * list, and sending notifications.
 *
 *                                               -- 2/2/08
 */
public class VocabListListeners extends Listeners
{
    /** The VocabList with which VocabListListeners is associated with. */
    private VocabList itsVL = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * VocabListListeners
     *
     * For now at least, only one constructor.  The Vocab List listeners
     * class is very simple, so all the construtor does is call the super and
     * then set itsVL.
     *
     *                                               -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    public VocabListListeners(Database db,
                              VocabList vl)
        throws SystemErrorException
    {
        super(db);

        final String mName = "VLChangeListeners::VLChangeListeners()";

        if ( vl == null )
        {
            throw new SystemErrorException(mName + "vl null on entry.");
        }

        this.itsVL = vl;

        return;

    } /* VocabListListeners::VocabListListeners(db, vl) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /*** none ***/


    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/

    /**
     * notifyListenersOfVEDeletion()
     *
     * Advise the listeners of the deletion of a VocabElement of the specified
     * id from the vocab list.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfVEDeletion(long VEID)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::notifyListenersOfVEDeletion()";
        ExternalVocabListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalVocabListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalVLChangeListener.");
            }

            el = (ExternalVocabListListener)o;

            el.VLDeletion(this.db, VEID);
        }

        return;

    } /* VocabListListeners::notifyListenersOfVEDeletion() */


    /**
     * notifyListenersOfInsertion()
     *
     * Advise the listeners of the inserion of a VocabElement of the specified
     * id into the vocab list.
     *
     *                                                   -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfVEInsertion(long VEID)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::notifyListenersOfInsertion()";
        ExternalVocabListListener el;

        // No internal listeners for now.


        // Notify the external listeners.
        for ( Object o : this.els )
        {
            if ( ! ( o instanceof ExternalVocabListListener ) )
            {
                throw new SystemErrorException(mName +
                        ": o not a ExternalVLChangeListener.");
            }

            el = (ExternalVocabListListener)o;

            el.VLInsertion(this.db, VEID);
        }

        return;

    } /* VocabListListeners::notifyListenersOfInsertion() */


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

    protected void deregisterExternalListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "VEChangeListener::deregisterExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        this.DeleteExternalListener(el);

        return;

    } /* VocabListListeners::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * Deregister an internal listener.  Internal listeners not implemented
     * at present, so just throw a SystemErrorException.
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
        final String mName = "VEChangeListener::deregisterInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");

        // return; /* commented out to keep the compiler happy */

    } /* VocabListListeners::deregisterInternalListener() */


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

    protected void registerExternalListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::registerExternalListener()";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }

        if ( ! ( el instanceof ExternalVocabListListener ) )
        {
            throw new SystemErrorException(mName +
                    ": el not an ExternalVLChangeListener.");
        }

        this.AddExternalListener(el);

        return;

    } /* VocabListListeners::registerExternalListener() */


    /**
     * registerInternalListener()
     *
     * Register an internal listener.  Internal listeners not implemented
     * at present, so just throw a SystemErrorException.
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
        final String mName = "VLChangeListeners::registerInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");

        // return; /* commented out to keep the compiler happy */

    } /* VocabListListeners::registerExternalListener() */
} /* class VocabListListeners */
