package org.openshapa.models.db;

/**
 * A VocabListListeners is used to manage the mechanics registering and
 * de-registering internal and external listeners for changes in the vocab
 * list, and sending notifications.
 *
 * @date 2008/02/02
 */
public class VocabListListeners extends Listeners
{
    /** The VocabList with which VocabListListeners is associated with. */
    private VocabList itsVL = null;

    /**
     * Constructor
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param db The database that this VocabListListener is listening too.
     * @param vl The VocabList that this VocabListListener is listening too for
     * changes.
     *
     * @date 2008/06/02
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

    }

    /**
     * Advises the listeners of the deletion of a VocabElement of the specified
     * id from the vocab list.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param VEID The ID of the vocab element that we are notifying listeners
     * of its removal.
     *
     * @throws SystemErrorException If unable to notify all listeners of the
     * vocab element removal.
     *
     * @date 2008/02/02
     */
    protected void notifyListenersOfVEDeletion(long VEID)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::notifyListenersOfVEDeletion()";
        ExternalVocabListListener el;

        // The database has been modified!
        db.markAsChanged();

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

    }

    /**
     * Advises the listeners of the inserion of a VocabElement of the specified
     * id into the vocab list.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param VEID The ID of the vocab element that we are notifying listeners
     * of insertion.
     *
     * @throws SystemErrorException If unable to notify all listeners of the
     * vocab element insertion.
     *
     * @date 2008/02/02
     */
    protected void notifyListenersOfVEInsertion(long VEID)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::notifyListenersOfInsertion()";
        ExternalVocabListListener el;

        // The database has been modified!
        db.markAsChanged();

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

    }

    /**
     * Deregister an external listener.
     *
     * @param el The external listener to deregister from the list of listeners.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @throws SystemErrorException If unable to deregister the listener.
     *
     * @date 2008/02/02
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

    }

    /**
     * Deregister an internal listener.  Internal listeners not implemented
     * at present, so this just throws a SystemErrorException.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param ID The id of the internal listener.
     *
     * @throws SystemErrorException Always - internal listeners are not
     * implemented
     *
     * @date 2008/02/02
     */
    protected void deregisterInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "VEChangeListener::deregisterInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");
    }

    /**
     * Register an external listener.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param el The external listener to register with the VocabListListeners.
     *
     * @throws SystemErrorException If unable to register external listener.
     *
     * @date 2008/02/02
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

    }

    /**
     * Register an internal listener.  Internal listeners not implemented
     * at present, so just throw a SystemErrorException.
     *
     * @param ID The id of the internal listener to register.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @date 2008/02/02
     */
    protected void registerInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "VLChangeListeners::registerInternalListener()";

        throw new SystemErrorException(mName +
                ": Internal listeners not supported.");
    }
}
