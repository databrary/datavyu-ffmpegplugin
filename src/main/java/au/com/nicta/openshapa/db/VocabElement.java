/*
 * VocabElement.java
 *
 * Created on February 13, 2007, 2:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.util.HashUtils;
import java.util.Vector;

/**
 * Class VocabElement
 *
 * Abstract class for vocabulary elements.
 *
 * This class contains functionality that is common to matrix and predicate
 * vocabulary elements.
 *
 * @author mainzer
 */
public abstract class VocabElement extends DBElement
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     * name:  String containing the name of the vocab element.
     *
     *      For matricies, this will be the name of the column variable which
     *      defines them.
     *
     *      For predicates, this will be the predicate name.
     *
     * system:  Boolean field indicating whether this vocab element is directly
     *      visible to and editable by the user.
     *
     *      Examples of vocab elements that are visible to the user include
     *      user defined matrix variables and user defined predicates.
     *
     *      Examples of vocab elements that are not directly visible to the
     *      user include system defined predicates, and single element matricies
     *      used to implement integer, float, nominal, and text column
     *      variables.
     *
     * varLen:  Boolean field used to indicate whether the vocab element's
     *      vocab list is variable length. System maticies will always be fixed
     *      length, but system predicates may be variable length.
     *
     * fArgList: Vector containing the formal argument list of the vocabulary
     *      element.  Once a vocab element has been fully constructed, this
     *      list must contain at least one argument.
     *
     * listeners: Instance of VocabElementListeners containing references to
     *      internal and external objects that must be notified when the
     *      vocabulary element is modified.
     */

    protected String name = "";

    /** whether this is a system vocab element that is not editable by the user */
    protected boolean system = false;

    /** Whether the argument list is variable length */
    protected boolean varLen = false;

    /** formal argument list */
    protected Vector<FormalArgument> fArgList =
            new Vector<FormalArgument>();

    /**
     * reference to instance of VocabElementListeners used to maintain lists of
     *  listeners, and notify them as appropriate.
     */
    protected VocabElementListeners listeners = null;

    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     *
     * VocabElement()
     *
     * Constructor for instances of VocabElement.
     *
     * Two versions of this constructor -- one which sets the name of the
     * of the new vocab element, and one which doesn't.
     *
     * Note that the version that sets the name does only limited error
     * checking -- full error checking is the responsibility of the subclass.
     *
     *                                              JRM -- 2/14/07
     *
     * Changes:
     *
     *    - Added a copy constructor.               JRM -- 4/30/07
     */

    public VocabElement(Database db)
        throws SystemErrorException
    {
        super(db);
    }

    public VocabElement(Database db,
                        String name)
        throws SystemErrorException
    {

        super(db);

        final String mName =
                "VocabElement::VocabElement(db, name): ";

        if (name == null || name.length() <= 0) {
            throw new SystemErrorException(mName + "Bad name param");
        }

        this.name = (new String(name));

    } /* VocabElement(db, name) */

    public VocabElement(VocabElement ve)
        throws SystemErrorException
    {
        super(ve);

        final String mName = "VocabElement::VocabElement(ve): ";
        int i;
        int fArgCount;
        FormalArgument fa;

        if (ve == null) {
            throw new SystemErrorException(mName + "Bad ve param");
        }
        else if ( ( ve.name != null ) && ( ve.name.length() <= 0 ) )
        {
            throw new SystemErrorException(mName + "Bad ve.name");
        }

        if ( ve.name != null )
        {
            this.name = (new String(ve.name));
        }

        /* if ve is an instance of MatrixVocabElement, we must set its
         * type before we copy over the formal arguments.
         */
        if ( ve instanceof MatrixVocabElement )
        {
            if ( ! ( this instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName + "type mismatch.");
            }

            ((MatrixVocabElement)this).type = ((MatrixVocabElement)ve).type;

            if ( ((MatrixVocabElement)this).type ==
                   MatrixVocabElement.MatrixType.UNDEFINED )
            {
                if ( ( this.fArgList != null ) &&
                     ( this.fArgList.size() != 0 ) )
                {
                    throw new SystemErrorException(mName +
                            "UNDEFINED matrix with fargs?!?.");
                }

                fArgCount = 0;
            }
            else
            {
                fArgCount = ve.getNumFormalArgs();
            }
        }
        else
        {
            fArgCount = ve.getNumFormalArgs();
        }

        // copy over the arguments.  Don't use appendFormalArg(), as this
        // method is overridden by at least one subclass, and the data
        // structures it depends on will not have been initialized yet.
        for ( i = 0; i < fArgCount; i++ )
        {
            fa = ve.copyFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "fa is null???");
            }
            else if ( ! fArgNameIsUnique(fa.getFargName()) )
            {
                throw new SystemErrorException(mName + "fa name not unique??.");
            }

            fArgList.add(fa);

            fa.setItsVocabElement(this);
            fa.setItsVocabElementID(this.getID());  /* may be INVALID_ID */
        }

        this.system = ve.system;
        this.varLen = ve.varLen;

        this.listeners = null;

        return;

    } /* VocabElement::VocabElement(ve) */


    /*************************************************************************/
    /******************* Abstract Method Declarations: ***********************/
    /*************************************************************************/

    /**
     * isWellFormed()
     *
     * Subclasses must define this method, which must return true if the
     * given VocabElement is in form suitable for insertion in the vocab list,
     * and false if it isn't.
     *                                              JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    abstract public boolean isWellFormed(boolean newVE)
        throws SystemErrorException;


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getName() & setName()
     *
     * Accessor methods for the name field.  Note that we only do minimal
     * error checking, as that is responsibility of the subclass.
     *
     *                                          JRM -- 2/14/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public String getName()
    {

        return (new String(this.name));

    } /* getName() */


    protected void setName(String name)
        throws SystemErrorException
    {
        final String mName = "VocabElement::setName(): ";

        if ( system )
        {
            throw new SystemErrorException(mName +
                                   "Attempt to modify a system vocab element");
        }

        if (name == null || name.length() <= 0) {
            throw new SystemErrorException(mName + "Bad name param");
        }

        this.name = (new String(name));

        return;

    } /* VocabElement::setName() */


    /**
     * getSystem() & setSystem()
     *
     * Accessor methods for the system field.  Note that the system field
     * is initialized to false, and can only be set to true.  Once it is set
     * to true, the vocab element cannot be modified.
     *
     *                                               JRM -- 2/14/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean getSystem()
    {
        return system;
    }

    public void setSystem()
        throws SystemErrorException
    {
        final String mName = "VocabElement::setSystem(): ";

        /* All vocab elements must have at least one formal argument.  If this
         * vocab element doesn't have one now, it never will.  Scream and die
         * if it doesn't.
         */
        if ( ( fArgList == null ) || ( fArgList.isEmpty() ) )
        {
            throw new SystemErrorException(mName + "fArgList empty?!?!");
        }

        this.system = true;

        return;

    } /* FormalArgument::setSystem() */


    /**
     * getVarLen() & setVarLen()
     *
     * Accessor methods for the varLen field.
     *
     *                                      JRM -- 2/14/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean getVarLen()
    {
        return varLen;
    }

    public void setVarLen(boolean varLen)
        throws SystemErrorException
    {
        final String mName = "VocabElement::setVarLen(): ";

        if ( system )
        {
            throw new SystemErrorException(mName +
                                   "Attempt to modify a system vocab element");
        }

        this.varLen = varLen;
        return;

    } /* FormalArgument::setVarLen() */


    /*************************************************************************/
    /******************** Argument List Manipulation: ************************/
    /*************************************************************************/

    /**
     * appendFormalArg()
     *
     * Append the supplied formal argument to the end of the formal argument
     * list.
     *
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */
    public void appendFormalArg(FormalArgument newArg)
    throws SystemErrorException {
        final String mName = "VocabElement::appendFormalArg(): ";

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }

        if ( newArg == null )
        {
            throw new SystemErrorException(mName +
                                   "Attempt to insert null formal argument");
        }
        else if ( ! fArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }

        fArgList.add(newArg);

        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        return;

    } /* VocabElement::appendFormalArg() */


    /**
     * copyFormalArg()
     *
     * Returns a copy of the n-th formal argument, or null if there
     * is no such argument.
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *   - Added code setting the itsVocabElement field of the copy to null,
     *     and simillarly setting the itsVocabElementID field to the
     *     INVALID_ID.
     *                                          JRM -- 6/15/07
     *
     */

    protected FormalArgument copyFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyFormalArg(): ";
        FormalArgument fArg = null;
        FormalArgument fArgCopy = null;

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= fArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }

        fArg = fArgList.get(n);

        fArgCopy = fArg.CopyFormalArg(false, true);

        if ( fArgCopy == null )
        {
            throw new SystemErrorException(mName + "fArgcopy is null");
        }

        return fArgCopy;

    } /* VocabElement::copyFormalArg() */


    /**
     * copyFormalArgList()
     *
     * Construct and return a vector containing a copy of the formal argument
     * list.
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    protected java.util.Vector<FormalArgument> copyFormalArgList()
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyFormalArgList()";
        int i;
        java.util.Vector<FormalArgument> copy =
                new java.util.Vector<FormalArgument>();

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        for ( i = 0; i < this.fArgList.size(); i++)
        {
            copy.add(this.copyFormalArg(i));
        }

        return copy;

    } /* VocabElement::copyFormalArgList() */

    /**
     * Finds the index of the supplied formal argument.
     *
     * @param fa The formal argument to look for within the vocab element.
     * @return The index of the suppliced formal argument if found, -1 if the
     * supplied formal argument does not exist within the vocab element
     */
    public int findFormalArgIndex(final FormalArgument fa) {
        for (int i = 0; i < this.fArgList.size(); i++) {
            if (fa.equals(this.fArgList.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * deleteFormalArg()
     *
     * Delete the n-th formal argument from the formal argument list.  Throw a
     * system error exception if there is no n-th formal argument.
     *
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public void deleteFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::deleteFormalArg(): ";
        FormalArgument deletedArg = null;

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= fArgList.size() )
        {
            throw new SystemErrorException(mName + "no nth formal argument");
        }

        deletedArg = fArgList.remove(n);

        if ( deletedArg == null )
        {
            throw new SystemErrorException(mName + "deleted arg is null");
        }

        deletedArg.setItsVocabElement(null);
        deletedArg.setItsVocabElementID(DBIndex.INVALID_ID);

        return;

    } /* VocabElement::deleteFormalArg() */


    /**
     * fArgListToDBString()
     *
     * Construct a string containing the names of the formal arguments in a
     * format that displays the full status of the formal arguments and
     * facilitates debugging.
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String fArgListToDBString()
        throws SystemErrorException
    {
        final String mName = "VocabElement::fArgListToDBString(): ";
        int i = 0;
        int numFArgs = 0;
        String s = new String("(");

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        numFArgs = fArgList.size();

        if ( numFArgs > 0 )
        {
            while ( i < (numFArgs - 1) )
            {
                s += getFormalArg(i).toDBString() + ", ";
                i++;
            }
            s += getFormalArg(i).toDBString();
        }

        s += ")";

        return s;

    } /* VocabElement::fArgListToDBString() */


    /**
     * fArgListToString()
     *
     * Construct a string containing the names of the formal arguments in the
     * format: (<arg0>, <arg1>, ... <argn>).
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String fArgListToString()
        throws SystemErrorException
    {
        final String mName = "VocabElement::fArgListToString(): ";
        int i = 0;
        int numFArgs = 0;
        String s = new String("(");

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        numFArgs = fArgList.size();

        if ( numFArgs > 0 )
        {
            while ( i < (numFArgs - 1) )
            {
                s += getFormalArg(i).toString() + ", ";
                i++;
            }
            s += getFormalArg(i).toString();
        }

        s += ")";

        return s;

    } /* VocabElement::fArgListToString() */


    /**
     * fArgNameIsUnique()
     *
     * Scan the formal argument list, and test to see if the supplied formal
     * argument list is unique.  Return true if it is, and false otherwise.
     *
     *                                      JRM -- 3/18/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean fArgNameIsUnique(String fArgName)
        throws SystemErrorException
    {
        final String mName = "VocabElement::fArgNameIsUnique(): ";
        boolean unique = true;

        if ( fArgName == null )
        {
            throw new SystemErrorException(mName + "fArgName null on entry.");
        }
        else if ( ! Database.IsValidFargName(fArgName) )
        {
            throw new SystemErrorException(mName +
                    "fArgName not a valid formal arg name.");
        }

        for ( FormalArgument t : this.fArgList )
        {
            if ( fArgName.compareTo(t.getFargName()) == 0 )
            {
                unique = false;
            }
        }

        return unique;

    } /* VocabElement::fArgNameIsUnique() */


    /**
     * getFormalArg()
     *
     * Returns a copy of the n-th formal argument, or null if there
     * is no such argument.
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *   - Modified the method to simply return the formal argument.  This
     *     change is due to a decision to handle vocab  changes at the
     *     level of vocab elements
     *                                          JRM -- 4/30/07
     *
     */
    public FormalArgument getFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::getFormalArg(): ";
        FormalArgument fArg = null;

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= fArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }

        fArg = fArgList.get(n);

        if ( fArg == null )
        {
            throw new SystemErrorException(mName + "fArg is null?!?");
        }
        if ( ! ( ( fArg instanceof ColPredFormalArg ) ||
                 ( fArg instanceof UnTypedFormalArg ) ||
                 ( fArg instanceof IntFormalArg ) ||
                 ( fArg instanceof FloatFormalArg ) ||
                 ( fArg instanceof TimeStampFormalArg ) ||
                 ( fArg instanceof QuoteStringFormalArg ) ||
                 ( fArg instanceof TextStringFormalArg ) ||
                 ( fArg instanceof NominalFormalArg ) ||
                 ( fArg instanceof PredFormalArg ) ) )
        {
            throw new SystemErrorException(mName + "fArg of unknown type");
        }

        return fArg;

    } /* VocabElement::getFormalArg() */


    /**
     * getNumFormalArgs()
     *
     * Return the number of formal arguments.
     *
     *                                      JRM 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumFormalArgs()
        throws SystemErrorException
    {
        final String mName = "VocabElement::getNumFormalArgs(): ";

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        return fArgList.size();

    } /* VocabElement::getNumFormalArgs() */


    /**
     * insertFormalArg()
     *
     * Insert the supplied formal argument in the n-th position in the formal
     * argument list.  If n is not zero, there must be at least n-1 formal
     * arguments in the list to begin with.  Any existing arguments with
     * index greater than or equal to n have their indicies increased by 1.
     *
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public void insertFormalArg(FormalArgument newArg, int n)
    throws SystemErrorException
    {
        final String mName = "VocabElement::insertFormalArg(): ";

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( system )
        {
            /* attempt to insert an argument in a system (and thus read only)
             * vocab element.
             */
            throw new SystemErrorException(mName +
                    "attempt to modify a system vocab element.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName +
                                   "Attempt to insert null formal argument");
        }
        else if ( ! fArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }
        else  if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n > fArgList.size() )
        {
            /* n-1-th formal argument doesn't exist -- scream and die */
            throw new SystemErrorException(mName + "n > arg list len");
        }

        fArgList.insertElementAt(newArg, n);

        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */

        return;

    } /* VocabElement::insertFormalArg() */


     /**
      * propagateID()
      *
      * Propagate the id assigned to the VocabElement to all current formal
      * arguments, if any.  This method should be called after the VocabElement
      * is assigned an ID and inserted into the vocab list.
      *
      *                                         JRM -- 6/17/07
      *
      * Changes:
      *
      *   - None.
      */

    public void propagateID()
        throws SystemErrorException
    {
        final String mName = "VocabElement::propagateID(): ";
        int i;
        int numFArgs;
        FormalArgument fArg;

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        numFArgs = fArgList.size();

        if ( numFArgs > 0 )
        {
            i = 0;

            while ( i <= (numFArgs - 1) )
            {
                fArg = getFormalArg(i);
                fArg.setItsVocabElementID(this.getID());
                i++;
            }
        }

        return;

    } /* VocabElement::propagateID() */


    /**
     * replaceFormalArg()
     *
     * If the n-th formal argument exists, replace it with the supplied
     * formal argument.
     *
     * Throw a system error exception if there is no n-th formal argument
     * to begin with.
     *
     *                                          JRM -- 2/27/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public void replaceFormalArg(FormalArgument newArg, int n)
    throws SystemErrorException {
        deleteFormalArg(n);
        insertFormalArg(newArg, n);

        return;
    }


    /*************************************************************************/
    /************************ Listener Manipulation: *************************/
    /*************************************************************************/

    /**
     * deregisterExternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister external change listeners message on to
     * the instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterExternalListener(ExternalVocabElementListener el)
        throws SystemErrorException
    {
        final String mName = "VocabElement::deregisterExternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to add external listener to non-cannonical version.");
        }

        this.listeners.deregisterExternalListener(el);

        return;

    } /* VocabElement::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister internal change listeners message on to
     * the instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "VocabElement::deregisterInternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to add internal listener to non-cannonical version.");
        }

        this.listeners.deregisterInternalListener(id);

        return;

    } /* VocabElement::deregisterInternalListener() */


    /**
     * getListeners()
     *
     * Return the corrent value of this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected VocabElementListeners getListeners()
    {

        return this.listeners;

    } /* VocabElement::getListeners() */


    /**
     * noteChange()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass a note changes message on to the instance of
     * VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void noteChange(VocabElement oldVE,
                              VocabElement newVE)
        throws SystemErrorException
    {
        final String mName = "VocabElement::noteChange()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to note changes on non-cannonical version.");
        }

        this.listeners.noteChange(oldVE, newVE);

        return;

    } /* VocabElement::noteChange() */


    /**
     * notifyListenersOfChange()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of changes message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfChange()
        throws SystemErrorException
    {
        final String mName = "VocabElement::notifyListenersOfChange()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to notify listeners of change on non-cannonical version.");
        }

        this.listeners.notifyListenersOfChange();

        return;

    } /* VocabElement::notifyListenersOfChange() */


    /**
     * notifyListenersOfDeletion()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of deletion message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName = "VocabElement::notifyListenersOfDeletion()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                    "Attempt to notify listeners of deletion on " +
                    "non-cannonical version.");
        }

        this.listeners.notifyListenersOfDeletion();

        return;

    } /* VocabElement::notifyListenersOfDeletion() */


    /**
     * registerExternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register external change listeners message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerExternalListener(ExternalVocabElementListener el)
        throws SystemErrorException
    {
        final String mName = "VocabElement::registerExternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to register external listener to non-cannonical version.");
        }

        this.listeners.registerExternalListener(el);

        return;

    } /* VocabElement::registerExternalListener() */


    /**
     * registerInternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register internal change listeners message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "VocabElement::registerInternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to register internal listener to non-cannonical version.");
        }

        this.listeners.registerInternalListener(id);

        return;

    } /* VocabElement::registerInternalListener() */


    /**
     * setListeners()
     *
     * Set the listeners field.  Setting this.listeners to a non-null value
     * signifies that this instance of VocabElement is the cannonical current
     * incarnation of the vocab element.  Setting it back to null indicates
     * that the incarnation has been superceeded.
     *
     * If this.listeners is null, it may be set to reference an instance
     * of VocabElementListeners that is associated with this vocab element.  If
     * this.listeners is not null, the only permissiable new value is null.
     *
     * In all other cases, throw a system error exception.
     *
     *                                          JRM -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void setListeners(VocabElementListeners listeners)
        throws SystemErrorException
    {
        final String mName = "VocabElement::setListeners()";

        if ( this.listeners == null )
        {
            if ( listeners == null )
            {
                throw new SystemErrorException(mName +
                        ": this.listeners is already null");
            }

            this.listeners = listeners;
            this.listeners.updateItsVE(this);
        }
        else
        {
            if ( listeners != null )
            {
                throw new SystemErrorException(mName +
                        ": this.listeners is already non-null.");
            }

            this.listeners = null;
        }

        return;

    } /* VocabElement::setListeners() */

    /**
     * @return A hash value for this object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += HashUtils.Obj2H(name) * Constants.SEED2;
        hash += (new Boolean(system)).hashCode() * Constants.SEED3;
        hash += (new Boolean(varLen)).hashCode() * Constants.SEED4;
        hash += HashUtils.Obj2H(fArgList) * Constants.SEED5;
        hash += HashUtils.Obj2H(listeners) * Constants.SEED6;

        return hash;
    }

    /**
     * Compares this UndefinedDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this
     * UndefinedDataValue, or false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        VocabElement ve = (VocabElement) obj;
        return super.equals(obj)
            && (name == null ? ve.name == null : name.equals(ve.name))
            && system == ve.system
            && varLen == ve.varLen
            && (fArgList == null ? ve.fArgList == null
                                 : fArgList.equals(ve.fArgList))
            && (listeners == null ? ve.listeners == null
                                  : listeners.equals(ve.listeners));
    }

} /* class VocabElement */
