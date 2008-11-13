/*
 * CascadeListeners.java
 *
 * Created on February 11, 2008, 10:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class CascadeListeners
 * 
 * Instances of this class are used to manage the mechanics registering and
 * de-registering internal and external listeners for notification of the 
 * beginning and end of cascades of changes through the database. 
 * 
 *                                              JRM -- 2/11/08 
 * 
 * @author mainzer
 */
public class CascadeListeners extends Listeners
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     * refCount:  Integer field used to deal with nested calls to the 
     *      notify methods.  
     * 
     *      The start cascade messages are sent iff refCount is zero on 
     *      entry to notifyListenersOfCascadeBegin().  In any case, this 
     *      method increments refcount.
     *
     *      Similarly, the end cascade messages are sent iff refCOunt is one
     *      on entry to notifyListenersOfCascadeEnd().  In any case, this
     *      method decrements refCount.
     */

    int refCount = 0;
    
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /**
     * CascadeListeners
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

    public CascadeListeners(Database db)
        throws SystemErrorException
    {
        super(db);
        
        return;
            
    } /* CascadeListeners::CascadeListeners(db) */
     
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /*** none ***/
    
    
    /*************************************************************************/
    /************************** Change Logging: ******************************/
    /*************************************************************************/
    
    /** 
     * notifyListenersOfCascadeBegin()
     *
     * If this.refCount is 0 on entry, Advise the listeners of the beginning 
     * of a cascade of changes.  Notify external listeners first, and then the 
     * internal listeners.  In any case, increment this.refCount.
     *
     *                                                  JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void notifyListenersOfCascadeBegin()
        throws SystemErrorException
    {
        final String mName = 
                "CascadeBoundryListeners::notifyListenersOfCascadeBegin()";
        DBElement dbe = null;
        ExternalCascadeListener el;
        InternalCascadeListener il;
        
        if ( this.refCount < 0 )
        {
            throw new SystemErrorException(mName + 
                    ": refCount negative on entry.");
        }
        
        this.refCount++;

        if ( this.refCount == 1 )
        {
            // Notify the external listeners first...
            for ( Object o : this.els )
            {
                if ( ! ( o instanceof ExternalCascadeListener ) )
                {
                    throw new SystemErrorException(mName + 
                            ": o not a ExternalCascadeBoundyListener.");
                }

                el = (ExternalCascadeListener)o;

                el.beginCascade(this.db);
            }

            // then notify the internal listeners.
            for ( long id : this.ils )
            {
                dbe = this.db.idx.getElement(id); // throws system error on failure

                if ( ! ( dbe instanceof InternalCascadeListener ) )
                {
                    throw new SystemErrorException(mName + 
                            ": dbe not a InternalCascadeBoundryListener.");
                }
                
                il = (InternalCascadeListener)dbe;
                
                il.beginCascade(this.db);
            }
        }
        
        return;
        
    } /* CascadeListeners::notifyListenersOfCascadeBegin() */
    
    
    /** 
     * notifyListenersOfCascadeEnd()
     *
     * If this.refCount is 1 on entry, Advise the listeners of the end 
     * of a cascade of changes.  Notify internal listeners first, and then the 
     * external listeners.  In any case, decrement this.refCount.
     *
     *                                                  JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void notifyListenersOfCascadeEnd()
        throws SystemErrorException
    {
        final String mName = 
                "CascadeBoundryListeners::notifyListenersOfCascadeEnd()";
        DBElement dbe = null;
        ExternalCascadeListener el;
        InternalCascadeListener il;
        
        if ( this.refCount <= 0 )
        {
            throw new SystemErrorException(mName + 
                    ": refCount non-positive on entry.");
        }
        
        this.refCount--;

        if ( this.refCount == 0 )
        {
            // Notify the internal listeners first...
            for ( Long id : this.ils )
            {
                dbe = this.db.idx.getElement(id); // throws system error on failure

                if ( ! ( dbe instanceof InternalCascadeListener ) )
                {
                    throw new SystemErrorException(mName + 
                            ": dbe not a InternalCascadeBoundryListener.");
                }
                
                il = (InternalCascadeListener)dbe;
                
                il.endCascade(this.db);
            }

            // ...then notify the external listeners
            for ( Object o : this.els )
            {
                if ( ! ( o instanceof ExternalCascadeListener ) )
                {
                    throw new SystemErrorException(mName + 
                            ": o not a ExternalCascadeBoundyListener.");
                }

                el = (ExternalCascadeListener)o;

                el.beginCascade(this.db);
            }
        }
        
        return;
        
    } /* CascadeListeners::notifyListenersOfCascadeEnd() */
    
    
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
    
    protected void deregisterExternalListener(ExternalCascadeListener el)
        throws SystemErrorException
    {
        final String mName = 
                "CascadeBoundryListeners::deregisterExternalListener()";
        
        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }
        
        this.DeleteExternalListener(el);
        
        return;
        
    } /* CascadeListeners::deregisterExternalListener() */
    
    
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
        final String mName = 
                "CascadeBoundryListeners::deregisterInternalListener()";
        
        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }
        
        this.DeleteInternalListener(ID);
        
        return;
        
    } /* CascadeListeners::deregisterInternalListener() */
    
    
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
    
    protected void registerExternalListener(ExternalCascadeListener el)
        throws SystemErrorException
    {
        final String mName = 
                "CascadeBoundryListeners::registerExternalListener()";
        
        if ( el == null )
        {
            throw new SystemErrorException(mName + ": el is null on entry.");
        }
        
        this.AddExternalListener(el);
        
        return;
        
    } /* CascadeListeners::registerExternalListener() */
    
    
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
        final String mName = 
                "CascadeBoundryListeners::registerInternalListener()";
        DBElement dbe = null;
        
        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + ": ID is invalid on entry.");
        }
        
        dbe = this.db.idx.getElement(ID); // throws system error on failure
        
        if ( ! ( dbe instanceof InternalCascadeListener ) )
        {
            throw new SystemErrorException(mName + 
                    ": dbe not a InternalCascadeBoundryListener.");
        }
        
        this.AddInternalListener(ID);
        
        return;
        
    } /* CascadeListeners::registerExternalListener() */
        
} // class CascadeListeners
