/*
 * VocabList.java
 *
 * Created on March 25, 2007, 7:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Class VocabList
 *
 * A singleton instance of VocabList is used to maintain the vocabulary list
 * for an OpenSHAPA database.
 *
 * Note that while the VocabList is quite similar to the DBIndex class, it 
 * isn't close enough to be a subclass.
 *
 *                                          JRM -- 4/30/07
 *
 * @author mainzer
 */
public class VocabList 
{
    
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    /**
     *
     * db:  Reference to the instance of Database of which this vocab list
     *      is part.
     *
     * vl:  Hashtable containg references to all instances of VocabElement that
     *      constitute the vocab list.
     *
     * nameMap: Hashmap mapping vocab element names to vocab element ID.
     *      This mapping is used both to allow lookups by vocab element name,
     *      and to determine if a vocab element name is in use.
     *
     * listeners: Instance of VocabListListeners use to maintain lists of 
     *      listeners for VocabList insertions and deletions, and issue
     *      notifications as appropriate.
     */
    
    /** Reference to the Database of which this instance is part */
    protected Database db = null;

    /** Index of all instances of VocabElement in the vocab list */
    protected Hashtable<Long, VocabElement> vl =
             new Hashtable<Long, VocabElement>();
    
    /** map to allow allow lookups of vocab elements by name. */
    protected HashMap<String, Long> nameMap = new HashMap<String, Long>();
          
    /**
     * instance of VocabListListeners used to maintain lists of listeners,
     *  and notify them as appropriate.
     */
    protected VocabListListeners listeners = null;
    
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
     
    /**
     * VocabList()
     *
     * Constructor for the VocabList class.  
     *                                             JRM -- 4/30/07
     *
     * Changes:
     *
     *    - None.
     */
    protected VocabList(Database db)
         throws SystemErrorException
    {
        super();

        final String mName = "VocabList::VocabList(db): ";

        if ( ( db == null ) ||
             ( ! ( db instanceof Database ) ) )
        {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;
        
        this.listeners = new VocabListListeners(db, this);

        return;
        
    } /* VocabList::VocabList(db) */
     
     
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * toString() -- overrride 
     * 
     * Returns a String representation of the contents of the vocab list.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toString() 
    {
        boolean first = true;
        String s;
        VocabElement ve;
        java.util.Enumeration<VocabElement> entries;
        
        s = "((VocabList) (vl_contents: (";
        entries = vl.elements();
        while ( entries.hasMoreElements() )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                s += ", ";
            }
            ve = entries.nextElement();
            s += ve.toString();
        }
        s += ")))";
        
