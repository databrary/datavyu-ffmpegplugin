/*
 * VocabList.java
 *
 * Created on March 25, 2007, 7:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.OpenSHAPA;
import java.util.HashMap;
import java.util.Vector;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.util.OpenHashtable;

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
    protected OpenHashtable<Long, VocabElement> vl =
             new OpenHashtable<Long, VocabElement>();

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

        if (db == null) {
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
     *
     *    - Modified code to create an instance of MatrixVocabElementListeners
     *      if the supplied vocab element is a mve, and to use that instance
     *      when setting the mve's initial listeners.
     *
     *      In all other cases, we proceed as before -- creating an instance of
     *      VocabElementListeners and using it to set the ve's initial listeners.
     */

    protected void addElement(VocabElement ve)
    throws SystemErrorException, LogicErrorException {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VocabList.class);

        final String mName = "VocabList::addElement(ve): ";
        VocabElementListeners nl = null;

        if (ve == null) {
            throw new SystemErrorException(mName + "Bad ve param");
        } else if (ve.getDB() != db) {
            throw new SystemErrorException(mName + "fe.getDB() != db");
        } else if (ve.getID() != DBIndex.INVALID_ID) {
            throw new SystemErrorException(mName +
                                           "ve.getID() != INVALID_ID");
        } else if (this.vl.containsReference(ve)
                   || this.nameMap.containsKey(ve.name)) {
            throw new LogicErrorException(rMap.getString("Error.veExists",
                                                         ve.getName()));
        }

        this.db.cascadeStart();

        this.db.idx.addElement(ve);

        this.vl.put(ve.getID(), ve);

        addFargListToIndex(ve);

        this.nameMap.put(ve.getName(), ve.getID());

        ve.propagateID();

        if ( ve instanceof MatrixVocabElement )
        {
            nl = new MatrixVocabElementListeners(this.db, ve);
        }
        else
        {
            nl = new VocabElementListeners(this.db, ve);
        }

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
     *
     *    - Modified method to assign IDs to column predicate formal arguments
     *      in instances of MatrixVocabElement.
     *
     *                                              JRM -- 8/31/08
     */

    private void addFargListToIndex(VocabElement ve)
        throws SystemErrorException
    {
        final String mName = "VocabList::addFargListToIndex(ve): ";
        int i;
        int numFormalArgs;
        int numCPFormalArgs = 0;
        FormalArgument farg;

        if (ve == null) {
            throw new SystemErrorException(mName + "Bad ve param");
        }

        if ( ve instanceof MatrixVocabElement )
        {
            numCPFormalArgs = ((MatrixVocabElement)ve).getNumCPFormalArgs();

            if ( ((MatrixVocabElement)ve).getType() ==
                  MatrixVocabElement.MatrixType.UNDEFINED )
            {
                numFormalArgs = 0;
            }
            else
            {
                numFormalArgs = ve.getNumFormalArgs();
            }

            assert( numCPFormalArgs == numFormalArgs + 3);
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

        if ( ve instanceof MatrixVocabElement )
        {
            for ( i = 0; i < numCPFormalArgs; i++ )
            {
                farg = ((MatrixVocabElement)ve).getCPFormalArg(i);

                if ( farg == null )
                {
                    throw new SystemErrorException(mName + "farg is null?!?");
                }

                db.idx.addElement(farg);
            }
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
        Vector<MatrixVocabElement> matricies = new Vector<MatrixVocabElement>();
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
                     ( mve.getType() == MatrixVocabElement.MatrixType.MATRIX ) )
                {
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
     * predicates, it returns an empty vector.
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */

    protected java.util.Vector<PredicateVocabElement> getPreds()
        throws SystemErrorException
    {
        Vector<PredicateVocabElement> preds =
                                            new Vector<PredicateVocabElement>();
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
     *
     *    - Added code to delete column predicate formal arguments from
     *      the index in the case of MatrixVocabElement.
     *
     *                                                  JRM -- 8/31/08
     */

    protected void removeVocabElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "VocabList::removeVocabElement(targetID): ";
        int i;
        long id;
        FormalArgument fArg;
        VocabElement ve = null;
        MatrixVocabElement mve = null;

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

            if ( ve instanceof MatrixVocabElement )
            {
                mve = (MatrixVocabElement)ve;

                for ( i = 0; i < mve.getNumCPFormalArgs(); i++ )
                {
                    fArg = mve.getCPFormalArg(i);
                    id = fArg.getID();

                    if ( id == DBIndex.INVALID_ID )
                    {
                        throw new SystemErrorException(mName +
                                                       "Invalid cpfArg id");
                    }
                    else if ( ! this.db.idx.inIndex(id) )
                    {
                        throw new SystemErrorException(mName +
                                                       "cpfArg id not in idx");
                    }
                    else if ( fArg != this.db.idx.getElement(id) )
                    {
                        throw new SystemErrorException(mName +
                                                       "cpfArg not in idx");
                    }
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

        if ( ve instanceof MatrixVocabElement )
        {
            mve = (MatrixVocabElement)ve;

            for ( i = 0; i < mve.getNumCPFormalArgs(); i++ )
            {
                this.db.idx.removeElement(mve.getCPFormalArg(i).getID());
            }
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
     *
     *    - Modified method to deal with the column predicate argument list
     *      of the MatrixVacabElement.
     *                                                  JRM -- 8/31/08
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
        FormalArgument cpFarg = null;
        FormalArgument newCPFarg = null;
        FormalArgument oldCPFarg = null;
        VocabElement old_ve = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement old_mve = null;

        if (ve == null) {
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
        else if ( old_ve.getNumFormalArgs() < 0 )
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

        /* likewise, if the vocab element is a MatrixVocabElement, scan the
         * old and new column predicate formal argument lists, and verify
         * that there are no type changes or duplicate IDs.
         *
         * Also verify the expected equivalencies between regular and column
         * predicate formal arguments.
         */
        if ( ve instanceof MatrixVocabElement )
        {
            mve = (MatrixVocabElement)ve;

            if ( ! ( old_ve instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "ve is a MVE, but old_ve isnt?!?!?");
            }

            old_mve = (MatrixVocabElement)old_ve;

            for ( i = 0; i < mve.getNumCPFormalArgs(); i++ )
            {
                matchFound = false;
                newCPFarg = mve.getCPFormalArg(i);

                if ( newCPFarg == null )
                {
                    throw new SystemErrorException(mName +
                                                   "newCPFarg is null(1)?!?");
                }

                if ( newCPFarg.getID() != DBIndex.INVALID_ID )
                {
                    for ( j = 0; j < old_mve.getNumCPFormalArgs(); j++ )
                    {
                        oldCPFarg = old_mve.getCPFormalArg(j);

                        if ( oldCPFarg == null )
                        {
                            throw new SystemErrorException(mName +
                                    "oldCPFarg is null(1)?!?");
                        }
                        else if ( newCPFarg.getID() == oldCPFarg.getID() )
                        {
                            if ( newCPFarg.getClass() != oldCPFarg.getClass() )
                            {
                                throw new SystemErrorException(mName +
                                        "new/old cp farg type mismatch?!?");
                            }
                            if ( matchFound )
                            {
                                throw new SystemErrorException(mName +
                                        "found multiple cp matches(1)?!?");
                            }
                            else
                            {
                                matchFound = true;
                                match = oldCPFarg;
                            }
                        }
                    }

                    if ( ! matchFound )
                    {
                        throw new SystemErrorException(mName +
                                "no match found for pre-existing cp farg(1)?!?");
                    }

                    for ( j = i + 1; j < mve.getNumCPFormalArgs(); j++ )
                    {
                        cpFarg = mve.getCPFormalArg(j);

                        if ( cpFarg == null )
                        {
                            throw new SystemErrorException(mName +
                                                           "cpFarg is null?!?");
                        }
                        else if ( newCPFarg.getID() == cpFarg.getID() )
                        {
                            throw new SystemErrorException(mName +
                                                        "dup id in new cp farg list");
                        }
                    }
                }
            }

            if ( (mve.getNumFormalArgs() + 3) != mve.getNumCPFormalArgs() )
            {
                throw new SystemErrorException(mName +
                                               "unexpected cp farg list len.");
            }

            for ( i = 0; i < mve.getNumFormalArgs(); i++ )
            {
                farg = mve.getFormalArg(i);
                cpFarg = mve.getCPFormalArg(i + 3);

                if ( ! FormalArgument.FormalArgsAreEquivalent(farg, cpFarg) )
                {
                    throw new SystemErrorException(mName +
                            "regular / cp formal arg mismatch");
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

        /* likewise, if the vocab element is a MatrixVocabElement, replace the
         * old column predicate formal argument list with the new in the
         * index.
         */
        if ( ve instanceof MatrixVocabElement )
        {
            mve = (MatrixVocabElement)ve;

            if ( ! ( old_ve instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "ve is a MVE, but old_ve isnt?!?!?");
            }

            old_mve = (MatrixVocabElement)old_ve;

            /* replace the old farg list with the new in the index */
            for ( i = 0; i < mve.getNumCPFormalArgs(); i++ )
            {
                matchFound = false;
                newCPFarg = mve.getCPFormalArg(i);

                if ( newCPFarg == null )
                {
                    throw new SystemErrorException(mName +
                                                  "newCPFarg is null(2)?!?");
                }

                if ( newCPFarg.getID() == DBIndex.INVALID_ID )
                {
                    /* it is a completely new formal argument -- just insert
                     * it in the index without attempting to find the instance
                     * of FormalArgument it is replacing.
                     */
                    this.db.idx.addElement(newCPFarg);
                }
                else
                {
                    for ( j = 0; j < old_mve.getNumCPFormalArgs(); j++ )
                    {
                        oldCPFarg = old_mve.getCPFormalArg(j);

                        if ( oldCPFarg == null )
                        {
                            throw new SystemErrorException(mName +
                                    "oldCPFarg is null(2)?!?");
                        }
                        else if ( newCPFarg.getID() == oldCPFarg.getID() )
                        {
                            if ( matchFound )
                            {
                                throw new SystemErrorException(mName +
                                        "found multiple cp matches(2)?!?");
                            }
                            else
                            {
                                matchFound = true;
                                match = oldCPFarg;
                            }
                        }
                    }

                    if ( matchFound )
                    {
                        if ( newCPFarg.getClass() == match.getClass() )
                        {
                            this.db.idx.replaceElement(newCPFarg);
                        }
                        else /* type of formal argument has changed */
                        {
                            /* When the type of a formal argument changed,
                             * it should be replaced with a new instance of
                             * the appropriate class of formal argument, and
                             * the ID should be left as INVALID_ID.
                             */
                            throw new SystemErrorException(mName +
                                    "Type of cp formal argument has changed.");
                        }
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "No match found for a pre-existing cp farg(2).");
                    }
                }
            }

            /* it is possible that formal arguments have been removed from the
             * column predicate formal argument list.  Thus we must scan the
             * old matrix vocab element's column predicate formal argument list,
             * and remove from the index all formal arguments that don't have
             * matches in the new matrix vocab elements column predicate formal
             * argument list.
             */
            for ( j = 0; j < old_mve.getNumCPFormalArgs(); j++ )
            {
                matchFound = false;

                oldCPFarg = old_mve.getCPFormalArg(j);

                if ( oldCPFarg == null )
                {
                    throw new SystemErrorException(mName +
                            "oldCPFarg is null(3)?!?");
                }
                else if ( oldCPFarg.getID() == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName +
                            "oldCPFarg doesn't have an ID?!?");
                }

                for ( i = 0; i < mve.getNumCPFormalArgs(); i++ )
                {
                    newCPFarg = mve.getCPFormalArg(i);

                    if ( newCPFarg == null )
                    {
                        throw new SystemErrorException(mName +
                                "newCPFarg is null(3)?!?");
                    }
                    else if ( newCPFarg.getID() == DBIndex.INVALID_ID )
                    {
                        throw new SystemErrorException(mName +
                                "newCPFarg doesn't have an ID?!?");
                    }

                    if ( oldCPFarg.getID() == newCPFarg.getID() )
                    {
                        if ( matchFound )
                        {
                            throw new SystemErrorException(mName +
                                    "found multiple cp matches(3)?!?");
                        }
                        else
                        {
                            matchFound = true;
                        }
                    }
                }

                /* if no match was found, just delete the formal argument from the
                 * index, as it has been deleted from the matrix vocab element.
                 */
                if ( ! matchFound )
                {
                    this.db.idx.removeElement(oldCPFarg.getID());
                }
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

} /* class VocabList */
