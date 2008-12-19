/*
 * Listeners.java
 *
 * Created on February 2, 2008, 4:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class Listeners
 *
 * Abstract class supporting the maintenance of lists of internal and
 * external listeners.  Lists are maintained as sets, so as to avoid
 * duplicates.
 *
 * The class and/or its subclasses will generate system errors on:
 *
 *      duplicate insertions,
 *
 *      insertion of internal targets that do not appear in the db index,
 *
 *      deletion of non-existant entries, and
 *
 *      attempts to callback internal targets that don't exist.
 *
 * On external callbacks to targets that have been descarded without
 * de-registering, I fear we will find out the hard way.  The target will
 * still exist according to Java since we will be maintaining a reference
 * to it, and thus the callback will look OK from this end.  However, I
 * suspect that the results will be undesireable.
 *
 *                                              JRM -- 2/2/08
 *
 * @author mainzer
 */
public class Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * db:  Reference to the host database.
     *
     * ils: Internal Listener Set.  Reference to the set of Long in which the
     *      IDs internal listeners are stored.
     *
     * els: External Listener Set.  Reference to the set of references to
     *      object in which the references to external listeners are stored.
     */

    Database db = null;

    java.util.HashSet<Long> ils = new java.util.HashSet<Long>();

    java.util.HashSet<Object> els = new java.util.HashSet<Object>();


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Listeners()
     *
     * Two constructors for the Listeners class -- the initial constructor
     * which only takes a reference to a Database as its parameter, and a
     * copy constructor which takes another instance of Listeners as its
     * parameter, and returns a duplicate instance.
     *
     *                                              JRM -- 2/2/08
     *
     * Changes:
     *
     *      None.
     */

    public Listeners(Database db)
        throws SystemErrorException
    {
        final String mName = "Listeners::Listeners(db)";

        if ( db == null )
        {
            throw new SystemErrorException(mName + ": db null on entry.");
        }

        this.db = db;

    } /* Listeners::Listeners(db) */

    public Listeners(Listeners base)
        throws SystemErrorException
    {
        final String mName = "Listeners::Listeners(base)";

        if ( base == null )
        {
            throw new SystemErrorException(mName + ": base null on entry.");
        }

        if ( base.db == null )
        {
            throw new SystemErrorException(mName + ": base.db null on entry.");
        }

        this.db = base.db;
        this.ils.addAll(base.ils);
        this.els.addAll(base.els);

    } /* Listeners::Listeners(base) */


    /*************************************************************************/
    /*************************** els management: *****************************/
    /*************************************************************************/

    /**
     * AddExternalListener()
     *
     * Add the reference to an external listener to els.  Verify that the
     * supplied reference is not null, and that it is not in the els -- throw
     * a system error if either test fails.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void AddExternalListener(Object el)
        throws SystemErrorException
    {
        final String mName = "Listeners::AddExternalListener(el)";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null.");
        }

        if ( this.els.contains(el) )
        {
            throw new SystemErrorException(mName + ": el already in els.");
        }

        this.els.add(el);

        return;

    } /* Listeners::AddExternalListener(ID) */

    /**
     * DeleteInternalListener()
     *
     * Delete the reference to an internal listener from els.  Verify that the
     * supplied reference exists in the els -- throw a system error if not.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void DeleteExternalListener(Object el)
        throws SystemErrorException
    {
        final String mName = "Listeners::DeleteExternalListener(el)";

        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null.");
        }

        if ( ! this.els.contains(el) )
        {
            throw new SystemErrorException(mName + ": el not in els.");
        }

        this.els.remove(el);

        return;

    } /* Listeners::DeleteExternalListener(el) */


    /*************************************************************************/
    /*************************** ils management: *****************************/
    /*************************************************************************/

    /**
     * AddInternalListener()
     *
     * Add the ID of an internal listener to ils.  Verify that the supplied
     * ID exists in the database index, and that it does not exist in the
     * ils -- throw a system error if either test fails.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void AddInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "Listeners::AddInternalListener(ID)";

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid.");
        }

        if ( ! db.idx.inIndex(ID) )
        {
            throw new SystemErrorException(mName + ": ID not in index.");
        }

        if ( this.ils.contains(ID) )
        {
            throw new SystemErrorException(mName + ": ID already in ils.");
        }

        this.ils.add(ID);

        return;

    } /* Listeners::AddInternalListener(ID) */

    /**
     * DeleteInternalListener()
     *
     * Delete the ID of an internal listener from ils.  Verify that the
     * supplied ID exists in the ils -- throw a system error if not.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void DeleteInternalListener(long ID)
        throws SystemErrorException
    {
        final String mName = "Listeners::DeleteInternalListener(ID)";

        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid.");
        }

        if ( ! this.ils.contains(ID) )
        {
            throw new SystemErrorException(mName + ": ID not in ils.");
        }

        this.ils.remove(ID);

        return;

    } /* Listeners::DeleteInternalListener(ID) */

} /* class Listeners */