        return s;
        
    } /* VocabList::toDBString() */
    
        
    /*************************************************************************/
    /********************* Listener Manipulation: ****************************/
    /*************************************************************************/
    
    /**
     * deregisterExternalChangeListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the deregister external change listeners message on to  
     * the instance of VocabListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterExternalChangeListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "VocabElement::deregisterExternalChangeListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "this.listeners is null!?!");
        }
        
        this.listeners.deregisterExternalListener(el);
        
        return;
        
    } /* VocabElement::deregisterExternalChangeListener() */
    
    
    /**
     * deregisterInternalChangeListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the deregister internal change listeners message on to  
     * the instance of VocabElementListeners pointed to by this.listeners.
     * 
     * Note that internal listeners are not supported at present, but the 
     * error will be caught at a lower level.
     * 
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void deregisterInternalChangeListener(long id)
        throws SystemErrorException
    {
        final String mName = "VocabElement::deregisterInternalChangeListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + 
                "Attempt to add internal listener to non-cannonical version.");
        }
        
        this.listeners.deregisterInternalListener(id);
        
        return;
        
    } /* VocabElement::deregisterInternalChangeListener() */
    
    
    /**
     * registerExternalListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the register external change listeners message on to the 
     * instance of VocabListListeners pointed to by this.listeners.
     * 
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerExternalListener(ExternalVocabListListener el)
        throws SystemErrorException
    {
        final String mName = "VocabElement::registerExternalChangeListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "listeners == null!?!?");
        }
        
        this.listeners.registerExternalListener(el);
        
        return;
        
    } /* VocabElement::registerExternalListener() */
    
    
    /**
     * registerInternalChangeListener()
     * 
     * If this.listeners is null, thow a system error exception.
     * 
     * Otherwise, pass the register internal change listeners message on to the 
     * instance of VocabElementListeners pointed to by this.listeners.
     * 
     * Note that internal listeners are not supported at present, however the
     * error will be caught at a lower level.
     * 
     *                                          JRM -- 2/5/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    protected void registerInternalChangeListener(long id)
        throws SystemErrorException
    {
        final String mName = "VocabElement::registerInternalChangeListener()";
        
        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName + "this.listeners == null?!?!");
        }
        
        this.listeners.registerInternalListener(id);
        
        return;
        
    } /* VocabElement::addInternalChangeListener() */
    

    /*************************************************************************/
    /****************************** Methods: *********************************/
    /*************************************************************************/
         
    /**
     * addElement()
     * 
     * Insert the vocab element and all the associated formal arguments into 
     * the index, an insert the vocab element in the vocab list.
     * 
     *                                                 JRM -- 4/23/07
     * 
     * Changes:
     * 
     *    - Added code to create and assign an instance of VocabElementListeners
     *      to the newly inserted vocab element.  Also added call to notify
     *      vocab list change listeners of the insertion.  Finally, inserted
     *      calls to mark the start and finish of the potential cascade of 
     *      changes.
     * 
     *                                                  JRM -- 2/5/08
     */
     
    protected void addElement(VocabElement ve)
       throws SystemErrorException
    {
        final String mName = "VocabList::addElement(ve): ";
        VocabElementListeners nl = null;
          
        if ( ( ve == null ) ||
             ( ! ( ve instanceof VocabElement ) ) )
        {
            throw new SystemErrorException(mName + "Bad ve param");
        }
        else if ( ve.getDB() != db )
        {
            throw new SystemErrorException(mName + "fe.getDB() != db");
        }
        else if ( ve.getID() != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "ve.getID() != INVALID_ID");
        }
        else if ( this.vl.containsValue(ve) )
        {
            throw new SystemErrorException(mName + "ve already in vl?!?");
        }
        else if ( this.nameMap.containsKey(ve.name) )
        {
            throw new SystemErrorException(mName + "ve name in use!?!");
        }
        
        this.db.cascadeStart();
        
        this.db.idx.addElement(ve);
        
        this.vl.put(ve.getID(), ve);
        
        addFargListToIndex(ve);
        
        this.nameMap.put(ve.getName(), ve.getID());
        
        ve.propagateID();
        
        nl = new VocabElementListeners(this.db, ve);
        ve.setListeners(nl);
        
        this.listeners.notifyListenersOfVEInsertion(ve.getID());
        
        this.db.cascadeEnd();
          
        return;
       
    } /* VocavList::addElement(ve) */
    
    /**
     * addFargListToIndex()
     *
     * Add the formal argument list of the supplied vocab element to the 
     * database index.
     *                                             JRM -- 4/30/07
     *
     * Changes:
     *
     *    - Modified method to avoid calling getNumFormalArgs() on matrix ve's
     *      that haven't had their type declared yet.
     *
     *                                              JRM -- 6/15/07
     */
    
    private void addFargListToIndex(VocabElement ve)
        throws SystemErrorException
    {
        final String mName = "VocabList::addFargListToIndex(ve): ";
        int i;
        int numFormalArgs;
        FormalArgument farg;
          
        if ( ( ve == null ) ||
             ( ! ( ve instanceof VocabElement ) ) )
        {
            throw new SystemErrorException(mName + "Bad ve param");
        }
        
        if ( ( ve instanceof MatrixVocabElement ) &&
             ( ((MatrixVocabElement)ve).getType() == 
               MatrixVocabElement.matrixType.UNDEFINED ) )
        {
            numFormalArgs = 0;
        }
        else
        {
            numFormalArgs = ve.getNumFormalArgs();
        }
        
        for ( i = 0; i < numFormalArgs; i++ )
        {
            farg = ve.getFormalArg(i);
            
            if ( farg == null )
            {
                throw new SystemErrorException(mName + "farg is null?!?");
            }
            
            db.idx.addElement(farg);
        }
        
    } /* VocabList::addFargListToIndex() */
    
    
    /**
     * getMatricies
     *
     * Construct and return a vector containing copies of all non-system 
     * matricies of MatrixType.MATRIX in the vocab list.  If the vocab list 
     * contains no such matricies, return null.
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - none.
     */
    
    protected java.util.Vector<MatrixVocabElement> getMatricies()
        throws SystemErrorException
    {
        java.util.Vector<MatrixVocabElement> matricies = null;
        VocabElement ve;
        MatrixVocabElement mve;
        java.util.Enumeration<VocabElement> entries;
        
        entries = this.vl.elements();
        while ( entries.hasMoreElements() )
        {
            ve = entries.nextElement();
            
            if ( ve instanceof MatrixVocabElement )
            {
                mve = (MatrixVocabElement)ve;
                
                if ( ( ! mve.getSystem() ) &&
                     ( mve.getType() == MatrixVocabElement.matrixType.MATRIX ) )
                {
                    if ( matricies == null )
                    {
                        matricies = new java.util.Vector<MatrixVocabElement>();
                    }
                    
                    matricies.add(new MatrixVocabElement(mve));
                }
            }
        }
        
        return matricies;
        
    } /* VocabList::getMatricies() */
    
    
    /**
     * getPreds
     *
     * Construct and return a vector containing copies of all non-system 
     * predicates in the vocab list.  If the vocab list contains no non-system
     * predicates, return null.
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */ 
    
    protected java.util.Vector<PredicateVocabElement> getPreds()
        throws SystemErrorException
    {
        java.util.Vector<PredicateVocabElement> preds = null;
        VocabElement ve;
        PredicateVocabElement pve;
        java.util.Enumeration<VocabElement> entries;
        
        entries = this.vl.elements();
        while ( entries.hasMoreElements() )
        {
            ve = entries.nextElement();
            
            if ( ve instanceof PredicateVocabElement )
            {
                pve = (PredicateVocabElement)ve;
                
                if ( ! pve.getSystem() )
                {
                    if ( preds == null )
                    {
                        preds = new java.util.Vector<PredicateVocabElement>();
                    }
                    preds.add(new PredicateVocabElement(pve));
                }
            }
        }
        
        return preds;
        
    } /* VocabList::getPreds() */
    
     
    /**
     * getVocabElement(targetID)
     *
     * Get the instance of VocabElement corresponding with the supplied id.
     *
     *                                                 JRM -- 4/30/07
     *
     * Changes:
     *
     *   - None.
     */
     
    protected VocabElement getVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::getVocabElement(targetID): ";
        VocabElement ve = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
         
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
         
        return ve;
         
    } /* VocabList::getVocabElement(targetID) */
    
    
    /**
     * getVocabElement(targetName)
     *
     * Get the instance of VocabElement corresponding with the supplied name.
     *
     *                                                  JRM -- 6/3/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected VocabElement getVocabElement(String targetName)
        throws SystemErrorException
    {
        final String mName = "VocabList::getVocabElement(targetName): ";
        long targetID;
        VocabElement ve = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( ! nameMap.containsKey(targetName) )
        {
            throw new SystemErrorException(mName + "targetName not in nameMap");
        }

        targetID = this.nameMap.get(targetName);
            
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "targetID == INVALID_ID");
        }
          
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        return ve;
        
    } /* VocabList::getVocabElement(targetName) */
    
    
    /* TODO: Test this method */ 
    /**
     * getMatrixVocabElement(targetID)
     *
     * Get the instance of MatrixVocabElement corresponding with the supplied 
     * id. Throw a system error if no such mve exists.
     *
     *                                                 JRM -- 8/30/07
     *
     * Changes:
     *
     *   - None.
     */
     
    protected MatrixVocabElement getMatrixVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::getMatrixVocabElement(targetID): ";
        VocabElement ve = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
         
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        if ( ! ( ve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "targetID doesn't refer to a MatrixVOcabElement");
        }
         
        return (MatrixVocabElement)ve;
         
    } /* VocabList::getMatrixVocabElement(targetID) */
    
    
    /* TODO: Test this method */ 
    /**
     * getMatrixVocabElement(targetName)
     *
     * Get the instance of PredicateVocabElement corresponding with the 
     * supplied name.  Throw a system error exception.
     *
     *                                                  JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected MatrixVocabElement getMatrixVocabElement(String targetName)
        throws SystemErrorException
    {
        final String mName = "VocabList::getMatrixVocabElement(targetName): ";
        Long targetID;
        VocabElement ve = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( ! nameMap.containsKey(targetName) )
        {
            throw new SystemErrorException(mName + "targetName not in nameMap");
        }

        targetID = this.nameMap.get(targetName);
            
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "targetID == INVALID_ID");
        }
          
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        if ( ! ( ve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "targetName doesn't refer to a MatrixVocabElement");
        }
         
        return (MatrixVocabElement)ve;
                
    } /* VocabList::getMatrixVocabElement(targetName) */
    
    
    /* TODO: Test this method */ 
    /**
     * getPredicateVocabElement(targetID)
     *
     * Get the instance of PredVocabElement corresponding with the supplied 
     * id. Throw a system error if no such pve exists.
     *
     *                                                 JRM -- 8/30/07
     *
     * Changes:
     *
     *   - None.
     */
     
    protected PredicateVocabElement getPredicateVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::getPredicateVocabElement(targetID): ";
        VocabElement ve = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
         
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        if ( ! ( ve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "targetID doesn't refer to a MatrixVOcabElement");
        }
         
        return (PredicateVocabElement)ve;
         
    } /* VocabList::getPredicateVocabElement(targetID) */
    
    
    /* TODO: Test this method */ 
    /**
     * getPredicateVocabElement(targetName)
     *
     * Get the instance of PredicateVocabElement corresponding with the 
     * supplied name.  Throw a system error if there is no such element.
     *
     *                                                  JRM -- 6/3/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected PredicateVocabElement getPredicateVocabElement(String targetName)
        throws SystemErrorException
    {
        final String mName = "VocabList::getPredicateVocabElement(targetName): ";
        Long targetID;
        VocabElement ve = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( ! nameMap.containsKey(targetName) )
        {
            throw new SystemErrorException(mName + "targetName not in nameMap");
        }

        targetID = this.nameMap.get(targetName);
            
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "targetID == INVALID_ID");
        }
          
        ve = vl.get(targetID);
         
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
        
        if ( ! ( ve instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "targetName doesn't refer to a PredicateVocabElement");
        }
         
        return (PredicateVocabElement)ve;
                
    } /* VocabList::getPredicateVocabElement(targetName) */
    
    
    /**
     * inVocabList(targetID)
     *
     * Return true if the vocab list contains an entry matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean inVocabList(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::inVocabList(targetID): ";
        boolean inVL = false;
        
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.vl.containsKey(targetID) )
        {
            inVL = true;
        }
        
        return inVL;
        
    } /* VocabList::inVocabList(targetID) */
    
    
    /**
     * inVocabList()
     *
     * Return true if the supplied predicate of variable name currently appears
     * in the vocab list, and false otherwise.
     *
     *                                              JRM -- 6/3/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean inVocabList(String targetName)
        throws SystemErrorException
    {
        final String mName = "VocabList::inVocabList(targetName): ";
        boolean inUse = false;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( nameMap.containsKey(targetName) )
        {
            inUse = true;
        }
        
        return inUse;
        
    } /* VocabList::inVocabList(targetName) */
    
    
    /**
     * matrixInVocabList(targetID)
     *
     * Return true if the vocab list contains a matrix vocab entry matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean matrixInVocabList(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::matrixInVocabList(targetID): ";
        boolean inVL = false;
        VocabElement ve = null;
                
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.vl.containsKey(targetID) )
        {
            ve = this.vl.get(targetID);
            
            if ( ve == null )
            {
                throw new SystemErrorException(mName + "ve == null");
            }
            else if ( ve instanceof MatrixVocabElement )
            {
                inVL = true;
            }
        }
        
        return inVL;
        
    } /* VocabList::matrixInVocabList(targetID) */
    
    
    /**
     * matrixInVocabList(targetName)
     *
     * Return true if the vocab list contains a matrix vocab entry matching the 
     * provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean matrixInVocabList(String targetName)
       throws SystemErrorException
    {
        final String mName = "VocabList::matrixInVocabList(targetName): ";
        boolean inVL = false;
        long targetID;
        VocabElement ve = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( this.nameMap.containsKey(targetName) )
        {
            targetID = this.nameMap.get(targetName);

            if ( targetID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "targetID == INVALID_ID");
            }

            ve = vl.get(targetID);

            if ( ve == null )
            {
                throw new SystemErrorException(mName + "ve == null");
            }
            else if ( ve instanceof MatrixVocabElement )
            {
                inVL = true;
            }
        }
        
        return inVL;
        
    } /* VocabList::matrixInVocabList(targetName) */
    
    
    /**
     * predInVocabList(targetID)
     *
     * Return true if the vocab list contains a predicate vocab entry matching  
     * the provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean predInVocabList(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::predInVocabList(targetID): ";
        boolean inVL = false;
        VocabElement ve = null;
                
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( this.vl.containsKey(targetID) )
        {
            ve = this.vl.get(targetID);
            
            if ( ve == null )
            {
                throw new SystemErrorException(mName + "ve is null");
            }
            else if ( ve instanceof PredicateVocabElement )
            {
                inVL = true;
            }
        }
        
        return inVL;
        
    } /* VocabList::predInVocabList(targetID) */
    
    
    /**
     * predInVocabList(targetName)
     *
     * Return true if the vocab list contains a predicate vocab entry matching  
     * the provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    protected boolean predInVocabList(String targetName)
       throws SystemErrorException
    {
        final String mName = "VocabList::predInVocabList(targetName): ";
        boolean inVL = false;
        long targetID;
        VocabElement ve = null;
        
        if ( targetName == null )
        {
            throw new SystemErrorException(mName + "targetName == null");
        }
        else if ( targetName.length() == 0 )
        {
            throw new SystemErrorException(mName + "targetName is empty");
        }
        else if ( ( ! Database.IsValidPredName(targetName) ) &&
                  ( ! Database.IsValidSVarName(targetName) ) )
        {
            throw new SystemErrorException(mName + "targetName invalid");
        }
        else if ( this.nameMap.containsKey(targetName) )
        {
            targetID = this.nameMap.get(targetName);

            if ( targetID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "targetID == INVALID_ID");
            }

            ve = vl.get(targetID);

            if ( ve == null )
            {
                throw new SystemErrorException(mName + "ve == null");
            }
            else if ( ve instanceof PredicateVocabElement ) 
            {
                inVL = true;
            }
        }
        
        return inVL;
        
    } /* VocabList::predInVocabList(targetName) */
    
     
    /**
     * removeVocabElement()
     * 
     * Remove the instance of VocabElement with the specified id from the 
     * vocab list.  Similarly, remove the VocabElement and all its associated
     * formal parameters from the index.
     * 
     *                                                 JRM -- 4/30/07
     * 
     * Changes:
     * 
     *    - Added code to notify listeners of deletion, and to remove the 
     *      instance of VocabElementListeners from the target VocabElement
     *      before the actual deletion.  Also added code to send a vocab 
     *      element deleted message to any vocab list change listeners.
     *      Finally, added calls to mark the beginning and end of any
     *      resulting cascade of changes.
     * 
     *                                                  JRM -- 2/5/08
     */
     
    protected void removeVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::removeVocabElement(targetID): ";
        int i;
        long id;
        FormalArgument fArg;
        VocabElement ve = null;
          
        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( ! vl.containsKey(targetID) )
        {
            throw new SystemErrorException(mName + 
                                           "targetID not in vocab list.");
        }
        
        /* verify that all formal arguments are in the index before we
         * start the removeal so that we don't have to quit half way
         * through the removal.  Also verify that the ve name is in the 
         * nameMap.
         */
        ve = this.vl.get(targetID);
        if ( ve == null )
        {
            throw new SystemErrorException(mName + "vl.get(targetID == null??");
        }
        else
        {
            for ( i = 0; i < ve.getNumFormalArgs(); i++ )
            {
                fArg = ve.getFormalArg(i);
                id = fArg.getID();
                
                if ( id == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + "Invalid fArg id");
                }
                else if ( ! this.db.idx.inIndex(id) )
                {
                    throw new SystemErrorException(mName + "fArg id not in idx");
                }
                else if ( fArg != this.db.idx.getElement(id) )
                {
                    throw new SystemErrorException(mName + "fArg not in idx");
                }
            }
            
            if ( ! this.nameMap.containsKey(ve.getName()) )
            {
                throw new SystemErrorException(mName + 
                                               "ve.name not in name map.");
            }
            else if ( this.nameMap.get(ve.getName()) != ve.getID() )
            {
                throw new SystemErrorException(mName +
                        "ve.name not mapped to ve.id in name map.");
            }
        }
        
        this.db.cascadeStart();
        
        ve.notifyListenersOfDeletion();
        ve.setListeners(null);
        
        this.listeners.notifyListenersOfVEDeletion(ve.getID());
        
        if ( (ve = vl.remove(targetID)) == null )
        {
            throw new SystemErrorException(mName + "vl.remove() failed.");
        }
        
        this.db.idx.removeElement(targetID);
        
        for ( i = 0; i < ve.getNumFormalArgs(); i++ )
        {
            this.db.idx.removeElement(ve.getFormalArg(i).getID());
        }
        
        this.nameMap.remove(ve.getName());
        
        this.db.cascadeEnd();
         
        return;
     
    } /* VocabList::removeVocabElement(targetID) */
    
     
    /**
     * replaceVocabElement()
     * 
     * Search the index for an instance of DBElement with the same id as that 
     * of the supplied instance.  
     * 
     * Scan the foraml argument lists of the two VocabElements and remove from 
     * the index all deleted formal arguments, add all new formal arguments, 
     * and replace all the remaining arguments with their new representation.
     * 
     * Replace the old representation of the VocabElement with the new 
     * representation in the vocab list and in the index.
     * 
     * N.B. This method must be used to replace a vocab element with a modified
     *      version of itself only.  If you play with IDs and try to use it to 
     *      replace one VocabElement with another, it will choke on the 
     *      pre-existing formal arguments.
     * 
     *      Similarly, don't try to recycle the IDs of formal arguments if you 
     *      change the type.  Just leave the ID set it INVALID_ID.
     * 
     *                                                 JRM -- 5/1/07
     * 
     * Changes:
     *
     *    - Added code to transfer the listeners from the old incarnation of
     *      the vocab element to the new, and to notify listeners of the 
     *      changes.  Also added calls to mark the beginning and end of any
     *      resulting cascade of changes.
     *                                                  JRM -- 2/5/08
     */

    protected void replaceVocabElement(VocabElement ve)
       throws SystemErrorException
    {
        final String mName = "VocabList::replaceVocabElement(dbe): ";
        boolean matchFound = false;
        int i;
        int j;
        FormalArgument match = null;
        FormalArgument farg = null;
        FormalArgument newFarg = null;
        FormalArgument oldFarg = null;
        VocabElement old_ve = null;
           
        if ( ( ve == null ) ||
             ( ! ( ve instanceof VocabElement ) ) )
        {
            throw new SystemErrorException(mName + "Bad ve param");
        }
        else if ( ve.getDB() != this.db )
        {
            throw new SystemErrorException(mName + "ve.getDB() != this.db");
        }
        else if ( ve.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "ve.getID() == INVALID_ID");
        }
        else if ( ve.getNumFormalArgs() < 0 )
        {
            throw new SystemErrorException(mName + 
                                           "ve.getNumFormalArgs() < 0");
        }
         
        old_ve = this.vl.get(ve.getID());
         
        if ( old_ve == null )
        {
            throw new SystemErrorException(mName +
                    "can't replace -- not in vocab list.");
        }
        else if ( ve.getClass() != old_ve.getClass() )
        {
            throw new SystemErrorException(mName + "type mis-match.");
        }
        else if ( old_ve.getNumFormalArgs() <= 0 )
        {
            throw new SystemErrorException(mName +
                                           "old_ve.getNumFormalArgs() <= 0");
        }
        
        /* before we actually do anything, scan the old and new formal argument
         * lists, and verify that there are no type changes or duplicate IDs.
         */
        for ( i = 0; i < ve.getNumFormalArgs(); i++ )
        {
            matchFound = false;
            newFarg = ve.getFormalArg(i);
            
            if ( newFarg == null )
            {
                throw new SystemErrorException(mName + "newFarg is null(1)?!?");
            }

            if ( newFarg.getID() != DBIndex.INVALID_ID )
            {
                for ( j = 0; j < old_ve.getNumFormalArgs(); j++ )
                {
                    oldFarg = old_ve.getFormalArg(j);

                    if ( oldFarg == null )
                    {
                        throw new SystemErrorException(mName + 
                                "oldFarg is null(1)?!?");
                    }
                    else if ( newFarg.getID() == oldFarg.getID() )
                    {
                        if ( newFarg.getClass() != oldFarg.getClass() )
                        {
                            throw new SystemErrorException(mName +
                                    "new/old farg type mismatch?!?");
                        }
                        if ( matchFound )
                        {
                            throw new SystemErrorException(mName +
                                    "found multiple matches(1)?!?");
                        }
                        else
                        {
                            matchFound = true;
                            match = oldFarg;
                        }
                    }
                }
                
                if ( ! matchFound )
                {
                    throw new SystemErrorException(mName +
                            "no match found for pre-existing farg(1)?!?");
                }
                
                for ( j = i + 1; j < ve.getNumFormalArgs(); j++ )
                {
                    farg = ve.getFormalArg(j);
                    
                    if ( farg == null )
                    {
                        throw new SystemErrorException(mName + 
                                                       "farg is null?!?");
                    }
                    else if ( newFarg.getID() == farg.getID() )
                    {
                        throw new SystemErrorException(mName + 
                                                    "dup id in new farg list");
                    }
                }
            }
        }
        
        /* Verify that the name of the old_ve is mapped to the ID of the 
         * old_ve in the name map.  If new_ve.name != old_ve.name, verify that
         * ve.name is valid and that it is not in use.
         */
        if ( ( ! this.nameMap.containsKey(old_ve.getName()) ) ||
             ( this.nameMap.get(old_ve.name) != old_ve.getID() ) )
        {
            throw new SystemErrorException(mName + 
                    "old_ve.name not mapped to old_ve.id in name map.");
        }
        
        if ( old_ve.getName().compareTo(ve.getName()) != 0 )
        {
            if ( ve.getName().length() == 0 )
            {
                throw new SystemErrorException(mName + "Modified name is empty");
            }
            
            if ( this.nameMap.containsKey(ve.getName()) )
            {
                throw new SystemErrorException(mName + 
                                               "Modified name already in use");
            }
            
            if ( ve instanceof PredicateVocabElement )
            {
                if ( ! Database.IsValidPredName(ve.getName()) )
                {
                    throw new SystemErrorException(mName + 
                                               "Modified pred name invalid");
                }
            }
            else if ( ve instanceof MatrixVocabElement )
            {
                if ( ! Database.IsValidSVarName(ve.getName()) )
                {
                    throw new SystemErrorException(mName + 
                                               "Modified svar name invalid");
                }
            }
            else
            {
                throw new SystemErrorException(mName + 
                        "Unknown vocab element type?!?!");
            }
        }
        
        this.db.cascadeStart();
        
        /* replace the old farg list with the new in the index */
        for ( i = 0; i < ve.getNumFormalArgs(); i++ )
        {
            matchFound = false;
            newFarg = ve.getFormalArg(i);
            
            if ( newFarg == null )
            {
                throw new SystemErrorException(mName + "newFarg is null(2)?!?");
            }
            
            if ( newFarg.getID() == DBIndex.INVALID_ID )
            {
                /* it is a completely new formal argument -- just insert 
                 * it in the index without attempting to find the instance 
                 * of FormalArgument it is replacing.
                 */
                this.db.idx.addElement(newFarg);
            }
            else
            {
                for ( j = 0; j < old_ve.getNumFormalArgs(); j++ )
                {
                    oldFarg = old_ve.getFormalArg(j);

                    if ( oldFarg == null )
                    {
                        throw new SystemErrorException(mName + 
                                "oldFarg is null(2)?!?");
                    }
                    else if ( newFarg.getID() == oldFarg.getID() )
                    {
                        if ( matchFound )
                        {
                            throw new SystemErrorException(mName +
                                    "found multiple matches(2)?!?");
                        }
                        else
                        {
                            matchFound = true;
                            match = oldFarg;
                        }
                    }
                }

                if ( matchFound )
                {
                    if ( newFarg.getClass() == match.getClass() )
                    {
                        this.db.idx.replaceElement(newFarg);
                    }
                    else /* type of formal argument has changed */
                    {
                        /* When the type of a formal argument changed,
                         * it should be replaced with a new instance of 
                         * the appropriate class of formal argument, and 
                         * the ID should be left as INVALID_ID.
                         */
                        throw new SystemErrorException(mName +
                                    "Type of formal argument has changed.");
                    }
                }
                else
                {
                    throw new SystemErrorException(mName + 
                            "No match found for a pre-existing farg(2).");
                }
            }
        }
        
        /* it is possible that formal arguments have been removed from the 
         * formal argument list.  Thus we must scan the old vocab element's 
         * formal argument list, and remove from the index all formal arguments
         * that don't have matches in the new vocab elements formal argument 
         * list.
         */
        for ( j = 0; j < old_ve.getNumFormalArgs(); j++ )
        {
            matchFound = false;

            oldFarg = old_ve.getFormalArg(j);
            
            if ( oldFarg == null )
            {
                throw new SystemErrorException(mName + "oldFarg is null(3)?!?");
            }
            else if ( oldFarg.getID() == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                        "oldFarg doesn't have an ID?!?");
            }
            
            for ( i = 0; i < ve.getNumFormalArgs(); i++ )
            {
                newFarg = ve.getFormalArg(i);

                if ( newFarg == null )
                {
                    throw new SystemErrorException(mName + 
                            "newFarg is null(3)?!?");
                }
                else if ( newFarg.getID() == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + 
                            "newFarg doesn't have an ID?!?");
                }
                
                if ( oldFarg.getID() == newFarg.getID() )
                {
                    if ( matchFound )
                    {
                        throw new SystemErrorException(mName +
                                "found multiple matches(3)?!?");
                    }
                    else
                    {
                        matchFound = true;
                    }
                }
            }
            
            /* if no match was found, just delete the formal argument from the 
             * index, as it has been deleted from the vocab element. 
             */
            if ( ! matchFound )
            {
                this.db.idx.removeElement(oldFarg.getID());
            }
        }
        
        
        /* Move the listeners from the old incarnation to the new */
        
        ve.setListeners(old_ve.getListeners());
        old_ve.setListeners(null);
        
        /* Replace the old vocab element with the new in the 
         * vocab list.  Similarly, replace the old_ve in the index.  If the
         * name has changed, replace the old name with the new in the name 
         * index.
         */

        if ( this.vl.put(ve.getID(), ve) != old_ve )
        {
            throw new SystemErrorException(mName + "replace failed.");
        }
        
        this.db.idx.replaceElement(ve);
        
        if ( old_ve.getName().compareTo(ve.getName()) != 0 )
        {
            this.nameMap.remove(old_ve.getName());
            
            if ( this.nameMap.put(ve.getName(), ve.getID()) != null )
            {
                throw new SystemErrorException(mName + 
                        "Unexpected return from this.nameMap.put().");
            }
        }
        
        /* Note changes between the old and new incarnations of the 
         * vocab element, and notify the listeners.
         */
        ve.noteChange(old_ve, ve);
        ve.notifyListenersOfChange();
        
        this.db.cascadeEnd();
        
        return;
         
    } /* VocabList::replaceVocabElement(ve) */
    
    /**
     * toDBString()
     * 
     * Returns a String representation of the contents of the vocabList.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toDBString() 
    {
        boolean first = true;
        String s;
        VocabElement ve;
        java.util.Enumeration<VocabElement> entries;
        
        try
        {
            s = "((VocabList) (vl_size: ";
            s += this.vl.size();
            s += ") (vl_contents: (";
            entries = this.vl.elements();
            while ( entries.hasMoreElements() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    s += ", ";
                }
                ve = entries.nextElement();
                s += ve.toDBString();
            }
            s += ")))";
        }
        
        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
       
        return s;
        
    } /* VocabList::toDBString() */
    
    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * ConstructTestMatrix()
     *
     * Construct a matrix vocab element with the supplied type and formal
     * arguments (if any).  Don't bother to catch system errors -- that will
     * be done at a higher level.
     *                                          JRM - 5/10/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static MatrixVocabElement 
            ConstructTestMatrix(Database db,
                                String name,
                                MatrixVocabElement.matrixType type,
                                FormalArgument arg0,
                                FormalArgument arg1,
                                FormalArgument arg2,
                                FormalArgument arg3)
       throws SystemErrorException
    {
        MatrixVocabElement matrix = null;
        
        matrix = new MatrixVocabElement(db, name);
        
        matrix.setType(type);
        
        if ( arg0 != null )
        {
            matrix.appendFormalArg(arg0);
        }

        if ( arg1 != null )
        {
            matrix.appendFormalArg(arg1);
        }
        
        if ( arg2 != null )
        {
            matrix.appendFormalArg(arg2);
        }
        
        if ( arg3 != null )
        {
            matrix.appendFormalArg(arg3);
        }
        
        return matrix;
        
    } /* VocabList::ConstructTestMatrix() */
    
    /**
     * ConstructTestPred()
     *
     * Construct a predicate vocab element with the supplied formal arguments
     * (if any).  Don't bother to catch system errors -- that will be done
     * at a higher level.
     *                                          JRM - 5/10/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static PredicateVocabElement ConstructTestPred(Database db,
                                                          String name,
                                                          FormalArgument arg0,
                                                          FormalArgument arg1,
                                                          FormalArgument arg2,
                                                          FormalArgument arg3)
       throws SystemErrorException
    {
        PredicateVocabElement pred = null;
        
        pred = new PredicateVocabElement(db, name);
        
        if ( arg0 != null )
        {
            pred.appendFormalArg(arg0);
        }
        
        if ( arg1 != null )
        {
            pred.appendFormalArg(arg1);
        }
        
        if ( arg2 != null )
        {
            pred.appendFormalArg(arg2);
        }
        
        if ( arg3 != null )
        {
            pred.appendFormalArg(arg3);
        }
        
        return pred;
        
    } /* VocabList::ConstructTestPred() */
    
    
    /**
     * Test1ArgConstructor()
     * 
     * Run a battery of tests on the one argument constructor for this 
     * class, and on the instance returned.
     *
     *                                              JRM - 5/8/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class VocabList               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        VocabList vl = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            db = null;
            vl = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                vl = new VocabList(db);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( db == null ) ||
                 ( vl == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("One or more class allocations " +
                                        "failed to complete.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( vl == null )
                    {
                        outStream.print(
                                "new VocabList(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new VocabList(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {            
            if ( vl.db != db )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.print("Unexpected initial vl.db != db.\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ( vl.vl == null ) ||
                 ( ! vl.vl.isEmpty() ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("vl null or non-empty on creation.\n");
                }
            }
        }
        
        
        /* Verify that the constructor fails when passed an invalid db. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            vl = null;
            systemErrorExceptionString = null;
                    
            try
            {
                vl = new VocabList((Database)null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( vl != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new VocabList(null) returned.\n");
                    }
                    
                    if ( vl != null )
                    {
                        outStream.print(
                             "new VocabList(null) returned non-null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("new VocabList(null) failed to " +
                                "throw system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DBIndex::Test1ArgConstructor() */
    
   
    /**
     * TestClassVocabList()
     *
     * Main routine for tests of class VocabList.
     *
     *                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassVocabList(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class VocabList:\n");
        
        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestVLManagement(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestGetPredsAndMatricies(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class VocabList.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class VocabList.\n\n");
        }
        
        return pass;
        
    } /* Database::TestClassVocabList() */
    
    
    /**
     * TestGetPredAndMatricies()
     *
     * Run a battery of tests on the getMatricies() and getPreds() methods.
     *
     *                                              JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestGetPredsAndMatricies(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getPreds() and getMatricies()                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        VocabList vl = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        PredicateVocabElement p7 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m2 = null;
        MatrixVocabElement m3 = null;
        MatrixVocabElement m4 = null;
        MatrixVocabElement m5 = null;
        MatrixVocabElement m6 = null;
        MatrixVocabElement m7 = null;
        IntFormalArg alpha = null;
        FloatFormalArg bravo = null;
        NominalFormalArg charlie = null;
        TextStringFormalArg delta = null;
        PredFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg hotela = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        UnTypedFormalArg mike = null;
        UnTypedFormalArg nero = null;
        UnTypedFormalArg oscar = null;
        UnTypedFormalArg papa = null;
        UnTypedFormalArg quebec = null;
        UnTypedFormalArg reno = null;
        UnTypedFormalArg sierra = null;
        UnTypedFormalArg tango = null;
        java.util.Vector<MatrixVocabElement> matricies = null;
        java.util.Vector<PredicateVocabElement> preds = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* Allocate a database, and verify that getPreds() and getMatricies()
         * return null when run on an empty vocab list.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            vl = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                vl = db.vl;
                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( vl == null ) ||
                 ( matricies != null ) ||
                 ( preds != null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(1).\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( vl == null )
                    {
                        outStream.print("vl not allocated with db?!?!?l.\n");
                    }
                    
                    if ( preds != null )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(1).\n");
                    }

                    if ( matricies != null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
        }
        
        /* Insert several system and/or non matrixType.MATRIX matricies, 
         * and run getMatricies() & getPreds() again.  They should still return
         * null.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                alpha   = new IntFormalArg(db, "<alpha>");
                bravo   = new FloatFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta   = new TextStringFormalArg(db);
                echo    = new PredFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");
                golf    = new UnTypedFormalArg(db, "<golf>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new UnTypedFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                kilo    = new UnTypedFormalArg(db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new UnTypedFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");
                oscar   = new UnTypedFormalArg(db, "<oscar>");
                papa    = new UnTypedFormalArg(db, "<papa>");
                quebec  = new UnTypedFormalArg(db, "<quebec>");
                reno    = new UnTypedFormalArg(db, "<reno>");
                sierra  = new UnTypedFormalArg(db, "<sierra>");
                tango   = new UnTypedFormalArg(db, "<tango>");
                
                p0 = ConstructTestPred(db, "p0", lima, null, null, null);
                p0.setSystem();
                p1 = ConstructTestPred(db, "p1", mike, nero, null, null);
                p1.setSystem();
                p2 = ConstructTestPred(db, "p2", oscar, null, null, null);
                p3 = ConstructTestPred(db, "p3", papa, null, null, null);
                p4 = ConstructTestPred(db, "p4", quebec, null, null, null);
                p5 = ConstructTestPred(db, "p5", reno, null, null, null);
                p6 = ConstructTestPred(db, "p6", sierra, null, null, null);
                p7 = ConstructTestPred(db, "p7", tango, null, null, null);
                
                m0 = ConstructTestMatrix(db, "m0", 
                                         MatrixVocabElement.matrixType.INTEGER, 
                                         alpha, null, null, null);
                m1 = ConstructTestMatrix(db, "m1", 
                                         MatrixVocabElement.matrixType.FLOAT, 
                                         bravo, null, null, null);
                m2 = ConstructTestMatrix(db, "m2", 
                                         MatrixVocabElement.matrixType.NOMINAL, 
                                         charlie, null, null, null);
                m3 = ConstructTestMatrix(db, "m3", 
                                         MatrixVocabElement.matrixType.TEXT, 
                                         delta, null, null, null);
                m4 = ConstructTestMatrix(db, "m4", 
                                         MatrixVocabElement.matrixType.PREDICATE, 
                                         echo, null, null, null);
                m5 = ConstructTestMatrix(db, "m5", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         foxtrot, golf, hotel, null);
                m5.setSystem();
                m6 = ConstructTestMatrix(db, "m6", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         india, juno, null, null);
                m7 = ConstructTestMatrix(db, "m7", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         kilo, null, null, null);
                
                vl.addElement(m0);
                vl.addElement(m1);
                vl.addElement(m2);
                vl.addElement(m3);
                vl.addElement(m4);
                vl.addElement(m5);
                vl.addElement(p0);
                vl.addElement(p1);

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( alpha == null ) || ( bravo == null ) || 
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( golf == null ) || ( hotel == null ) ||
                 ( india == null ) || ( juno == null ) ||
                 ( kilo == null ) || ( lima == null ) ||
                 ( mike == null ) || ( nero == null ) ||
                 ( oscar == null ) || ( papa == null ) ||
                 ( quebec == null ) || ( reno == null ) ||
                 ( sierra == null ) || ( tango == null ) ||
                 ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                 ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) || ( p7 == null ) ||
                 ( m0 == null ) || ( m1 == null ) || ( m2 == null ) ||
                 ( m3 == null ) || ( m4 == null ) || ( m5 == null ) ||
                 ( m6 == null ) || ( m7 == null ) ||
                 ( matricies != null ) ||
                 ( preds != null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(2).\n");
                    }
                    
                    if ( ( alpha == null ) || ( bravo == null ) || 
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( golf == null ) || ( hotel == null ) ||
                         ( india == null ) || ( juno == null ) ||
                         ( kilo == null ) || ( lima == null ) ||
                         ( mike == null ) || ( nero == null ) ||
                         ( oscar == null ) || ( papa == null ) ||
                         ( quebec == null ) || ( reno == null ) ||
                         ( sierra == null ) || ( tango == null ) )
                    {
                        outStream.print("formal arg alloc(s) failed.\n");
                    }
                    
                    if ( ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                         ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                         ( p6 == null ) || ( p7 == null ) )
                    {
                        outStream.print("predicate alloc(s) failed.\n");
                    }
                    
                    if ( ( m0 == null ) || ( m1 == null ) || ( m2 == null ) ||
                         ( m3 == null ) || ( m4 == null ) || ( m5 == null ) ||
                         ( m6 == null ) || ( m7 == null ) )
                    {
                        outStream.print("matrix alloc(s) failed.\n");
                    }
                                        
                    if ( preds != null )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(2).\n");
                    }

                    if ( matricies != null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
        }
        
        
        if ( failures == 0 )
        {
            long keys[] = {1, 3, 5, 7, 9, 11, 15, 17};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 
                              11, 12, 13, 14, 15, 16, 17, 18, 19};
            DBElement idxValues[] = {m0, alpha, 
                                     m1, bravo,
                                     m2, charlie,
                                     m3, delta,
                                     m4, echo,
                                     m5, foxtrot, golf, hotel,
                                     p0, lima, 
                                     p1, mike, nero};
            
            if ( ! VerifyVLContents(8, keys, values, vl, outStream, 
                                    verbose, 1) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(19, idxKeys, idxValues, 
                                               db.idx, outStream, 
                                               verbose, 1) )
            {
                failures++;
            }
        }

        
        /* Insert one non-system predicate and one non-system matrixType.MATRIX
         * predicate, and run getMatricies() & getPreds() again.  Copies should 
         * show up in the returned vectors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(m6);
                vl.addElement(p2);

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( matricies == null ) ||
                 ( preds == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(3).\n");
                    }
                                                            
                    if ( preds == null ) 
                    {
                        outStream.print(
                                "unexpected return from getPreds()(3).\n");
                    }

                    if ( matricies == null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(3).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(3): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }            
        }
        
        
        if ( failures == 0 )
        {
            long keys[] = {1, 3, 5, 7, 9, 11, 15, 17, 20, 23};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1, m6, p2};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 
                              11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                              21, 22, 23, 24};
            DBElement idxValues[] = {m0, alpha, 
                                     m1, bravo,
                                     m2, charlie,
                                     m3, delta,
                                     m4, echo,
                                     m5, foxtrot, golf, hotel,
                                     p0, lima, 
                                     p1, mike, nero,
                                     m6, india, juno,
                                     p2, oscar};
            MatrixVocabElement matrixValues[] = {m6};
            PredicateVocabElement predValues[] = {p2};

            if ( ! VerifyVLContents(10, keys, values, vl, outStream, 
                                    verbose, 2) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(24, idxKeys, idxValues, 
                                               db.idx, outStream, 
                                               verbose, 2) )
            {
                failures++;
            }
            
            if ( ! VerifyVectorContents(matricies, 1, matrixValues, 
                                        outStream, verbose, 2) )
            {
                failures++;
            }
            
            if ( ! VerifyVectorContents(preds, 1, predValues, 
                                        outStream, verbose, 2) )
            {
                failures++;
            }
        }
        
        /* Insert more non-systems preds and matricies.  Check the output
         * yet again.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(p3);
                vl.addElement(p4);
                vl.addElement(p5);
                vl.addElement(m7);
                vl.addElement(p6);
                vl.addElement(p7);

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( matricies == null ) ||
                 ( matricies.size() != 2 ) ||
                 ( preds == null ) ||
                 ( preds.size() != 6 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(4).\n");
                    }
                                                            
                    if ( ( preds == null ) ||
                         ( preds.size() != 1 ) ||
                         ( preds.get(0) == p2 ) ||
                         ( preds.get(0).toDBString().
                            compareTo(p2.toDBString()) != 0 ) )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(4).\n");
                    }

                    if ( ( matricies == null ) ||
                         ( matricies.size() != 2 ) ||
                         ( matricies.get(0) == m6 ) ||
                         ( matricies.get(0).toDBString().
                            compareTo(m6.toDBString()) != 0 ) )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(4).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(4): \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = { 1,  3,  5,  7,  9, 11, 15, 17,
                           20, 23, 25, 27, 29, 31, 33, 35};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1, 
                                     m6, p2, p3, p4, p5, m7, p6, p7};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 
                              11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                              21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                              31, 32, 33, 34, 35, 36};
            DBElement idxValues[] = {m0, alpha, 
                                     m1, bravo,
                                     m2, charlie,
                                     m3, delta,
                                     m4, echo,
                                     m5, foxtrot, golf, hotel,
                                     p0, lima, 
                                     p1, mike, nero,
                                     m6, india, juno,
                                     p2, oscar,
                                     p3, papa,
                                     p4, quebec, 
                                     p5, reno,
                                     m7, kilo,
                                     p6, sierra,
                                     p7, tango};
            MatrixVocabElement matrixValues[] = {m6, m7};
            PredicateVocabElement predValues[] = {p2, p3, p4, p5, p6, p7};

            if ( ! VerifyVLContents(16, keys, values, vl, outStream, 
                                    verbose, 1) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(36, idxKeys, idxValues, 
                                               db.idx, outStream, 
                                               verbose, 1) )
            {
                failures++;
            }
                
            if ( ! VerifyVectorContents(matricies, 2, matrixValues, 
                                        outStream, verbose, 4) )
            {
                failures++;
            }

            if ( ! VerifyVectorContents(preds, 6, predValues, 
                                        outStream, verbose, 4) )
            {
                failures++;
            }
        }         
                                                    
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DBIndex::TestGetPredsAndMatricies() */
    
   
    /**
     * TestVLManagement()
     *
     * Run a battery of tests on vocab list management.
     *
     *                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestVLManagement(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing vocab list management for class VocabList                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean inVL0;
        boolean inVL1;
        boolean inVL2;
        boolean inVL3;
        boolean inVL4;
        boolean mInVL0;
        boolean mInVL1;
        boolean mInVL2;
        boolean mInVL3;
        boolean mInVL4;
        boolean mInVL5;
        boolean pInVL0;
        boolean pInVL1;
        boolean pInVL2;
        boolean pInVL3;
        boolean pInVL4;
        boolean pInVL5;
        boolean methodReturned = false;
        boolean contentsVerified;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        Database another_db = null;
        VocabList vl = null;
        DBElement dbe = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p1a = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p3a = null;
        PredicateVocabElement p3dup = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m1a = null;
        MatrixVocabElement m2 = null;
        MatrixVocabElement m2a = null;
        MatrixVocabElement m3 = null;
        MatrixVocabElement m3a = null;
        VocabElement ve0;
        VocabElement ve1;
        VocabElement ve2;
        VocabElement ve3;
        VocabElement ve4;
        VocabElement ve5;
        VocabElement ve6;
        VocabElement ve7;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg bravoa = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        UnTypedFormalArg echo = null;
        UnTypedFormalArg echoa = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg foxtrota = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg golfa = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg hotela = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        FloatFormalArg mike = null;
        FloatFormalArg mikea = null;
        UnTypedFormalArg nero = null;
        UnTypedFormalArg oscar = null;
        UnTypedFormalArg papa = null;
        NominalFormalArg quebec = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* Start by allocating the vocab list and database that we will be 
         * using in the test.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            db = null;
            vl = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                another_db = new ODBCDatabase();
                /* For test purposes, use the vl allocated as part of the db .
                 * This will prevent a bunch of sanity check failures.
                 */
                vl = db.vl;
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( db == null ) ||
                 ( another_db == null ) ||
                 ( vl == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "db & vl allocations failed to complete.\n");
                    }
                    
                    if ( ( db == null ) || ( another_db == null ) )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( vl == null )
                    {
                        outStream.print(
                                "vl not allocated with db?!?!?l.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("db & vl allocations threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Allocate a bunch of vocab elements.  These are just
         * convenient instances for use in testing.
         */
        if ( failures == 0 )
        {
            alpha   = null;
            bravo   = null;
            charlie = null;
            delta   = null;
            echo    = null;
            foxtrot = null;
            hotel   = null;
            india   = null;
            juno    = null;
            kilo    = null;
            lima    = null;
            mike    = null;
            nero    = null;
            oscar   = null;
            papa    = null;
            quebec  = null;
            m0      = null;
            m1      = null;
            m2      = null;
            m3      = null;
            p0      = null;
            p1      = null;
            p2      = null;
            p3      = null;
            p3dup   = null;
            p4      = null;
            p5      = null;
            p6      = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                alpha   = new UnTypedFormalArg(db, "<alpha>");
                bravo   = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta   = new UnTypedFormalArg(db, "<delta>");
                echo    = new UnTypedFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new UnTypedFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                kilo    = new UnTypedFormalArg(db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new FloatFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");
                oscar   = new UnTypedFormalArg(db, "<oscar>");
                papa    = new UnTypedFormalArg(another_db, "<papa>");
                quebec  = new NominalFormalArg(db, "<quebec>");
                
                p0 = ConstructTestPred(db, "p0", alpha, null, null, null);
                p1 = ConstructTestPred(db, "p1", bravo, charlie, null, null);
                p2 = ConstructTestPred(db, "p2", null, null, null, null);
                p3 = ConstructTestPred(db, "p3", india, null, null, null);
                p3dup = ConstructTestPred(db, "p3", null, null, null, null);
                p4 = ConstructTestPred(another_db, "p4", papa, null, null, null);
                p5 = ConstructTestPred(another_db, "p5", null, null, null, null);
                p6 = ConstructTestPred(db, "p6", null, null, null, null);
                
                m0 = ConstructTestMatrix(db, "m0", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         delta, null, null, null);
                m1 = ConstructTestMatrix(db, "m1", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         echo, foxtrot, hotel, null);
                m2 = ConstructTestMatrix(db, "m2", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         juno, kilo, lima, null);
                m3 = ConstructTestMatrix(db, "m3", 
                                         MatrixVocabElement.matrixType.FLOAT, 
                                         mike, null, null, null);
                
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( alpha == null ) || ( bravo == null ) || 
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( hotel == null ) || ( india == null ) ||  
                 ( juno == null ) || ( kilo == null ) ||
                 ( lima == null ) || ( mike == null ) ||
                 ( nero == null ) || ( oscar == null ) || 
                 ( papa == null ) || ( quebec == null ) ||
                 ( p0 == null ) || ( p1 == null ) || 
                 ( p2 == null ) || ( p3 == null ) || 
                 ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) ||
                 ( m0 == null ) || ( m1 == null ) ||
                 ( m2 == null ) || ( m3 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Allocations failed to complete.\n");
                    }
                    
                    if ( ( alpha == null ) || ( bravo == null ) || 
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) ||  
                         ( juno == null ) || ( kilo == null ) ||
                         ( lima == null ) || ( mike == null ) ||
                         ( nero == null ) || ( oscar == null ) ||
                         ( papa == null ) ) 
                    {
                        outStream.print(
                                "one or more formal arg allocations failed.\n");
                    }
                    
                    if ( ( p0 == null ) || ( p1 == null ) || 
                         ( p2 == null ) || ( p3 == null ) ||
                         ( p4 == null ) || ( p5 == null ) || 
                         ( p6 == null ) )
                        
                    {
                        outStream.print(
                                "one or more pred ve allocations failed.\n");
                    }
                    
                    if ( ( m0 == null ) || ( m1 == null ) ||
                         ( m2 == null ) || ( m3 == null ) )
                    {
                        outStream.print(
                                "one or more matrix ve allocations failed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now try to add several vocab elements to the vocab list, and 
         * verify that they are there.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(p0);
                vl.addElement(m0);
                vl.addElement(p1);
                vl.addElement(m1);
                vl.addElement(p2);
                
                methodReturned = true;
            }
         
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Calls to vl.addElement() failed " +
                                        "to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("vl.addElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {1, 3, 5, 8, 12};
            VocabElement values[] = {p0, m0, p1, m1, p2};
            long idxKeys[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            DBElement idxValues[] = {p0, alpha, 
                                     m0, delta, 
                                     p1, bravo, charlie,
                                     m1, echo, foxtrot, hotel, 
                                     p2};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 1) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(12, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 1) )
            {
                failures++;
            }
        }
        
        /* Now delete several entries from the vocab list, and see if we get the
         * expected results.
         *
         * Note that we remove one entry from either end, and one from somewhere
         * near the middle.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.removeVocabElement(3);
                vl.removeVocabElement(12);
                vl.removeVocabElement(1);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Calls to vl.removeVocabElement() " +
                                        "failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("idx.removeElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8};
            VocabElement values[] = {p1, m1};
            long idxKeys[] = {5, 6, 7, 8, 9, 10, 11};
            DBElement idxValues[] = {p1, bravo, charlie,
                                     m1, echo, foxtrot, hotel};

            if ( ! VerifyVLContents(2, keys, values, vl, outStream, 
                                    verbose, 2) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(7, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 2) )
            {
                failures++;
            }
        }
        
        /* Now add three more entries just to bring us back up to 5.
         * In passing, verify that inVocabList() and getVocabElement()
         * work as they should with valid input.  Also verify that the
         * matrix and predicate specific versions work correctly with 
         * valid input.
         */
        if ( failures == 0 )
        {
            inVL0 = true;
            inVL1 = false;
            inVL2 = true;
            inVL3 = false;
            mInVL0 = true;
            mInVL1 = true;
            mInVL2 = true;
            mInVL3 = true;
            mInVL4 = false;
            mInVL5 = false;
            pInVL0 = true;
            pInVL1 = false;
            pInVL2 = true;
            pInVL3 = false;
            pInVL4 = true;
            pInVL5 = true;
            ve0 = null;
            ve1 = null;
            ve2 = null;
            ve3 = null;
            ve4 = null;
            ve5 = null;
            ve6 = null;
            ve7 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(m2);
                inVL0 = vl.inVocabList(1);
                inVL1 = vl.inVocabList(5);
                inVL2 = vl.inVocabList("p0");
                inVL3 = vl.inVocabList("p1");
                mInVL0 = vl.matrixInVocabList(1);
                mInVL1 = vl.matrixInVocabList(5);
                mInVL2 = vl.matrixInVocabList("p0");
                mInVL3 = vl.matrixInVocabList("p1");
                mInVL4 = vl.matrixInVocabList(8);
                mInVL5 = vl.matrixInVocabList("m1");
                pInVL0 = vl.predInVocabList(1);
                pInVL1 = vl.predInVocabList(5);
                pInVL2 = vl.predInVocabList("p0");
                pInVL3 = vl.predInVocabList("p1");
                pInVL4 = vl.predInVocabList(8);
                pInVL5 = vl.predInVocabList("m1");
                vl.addElement(m3);
                ve0 = vl.getVocabElement(8);
                ve1 = vl.getVocabElement("m1");
                vl.addElement(p3);
                ve2 = vl.getVocabElement(5);
                ve3 = vl.getVocabElement("p1");
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( inVL0 != false ) || ( inVL1 != true ) || 
                 ( inVL2 != false ) || ( inVL3 != true ) ||
                 ( mInVL0 != false ) || ( mInVL1 != false ) || 
                 ( mInVL2 != false ) || ( mInVL3 != false ) ||
                 ( mInVL4 != true ) || ( mInVL5 != true ) ||
                 ( pInVL0 != false ) || ( pInVL1 != true ) || 
                 ( pInVL2 != false ) || ( pInVL3 != true ) ||
                 ( pInVL4 != false ) || ( pInVL5 != false ) ||
                 ( ve0 != m1 ) || ( ve1 != m1 ) ||
                 ( ve2 != p1 ) || ( ve3 != p1 ) ||
                 ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( inVL0 != false )
                    {
                        outStream.print("vl.inVocabList(1) returned true.\n");
                    }
                    
                    if ( inVL1 != true )
                    {
                        outStream.print("vl.inVocabList(5) returned false.\n");
                    }
                    
                    if ( inVL2 != false )
                    {
                        outStream.print(
                                "vl.inVocabList(\"p0\") returned true.\n");
                    }
                    
                    if ( inVL3 != true )
                    {
                        outStream.print(
                                "vl.inVocabList(\"p1\") returned false.\n");
                    }
                    
                    
                    if ( mInVL0 != false ) 
                    {
                        outStream.print(
                                "vl.matrixInVocabList(1) returned true.\n");
                    }
                    
                    if ( mInVL1 != false )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(5) returned true.\n");
                    }
                    
                    if ( mInVL2 != false ) 
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"p0\") returned true.\n");
                    }
                    
                    if ( mInVL3 != false ) 
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"p1\") returned true.\n");
                    }
                    
                    if ( mInVL4 != true ) 
                    {
                        outStream.print(
                                "vl.matrixInVocabList(8) returned false.\n");
                    }
                    
                    if ( mInVL5 != true )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"m1\") returned false.\n");
                    }
                    
                    
                    if ( pInVL0 != false ) 
                    {
                        outStream.print(
                                "vl.predInVocabList(1) returned true.\n");
                    }
                    
                    if ( pInVL1 != true )
                    {
                        outStream.print(
                                "vl.predInVocabList(5) returned false.\n");
                    }
                    
                    if ( pInVL2 != false ) 
                    {
                        outStream.print(
                                "vl.predInVocabList(\"p0\") returned true.\n");
                    }
                    
                    if ( pInVL3 != true ) 
                    {
                        outStream.print(
                                "vl.predInVocabList(\"p1\") returned false.\n");
                    }
                    
                    if ( pInVL4 != false ) 
                    {
                        outStream.print(
                                "vl.predInVocabList(8) returned true.\n");
                    }
                    
                    if ( pInVL5 != false )
                    {
                        outStream.print(
                                "vl.predInVocabList(\"m1\") returned true.\n");
                    }
                    
                     
                    if ( ve0 != m1 )
                    {
                        outStream.print("vl.getVocabElement(8) != p1\n");
                    }
                     
                    if ( ve1 != m1 )
                    {
                        outStream.print("vl.getVocabElement(\"m1\") != p1\n");
                    }
                     
                    if ( ve2 != p1 )
                    {
                        outStream.print("vl.getVocabElement(5) != p1\n");
                    }
                     
                    if ( ve3 != p1 )
                    {
                        outStream.print("vl.getVocabElement(\"p1\") != p1\n");
                    }
                    
                    if ( ! methodReturned )
                    {
                        outStream.print("test failed to complete(1).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1, m1, m2, m3, p3};
            long idxKeys[] = {5, 6, 7, 8, 9, 10, 11, 13, 14, 
                              15, 16, 17, 18, 19, 20};
            DBElement idxValues[] = {p1, bravo, charlie,
                                     m1, echo, foxtrot, hotel,
                                     m2, juno, kilo, lima,
                                     m3, mike,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 3) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(15, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 3) )
            {
                failures++;
            }
        }
        
        /* Now simulate a bunch of edits to the entries on the vocab list.
         * This will be done by obtaining a copy of the entry, modifying it
         * and then replacing the old entry with the new.
         */
        
        if ( failures == 0 )
        {
            FormalArgument fArg;
            
            bravoa = null;
            p1a = null;
            inVL0 = false;
            inVL1 = false;
            inVL2 = false;
            inVL3 = false;
            inVL4 = true;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                /* modify a predicate */
                p1a = (PredicateVocabElement)(vl.getVocabElement("p1"));
                p1a = new PredicateVocabElement(p1a);
                /* add a formal arg, modify a formal arg, and delete a
                 * formal arg
                 */
                p1a.appendFormalArg(nero);
                bravoa = (UnTypedFormalArg)(p1a.copyFormalArg(0));
                bravoa.setFargName("<bravoa>");
                p1a.replaceFormalArg(bravoa, 0);
                p1a.deleteFormalArg(1);
                /* change name */
                p1a.setName("p1a");
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(p1a);
                /* verify that inVocabList() works on the revised entry */
                inVL0 = vl.inVocabList(5);
                inVL1 = vl.inVocabList("p1a");
                inVL4 = vl.inVocabList("p1");
                
                /* now modify a matrix */
                m1a = (MatrixVocabElement)(vl.getVocabElement(8));
                m1a = new MatrixVocabElement(m1a);
                /* This time, just add a formal argument, and change
                 * the order of the existing arguments.
                 */
                echoa = (UnTypedFormalArg)m1a.getFormalArg(0);
                foxtrota = (UnTypedFormalArg)m1a.getFormalArg(1);
                hotela = (UnTypedFormalArg)m1a.getFormalArg(2);
                /* make echo the second formal argument */
                m1a.deleteFormalArg(0);
                m1a.insertFormalArg(echoa, 1);
                /* Insert oscar at the head of the formal argument list */
                m1a.insertFormalArg(oscar, 0);
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(m1a);
                
                /* Modify a Float matrix */
                m3a = (MatrixVocabElement)vl.getVocabElement(17);
                m3a = new MatrixVocabElement(m3a);
                mikea = (FloatFormalArg)m3a.getFormalArg(0);
                mikea.setFargName("<mikea>");
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(m3a);
                /* verify that the modified version of m3 is detectable by
                 * inVocabList()
                 */
                inVL2 = vl.inVocabList(17);
                inVL3 = vl.inVocabList("m3");
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( p1a == null ) || ( p1a == p1 ) || 
                 ( bravoa == null ) || ( bravoa == bravo ) ||
                 ( inVL0 == false ) || ( inVL1 == false ) ||
                 ( inVL2 == false ) || ( inVL3 == false ) ||
                 ( inVL4 == true ) || ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ( p1a == null ) || ( p1a == p1 ) )
                    {
                        outStream.print("p1a allocation failed.\n");
                    }
                    
                    if ( ( bravoa == null ) || ( bravoa == bravo ) )
                    {
                        outStream.print("bravoa allocation failed.\n");
                    }
                    
                    if ( ( inVL0 == false ) || ( inVL1 == false ) ||
                         ( inVL2 == false ) || ( inVL3 == false ) ||
                         ( inVL4 == true ) )
                    {
                        outStream.print("Bad inVocabList() result(s).\n");
                    }
                    
                    if ( ! methodReturned )
                    {
                        outStream.print("test failed to complete(2).\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 4) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 4) )
            {
                failures++;
            }
        }
        
        
        /* At this point we have tested functionality with valid input.
         *
         * Start by verifying that addElement() generates the expected
         * errors.
         */
        
        /* Start by trying to insert a VocabElement whose ID has already been
         * defined.
         *
         * p0's id was set the first time we inserted it, so we will use
         * it as a test element.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(p0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.addElement() with bad " +
                                        "id completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad id) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 5) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 5) )
            {
                failures++;
            }
        }


        /* Now try to add a VocabElement with a database reference that doesn't
         * match that of the index.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(p4);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.addElement() with bad " +
                                        "db completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad db) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 6) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 6) )
            {
                failures++;
            }
        }
        
        
        /* Now try to add a vocab element to the vocab list that is already 
         * in the vocab list.
         * To avoid triggering the ID aleady set error we will have to set
         * the id to INVALID_ID
         */

        if ( failures == 0 )
        {
            long old_id = DBIndex.INVALID_ID;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                old_id = DBElement.ResetID(p3);
                vl.addElement(p3);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.addElement() with ve " +
                                        "already in vl completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad ve) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
            else /* fix up p3's ID so we dont' confuse the index */
            {
                methodReturned = false;
                threwSystemErrorException = false;
                
                try
                {
                    p3.setID(old_id);
                    methodReturned = true;
                }
                
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                    systemErrorExceptionString = e.getMessage();
                }
                
                if ( ( ! methodReturned ) ||
                     ( threwSystemErrorException ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        if ( ! methodReturned )
                        {
                            outStream.print("p3.setID() failed to complete.\n");
                        }
                        
                        if ( threwSystemErrorException )
                        {
                            outStream.printf("p3.setID() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                        }
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 7) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 7) )
            {
                failures++;
            }
        }
        
        /* Now pass a null to addElement().  This should fail with a 
         * system error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.addElement(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(null) failed to throw " +
                                        "a system error exception:.\n");
                    }
                }
            }
        }        
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 8) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 8) )
            {
                failures++;
            }
        }
        
        /* Try to add an element with a name that is already in use. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.addElement(p3dup);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.addElement() with name " +
                                        "in use completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(name in use) failed " +
                                "to throw a system error exception:.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 9) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 9) )
            {
                failures++;
            }
        }
        

        /*
         * Next, verify that getElement() fails as expected.
         */
        /* Start by verifying that getVocabElement fails when passed the 
         * invalid ID.
         */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement(DBIndex.INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ve0 != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ( ve0 != null ) || ( methodReturned ) )
                    {
                        outStream.print("Call to vl.getVocabElement" +
                                        "(INVALID_ID) completed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("vl.getElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 10) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 10) )
            {
                failures++;
            }
        }
        
        
        /* Likewise verify that calling getVocabElement with an ID that is 
         * not in the vocab list will generate a system error.
         */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement(1);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.getVocabElement(1) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(1) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 11) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 11) )
            {
                failures++;
            }
        }
        
        
        /* Also verify that calling getVocabElement with a null vocab element
         * name, an empty name, and invalid name, or a vocab element name
         * that is not in the vocab list will generate a system error.
         */
        
        /* pass getVocabElement() a null */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.getVocabElement(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(null) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 12) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 12) )
            {
                failures++;
            }
        }
        
        /* pass getVocabElement() the empty name */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement("");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.getVocabElement(\"\") " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"\") failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 13) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 13) )
            {
                failures++;
            }
        }
        
        
        /* pass getVocabElement() an invalid name */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement("<invalid>");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.getVocabElement" +
                                        "(\"<invalid>\") completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 14) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 14) )
            {
                failures++;
            }
        }
        
        /* pass getVocabElement() a name that isn't in the vocab list */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                ve0 = vl.getVocabElement("p0");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.getVocabElement(\"p0\") " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"p0\") failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 15) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 15) )
            {
                failures++;
            }
        }
        
        
        /* 
         * Next, verify that inVocabList() fails where expected.  This is  
         * pretty easy, as the only ways inVocabList() should fail are if you  
         * pass it the INVALID_ID, null, the empty string, or an invalid name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.inVocabList(DBIndex.INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.inVocabList(INVALID_ID) " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(INVALID_ID) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 16) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 16) )
            {
                failures++;
            }
        }
        
        /* pass inVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.inVocabList(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.inVocabList(null) " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 17) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 17) )
            {
                failures++;
            }
        }
        
        /* pass inVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.inVocabList("");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.inVocabList(\"\") " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(\"\") " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 18) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 18) )
            {
                failures++;
            }
        }
        
        /* pass inVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.inVocabList("<invalid>");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.inVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 19) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 19) )
            {
                failures++;
            }
        }
        
        
        /* 
         * Next, verify that matrixInVocabList() fails where expected.  This is  
         * pretty easy, as the only ways matrixInVocabList() should fail are if   
         * you pass it the INVALID_ID, null, the empty string, or an invalid 
         * name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.matrixInVocabList(DBIndex.INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(INVALID_ID) returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(INVALID_ID) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 20) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 20) )
            {
                failures++;
            }
        }
        
        /* pass matrixInVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.matrixInVocabList(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.matrixInVocabList(null) " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 21) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 21) )
            {
                failures++;
            }
        }
        
        /* pass matrixInVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.matrixInVocabList("");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.matrixInVocabList(\"\") " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList(\"\") " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 22) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 22) )
            {
                failures++;
            }
        }
        
        /* pass matrixInVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.matrixInVocabList("<invalid>");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 23) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 23) )
            {
                failures++;
            }
        }
        
        
        /* 
         * Next, verify that predInVocabList() fails where expected.  This is  
         * pretty easy, as the only ways predInVocabList() should fail are if   
         * you pass it the INVALID_ID, null, the empty string, or an invalid 
         * name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.predInVocabList(DBIndex.INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(INVALID_ID) returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(INVALID_ID) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 24) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 24) )
            {
                failures++;
            }
        }
        
        /* pass predInVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.predInVocabList(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.predInVocabList(null) " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 25) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 25) )
            {
                failures++;
            }
        }
        
        /* pass predInVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.predInVocabList("");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.predInVocabList(\"\") " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList(\"\") " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 26) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 26) )
            {
                failures++;
            }
        }
        
        /* pass predInVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = vl.predInVocabList("<invalid>");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 27) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 27) )
            {
                failures++;
            }
        }
        

        /*
         * Next, verify that removeVocabElement() in the expected places.
         */
        /* Start by feeding removeVocabElement the INVALID_ID */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.removeVocabElement(DBIndex.INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.removeVocabElement" +
                                        "(INVALID_ID) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeVocabElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 28) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 28) )
            {
                failures++;
            }
        }
        
        
        /* now try to remove a non-existant element.  Note that the method
         * should also fail if the target element isn't in the vocab list. 
         * However we test to see if the ID exists in the vocab list first, 
         * and thus this error will only appear if there is a bug in the 
         * hash table.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.removeVocabElement(1);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.removeVocabElement(1) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeElement(1) failed to " +
                                        "throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 29) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 29) )
            {
                failures++;
            }
        }
        
        /* now try to remove an element with a formal argument that isnt
         * in the index.  Set this up by adding a formal argument to p1a.
         * To avoid confusing the database, we will have to remove it when
         * we are done.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                p1a.appendFormalArg(alpha);
                vl.removeVocabElement(5);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.removeVocabElement(5) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeElement(5) failed to " +
                                        "throw a system error exception.\n");
                    }
                }
            }
            else /* tidy up */
            {
                methodReturned = false;
                threwSystemErrorException = false;
                systemErrorExceptionString = null;
                
                try
                {
                    p1a.deleteFormalArg(2);
                    methodReturned = true;
                }
        
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                    systemErrorExceptionString = e.getMessage();
                }
                
                if ( ( ! methodReturned ) || 
                     ( threwSystemErrorException ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( ! methodReturned )
                        {
                            outStream.print("p1a.deleteFormalArg(2) failed " +
                                            "to complete.\n");
                        }
                        if ( threwSystemErrorException )
                        {
                            outStream.printf("Unexpected system error in " +
                                    "tidy after removeElement test: %s\n",
                                    systemErrorExceptionString);
                        }
                    }
                    
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 30) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 30) )
            {
                failures++;
            }
        }
        
        
        /* 
         * Finally, verify that replaceVocabElement fails in the expected places.
         */
        /* Start by feeding it a null ve */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.replaceVocabElement(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 31) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 31) )
            {
                failures++;
            }
        }
        
        
        /* Next, feed replaceVocabElement a DBElement with a db field that 
         * doesn't match that of vl.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                p5.setID(5);
                vl.replaceVocabElement(p5);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement" +
                                        "(bad db) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(bad db) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 32) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 32) )
            {
                failures++;
            }
        }
        
        
        /* Next, feed replaceVocabElement a VocabElement with a id field set to 
         * INVALID_ID.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                vl.replaceVocabElement(p6);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(INVALID_ID) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 33) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 33) )
            {
                failures++;
            }
        }
        
        
        /* next, try to replace an element that isn't in the vocab list */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                p6.setID(1);
                vl.replaceVocabElement(p6);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(no_such_id) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(no_such_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 34) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 34) )
            {
                failures++;
            }
        }

        
        /* Try to replace an vocab list entry with an VocabElement
         * of a different sub-class.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                p6.setID(m3a.getID());
                vl.replaceVocabElement(p6);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(type mismatch) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(type mismatch) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 35) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 35) )
            {
                failures++;
            }
        }
        
        /* Finally, try to replace a formal argument in a vocab element
         * with a formal argument of a different subcless.  This should
         * throw a system error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                m2a = (MatrixVocabElement)vl.getVocabElement(13);
                m2a = new MatrixVocabElement(m2a);
                quebec.setID(m2a.getFormalArg(1).getID());
                m2a.replaceFormalArg(quebec, 1);
                vl.replaceVocabElement(m2a);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(farg type mismatch) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(farg type " +
                                        "mismatch) failed to throw a system " +
                                        "error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {5, 8, 13, 17, 19};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = {5, 6, 21, 
                              8, 9, 10, 11, 22,
                              13, 14, 15, 16, 
                              17, 18, 
                              19, 20};
            DBElement idxValues[] = {p1a, bravoa, nero,
                                     m1a, echoa, foxtrota, hotela, oscar,
                                     m2, juno, kilo, lima,
                                     m3a, mikea,
                                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream, 
                                    verbose, 36) )
            {
                failures++;
            }
            
            if ( ! DBIndex.VerifyIndexContents(16, idxKeys, idxValues, 
                                               vl.db.idx, outStream, 
                                               verbose, 36) )
            {
                failures++;
            }
        }
        
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DBIndex::TestVLManagement() */
    
    
    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 5/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        final String expectedVLString0 = "((VocabList) (vl_contents: ()))";
        final String expectedVLString1 = 
                "((VocabList) " +
                 "(vl_contents: " +
                  "(m1(<echo>, <foxtrot>, <hotel>), " +
                   "p1(<bravo>, <charlie>), " +
                   "m0(<arg>), " +
                   "m2(<india>), " +
                   "p2(), " + 
                   "p0(<alpha>))))";
        final String expectedIDXString0 = "((DBIndex) (index_contents: ()))";
        final String expectedIDXString1 = 
                "((DBIndex) (index_contents: (<india>, m2(<india>), p2(), " +
                "<hotel>, <foxtrot>, <echo>, m1(<echo>, <foxtrot>, <hotel>), " +
                "<charlie>, <bravo>, p1(<bravo>, <charlie>), <arg>, " +
                "m0(<arg>), <alpha>, p0(<alpha>))))";
        final String expectedVLDBString0 = 
                "((VocabList) (vl_size: 0) (vl_contents: ()))";
        final String expectedVLDBString1 = 
                "((VocabList) " +
                 "(vl_size: 6) " +
                 "(vl_contents: " +
                  "(((MatrixVocabElement: 8 m1) " +
                    "(system: false) " +
                    "(type: MATRIX) " +
                    "(varLen: false) " +
                    "(fArgList: " +
                     "((TimeStampFormalArg 9 <echo> false null null), " +
                      "(NominalFormalArg 10 <foxtrot> false ()), " +
                      "(UnTypedFormalArg 11 <hotel>))), " +
                   "((PredicateVocabElement: 5 p1) " +
                    "(system: false) " +
                    "(varLen: false) " +
                    "(fArgList: " +
                     "((IntFormalArg 6 <bravo> false " +
                       "-9223372036854775808 9223372036854775807), " +
                     "(NominalFormalArg 7 <charlie> false ()))), " +
                   "((MatrixVocabElement: 3 m0) " +
                    "(system: false) " +
                    "(type: TEXT) " +
                    "(varLen: false) " +
                    "(fArgList: ((TextStringFormalArg 4 <arg>))), " +
                   "((MatrixVocabElement: 13 m2) " + 
                    "(system: false) " +
                    "(type: NOMINAL) " +
                    "(varLen: false) " +
                    "(fArgList: ((NominalFormalArg 14 <india> false ()))), " +
                   "((PredicateVocabElement: 12 p2) " +
                    "(system: false) " +
                    "(varLen: false) " +
                    "(fArgList: ()), " +
                   "((PredicateVocabElement: 1 p0) " +
                    "(system: false) " + 
                    "(varLen: false) " +
                    "(fArgList: " +
                     "((FloatFormalArg 2 <alpha> false " +
                       "-1.7976931348623157E308 1.7976931348623157E308))))))";
        final String expectedIDXDBString0 = 
                "((DBIndex) (nextID: 1) (index_size: 0) (index_contents: ()))";
        final String expectedIDXDBString1 = 
                "((DBIndex) (nextID: 15) (index_size: 14) " + 
                 "(index_contents: " +
                  "((NominalFormalArg 14 <india> false ()), " +
                   "((MatrixVocabElement: 13 m2) " +
                    "(system: false) " +
                    "(type: NOMINAL) " +
                    "(varLen: false) " +
                    "(fArgList: " +
                     "((NominalFormalArg 14 <india> false ()))), " +
                      "((PredicateVocabElement: 12 p2) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: ()), " +
                      "(UnTypedFormalArg 11 <hotel>), " +
                      "(NominalFormalArg 10 <foxtrot> false ()), " +
                      "(TimeStampFormalArg 9 <echo> false null null), " +
                      "((MatrixVocabElement: 8 m1) " +
                       "(system: false) " +
                       "(type: MATRIX) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((TimeStampFormalArg 9 <echo> false null null), " +
                         "(NominalFormalArg 10 <foxtrot> false ()), " +
                         "(UnTypedFormalArg 11 <hotel>))), " +
                      "(NominalFormalArg 7 <charlie> false ()), " +
                      "(IntFormalArg 6 <bravo> false -9223372036854775808 " +
                        "9223372036854775807), " +
                      "((PredicateVocabElement: 5 p1) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((IntFormalArg 6 <bravo> false " +
                          "-9223372036854775808 9223372036854775807), " +
                         "(NominalFormalArg 7 <charlie> false ()))), " +
                      "(TextStringFormalArg 4 <arg>), " +
                      "((MatrixVocabElement: 3 m0) " +
                       "(system: false) " +
                       "(type: TEXT) " +
                       "(varLen: false) " +
                       "(fArgList: ((TextStringFormalArg 4 <arg>))), " +
                      "(FloatFormalArg 2 <alpha> false " +
                        "-1.7976931348623157E308 1.7976931348623157E308), " +
                      "((PredicateVocabElement: 1 p0) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: ((FloatFormalArg 2 <alpha> false " +
                        "-1.7976931348623157E308 1.7976931348623157E308))))))";
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        int progress = 0;
        Database db = null;
        VocabList vl = null;
        FloatFormalArg alpha = null;
        IntFormalArg bravo = null;
        NominalFormalArg charlie = null;
        TextStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        NominalFormalArg foxtrot = null;
        NominalFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        NominalFormalArg india = null;
        UnTypedFormalArg juno = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m2 = null;
 
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();
                vl = db.vl;
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( db == null ) ||
                 ( vl == null ) ||
                 ( ! completed ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( db == null )
                    {
                        outStream.print("db null after setup?!?\n");
                    }
                    
                    if ( vl == null )
                    {
                        outStream.print("vl null after setup?!?\n");
                    }
                    
                    if ( ! completed )
                    {
                        outStream.print(
                            "Setup for strings test failed to complete(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException in " +
                                "setup for strings test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* first see if an enpty vl and index generates the expected string and 
         * debug string.
         */
        
        if ( failures == 0 )
        {
            if ( vl.toString().compareTo(expectedVLString0) != 0 )
            {
                failures++;
                outStream.printf(
                        "vl.toString() returned unexpected value(1): \"%s\".\n",
                        vl.toString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( vl.toDBString().compareTo(expectedVLDBString0) != 0 )
            {
                failures++;
                outStream.printf("vl.toDBString() returned unexpected " +
                        "value(1): \"%s\".\n", vl.toDBString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( db.idx.toString().compareTo(expectedIDXString0) != 0 )
            {
                failures++;
                outStream.printf("db.idx.toString() returned unexpected " +
                        "value(1): \"%s\".\n", db.idx.toString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( db.idx.toDBString().compareTo(expectedIDXDBString0) != 0 )
            {
                failures++;
                outStream.printf("db.idx.toDBString() returned unexpected " +
                        "value(1): \"%s\".\n", db.idx.toDBString());
            }
        }
        
        /* Now allocate and insert a bunch of entries in the vocab list. */
        
        if ( failures == 0 )
        {
            progress = 0;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                alpha   = new FloatFormalArg(db, "<alpha>");
                bravo   = new IntFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta   = new TextStringFormalArg(db);
                echo    = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new NominalFormalArg(db, "<foxtrot>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new NominalFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                
                progress++;
                
                p0 = ConstructTestPred(db, "p0", alpha, null, null, null);
                p1 = ConstructTestPred(db, "p1", bravo, charlie, null, null);
                p2 = ConstructTestPred(db, "p2", null, null, null, null);
                
                m0 = ConstructTestMatrix(db, "m0", 
                                         MatrixVocabElement.matrixType.TEXT, 
                                         delta, null, null, null);
                m1 = ConstructTestMatrix(db, "m1", 
                                         MatrixVocabElement.matrixType.MATRIX, 
                                         echo, foxtrot, hotel, null);
                m2 = ConstructTestMatrix(db, "m2", 
                                         MatrixVocabElement.matrixType.NOMINAL, 
                                         india, null, null, null);
                
                progress++;
                
                vl.addElement(p0);
                progress++;
                vl.addElement(m0);
                progress++;
                vl.addElement(p1);
                progress++;
                vl.addElement(m1);
                progress++;
                vl.addElement(p2);
                progress++;
                vl.addElement(m2);
                
                progress++;

                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! completed ) ||
                 ( alpha == null ) || ( bravo == null ) || 
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( hotel == null ) || ( india == null ) ||  
                 ( p0 == null ) || ( p1 == null ) || 
                 ( p2 == null ) || 
                 ( m0 == null ) || ( m1 == null ) ||
                 ( m2 == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("progress = %d\n", progress);
                    
                    if ( ! completed )
                    {
                        outStream.print("Setup for strings test failed " +
                                        "to complete().\n");
                    }
                    
                    if ( ( alpha == null ) || ( bravo == null ) || 
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) ) 
                    {
                        outStream.print(
                                "one or more formal arg allocations failed.\n");
                    }
                    
                    if ( ( p0 == null ) || ( p1 == null ) || 
                         ( p2 == null ) )
                        
                    {
                        outStream.print(
                                "one or more pred ve allocations failed.\n");
                    }
                    
                    if ( ( m0 == null ) || ( m1 == null ) ||
                         ( m2 == null ) )
                    {
                        outStream.print(
                                "one or more matrix ve allocations failed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now test again to see if we generate the expected strings after the
         * additions to the vocab list.
         */
        
        if ( failures == 0 )
        {
            if ( vl.toString().compareTo(expectedVLString1) != 0 )
            {
                failures++;
                outStream.printf(
                        "vl.toString() returned unexpected value(1): \"%s\".\n",
                        vl.toString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( vl.toDBString().compareTo(expectedVLDBString1) != 0 )
            {
                failures++;
                outStream.printf("vl.toDBString() returned unexpected " +
                        "value(1): \"%s\".\n", vl.toDBString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( db.idx.toString().compareTo(expectedIDXString1) != 0 )
            {
                failures++;
                outStream.printf("db.idx.toString() returned unexpected " +
                        "value(1): \"%s\".\n", db.idx.toString());
            }
        }
        
        if ( failures == 0 )
        {
            if ( db.idx.toDBString().compareTo(expectedIDXDBString1) != 0 )
            {
                failures++;
                outStream.printf("db.idx.toDBString() returned unexpected " +
                        "value(1): \"%s\".\n", db.idx.toDBString());
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;
        
    } /* VocabList::TestToStringMethods() */
        
    
    /**
     * VerifyVectorContents()
     *
     * Verify that the supplied Vector contains the specified number of 
     * elements, and that a copy of each entry in values[] is in the 
     * Vector.
     *
     * Return true if this holds, and false otherwise.
     *
     * Two near identical versions of this method -- one for vectors of 
     * MatrixVocabElements, and one for vectors of PredicateVocabElement.
     * This is sort of stupid, but I can't get java to let me do the 
     * type casting required to avoid this.
     *
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - none.
     */
    
    protected static boolean VerifyVectorContents(java.util.Vector<MatrixVocabElement> v,
                                                  int numEntries,
                                                  MatrixVocabElement values[],
                                                  java.io.PrintStream outStream,
                                                  boolean verbose,
                                                  int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVectorContents(matrix): ";
        String mveString = null;
        boolean matchFound = false;
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        MatrixVocabElement mve = null;
        
        if ( ( v == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }
        
        if ( numEntries != v.size() )
        {
            verified = false;
            
            if ( verbose )
            {
                outStream.printf("mtest %d: bad v size %d (%d expected).\n",
                                     testNum, v.size(), numEntries);
            }
        }
        
        while ( i < numEntries )
        {
            mve = values[i];
            mveString = mve.toDBString();
            j = 0;
            matchFound = false;
            
            while ( ( j < numEntries ) && ( ! matchFound ) )
            {
                if ( mveString.compareTo(v.get(j).toDBString()) == 0 )
                {
                    if ( mve == v.get(i) )
                    {
                        verified = false;
                        
                        if ( verbose )
                        {
                            outStream.printf(
                                    "mtest %d:match (%d, %d) is not a copy.\n",
                                    testNum, i, j);
                        }
                    }
                    else
                    {
                        matchFound = true;
                    }
                }
                j++;
            }
            
            if ( ! matchFound )
            {
                verified = false;
                
                if ( verbose )
                {
                    outStream.printf("mtest %d: no match found for %d.\n",
                                      testNum, i);
                }
            }
            
            i++;
        }

        return verified;
        
    } /* VocabList::VerifyVectorContents(matrix) */
    
    
    protected static boolean VerifyVectorContents(java.util.Vector<PredicateVocabElement> v,
                                                  int numEntries,
                                                  PredicateVocabElement values[],
                                                  java.io.PrintStream outStream,
                                                  boolean verbose,
                                                  int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVectorContents(pred): ";
        String pveString = null;
        boolean matchFound = false;
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        PredicateVocabElement pve = null;
        
        if ( ( v == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }
        
        if ( numEntries != v.size() )
        {
            verified = false;
            
            if ( verbose )
            {
                outStream.printf("ptest %d: bad v size %d (%d expected).\n",
                                     testNum, v.size(), numEntries);
            }
        }
        
        while ( i < numEntries )
        {
            pve = values[i];
            pveString = pve.toDBString();
            j = 0;
            matchFound = false;
            
            while ( ( j < numEntries ) && ( ! matchFound ) )
            {
                if ( pveString.compareTo(v.get(j).toDBString()) == 0 )
                {
                    if ( pve == v.get(i) )
                    {
                        verified = false;
                        
                        if ( verbose )
                        {
                            outStream.printf(
                                    "ptest %d:match (%d, %d) is not a copy.\n",
                                    testNum, i, j);
                        }
                    }
                    else
                    {
                        matchFound = true;
                    }
                }
                j++;
            }
            
            if ( ! matchFound )
            {
                verified = false;
                
                if ( verbose )
                {
                    outStream.printf("ptest %d: no match found for %d.\n",
                                      testNum, i);
                }
            }
            
            i++;
        }

        return verified;
        
    } /* VocabList::VerifyVectorContents(pred) */
    
    
    /**
     * VerifyVLContents()
     *
     * Verify that the supplied instance of VocabList contains the key value 
     * pairs contained in the keys and values vectors, and no others.
     *
     * Also verify that the entries in the vocab list and all their associated
     * formal arguements are in the index.
     *
     * Return true if this holds, and false otherwise. 
     *
     *                                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected static boolean VerifyVLContents(int numEntries,
                                              long keys[],
                                              VocabElement values[],
                                              VocabList vl,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVLContents(): ";
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        
        if ( ( vl == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }
        
        if ( numEntries != vl.vl.size() )
        {
            verified = false;
            
            if ( verbose )
            {
                outStream.printf("test %d: bad vl size %d (%d expected).\n",
                                     testNum, vl.vl.size(), numEntries);
            }
        }
        
        while ( i < numEntries )
        {
            if ( vl.vl.get(keys[i]) != values[i] )
            {
                verified = false;
                
                if ( verbose )
                {
                    outStream.printf("test %d: unexpected value for key %d.\n",
                                     testNum, keys[i]);
                }
            }
            
            expected_idx_size++;
            
            /* now verify that all the formal arguments of the vocab
             * element are in the index.
             */
            for ( j = 0; j < values[i].getNumFormalArgs(); j++ )
            {
                if ( values[i].getFormalArg(j) != 
                        vl.db.idx.getElement(values[i].getFormalArg(j).getID()) )
                {
                    verified = false;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "test %d: formal arg (%d, %d) not in idx.\n",
                                testNum, i, j);
                    }
                }
                else
                {
                    expected_idx_size++;
                }
            }
            
            i++;
        }
        
        if ( ( verified ) && 
             ( DBIndex.GetIndexSize(vl.db.idx) != expected_idx_size ) )
        {
            verified = false;
                    
            if ( verbose )
            {
                outStream.printf(
                        "test %d: idx size = %d != %d = expected idx size.\n",
                        testNum, DBIndex.GetIndexSize(vl.db.idx), 
                        expected_idx_size);
            }
        }
       
        return verified;
        
    } /** DBIndex::VerifyVLContents() */
    
} /* class VocabList */
