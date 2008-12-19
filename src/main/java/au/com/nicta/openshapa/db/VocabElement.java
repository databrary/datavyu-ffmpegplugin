/*
 * VocabElement.java
 *
 * Created on February 13, 2007, 2:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

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

        if ( ( name == null ) ||
             ( ! ( name instanceof String ) ) ||
             ( name.length() <= 0 ) )
        {
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

        if ( ( ve == null ) || ( ! ( ve instanceof VocabElement) ) )
        {
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

        if ( ( name == null ) ||
             ( ! ( name instanceof String ) ) ||
             ( name.length() <= 0 ) )
        {
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

        if ( ( varLen != true ) && ( varLen != false ) )
        {
            /* I don't think this can happen, but check it anyway. */
            throw new SystemErrorException(mName + "Invalid varLen param");
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
    protected void appendFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
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
        else if ( ! ( newArg instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName +
                                           "newArg not a formal argument");
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

        fArgCopy = FormalArgument.CopyFormalArg(fArg, false, true);

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

    protected void deleteFormalArg(int n)
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
    protected FormalArgument getFormalArg(int n)
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

    protected void insertFormalArg(FormalArgument newArg,
                                   int n)
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
        else if ( ! ( newArg instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName +
                                           "newArg not a formal argument");
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
                fArg.setItsVocabElementID(this.id);
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

    protected void replaceFormalArg(FormalArgument newArg,
                                    int n)
        throws SystemErrorException
    {
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


//    /**
//     * addChangeListener()
//     *
//     * Add a change listener to the listeners list.
//     *
//     *                                  JRM -- 2/28/07
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     */
//
//    public void addChangeListener(VocabChangeListener newListener)
//        throws SystemErrorException
//    {
//        final String mName = "VocabElement::addChangeListener(): ";
//
//        if ( listeners == null )
//        {
//            /* listeners hasn't been instantiated yet -- scream and die */
//            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
//        }
//        else if ( newListener == null )
//        {
//            throw new SystemErrorException(mName + "newListener is null");
//        }
//        else if ( listeners.contains(newListener) )
//        {
//            throw new SystemErrorException(mName +
//                                           "newListener already in listeners");
//        }
//
//        listeners.add(newListener);
//
//        return;
//
//  } /* VocabElement::addChangeListener() */
//
//
//    /**
//     * removeChangeListener()
//     *
//     * Remove a change listener to the listeners list.
//     *
//     *                                  JRM -- 2/28/07
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     */
//
//    public void removeChangeListener(VocabChangeListener target)
//        throws SystemErrorException
//    {
//        final String mName = "VocabElement::addChangeListener(): ";
//
//        if ( listeners == null )
//        {
//            /* listeners hasn't been instantiated yet -- scream and die */
//            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
//        }
//        else if ( target == null )
//        {
//            throw new SystemErrorException(mName + "newListener is null");
//        }
//        else if ( ! listeners.remove(target) )
//        {
//            throw new SystemErrorException(mName + "target not in listeners");
//        }
//
//        if ( listeners.contains(target) )
//        {
//            throw new SystemErrorException(mName +
//                                            "listener still contains target?!?");
//        }
//
//        return;
//
//  } /* VocabElement::removeChangeListener() */
//

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors defined in this class using
     * the instance of some subclass of VocabElement supplied in
     * the argument list.
     *
     * This method is intended to be called in the test code of the subclass
     * of VocabElement, and thus just returns the number of failures
     * unless the verbose parameter is true.
     *
     * Note that the method doesn't leave the supplied instance of some
     * subclass of VocabElement in the same condidtion it found
     * it in, so it is probably best to discard the instance on return.
     *
     *                                          JRM -- 3/17/07
     *
     * Changes:
     *
     *    - Added the setNameOnSystemVEOK field, as I had forgotten that
     *      it was OK to change the name of a system MVE and needed to
     *      modify the test to allow it.
     *                                          JRM -- 11/18/08
     */

    public static int TestAccessors(VocabElement ve,
                                    boolean setNameOnSystemVEOK,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
        final String mName = "VocabElement::TestAccessors(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ! ( ve instanceof VocabElement ) )
        {
            outStream.print(mName +
                    "ve not instanceof AbstractFormalArgument.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }

        /***************************************/
        /* Start by testing accessors for name */
        /***************************************/

        /* verify that we have the default vocab element name */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("test") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(1) \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        /* now change it to another value.  Make it valid for
         * both predicates and matricies.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName("a_valid_name");
                methodReturned= true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("\"arg.setName(\"a_valid_name\")\""
                            + " failed to return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setName(\"a_valid_name\")\""
                            + " threw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change took */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("a_valid_name") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(2) \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        /* Most name validation is done at a higher level, so we can't do much
         * here.  However, we can verify that setName will throw a system error
         * if passed a null.
         */

        if ( failures == 0 )
        {
            String nullString = null;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setName(nullString);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("\"arg.setName(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"arg.setName(null)\" failed to " +
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }


        /* verify that the change didn't take */

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("a_valid_name") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(3) \"%s\".\n",
                                       ve.getName());
                }
            }
        }


        /* Finally, verify that setName() either will or will not refuse to
         * change the name if the system flag is set depending on the value
         * of the setNameOnSystemVEOK.  Note that for the purposes of this
         * test, we change the system flag directly, as otherwise the setup
         * for this test would be much more involved.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve.system = true;

            try
            {
                ve.setName("another_valid_name");
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            ve.system = false;

            if ( setNameOnSystemVEOK )
            {
                if ( ( ! methodReturned ) ||
                     ( threwSystemErrorException ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( ! methodReturned )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "failed to return with system flag set.\n");
                        }

                        if ( ! threwSystemErrorException )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "threw a SystemErrorException " +
                                    "with system flag set.\n");
                        }
                    }
                }
            }
            else
            {
                if ( ( methodReturned ) ||
                     ( ! threwSystemErrorException ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( methodReturned )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "returned with system flag set.\n");
                        }

                        if ( ! threwSystemErrorException )
                        {
                            outStream.print(
                                    "\"arg.setName(\"another_valid_name\")\" " +
                                    "failed to throw a SystemErrorException " +
                                    "with system flag set.\n");
                        }
                    }
                }
            }
        }


        /* again, verify that the change did or didn't take */

        if ( failures == 0 )
        {
            if ( ( ( setNameOnSystemVEOK ) &&
                   ( ve.getName().compareTo("another_valid_name") != 0 ) ) ||
                 ( ( ! setNameOnSystemVEOK ) &&
                   ( ve.getName().compareTo("a_valid_name") != 0 ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected name(4) \"%s\".\n",
                                       ve.getName());
                }
            }
        }


        /***********************************************/
        /* now test the accessors for the varLen field */
        /***********************************************/

        /* verify that valLen has its default value */

        if ( failures == 0 )
        {
            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(1): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* now try to set varLen to true, and verify that the change took */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(true);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "\"arg.setVarLen(true)\" did not return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(true)\" threw a " +
                                "SystemErrorException.\n");
                    }
                }
            }


            if ( ve.getVarLen() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(2): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* now try to set varLen back to false, and verify that the change took */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(false);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "\"arg.setVarLen(false)\" did not return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(false)\" threw a " +
                                "SystemErrorException.\n");
                    }
                }
            }


            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(3): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /* Finally, verify that setVarLen will throw a system error if
         * invoked when the system flag is set.
         */

        if ( failures == 0 )
        {
            ve.system = true;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setVarLen(true);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            ve.system = false;


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("\"arg.setVarLen(true)\" returned " +
                                "with system flag set.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"arg.setVarLen(false)\" failed " +
                                "to throw a SystemErrorException with system " +
                                "flag set.\n");
                    }
                }
            }


            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of varLen(4): %b.\n",
                                       ve.getVarLen());
                }
            }
        }


        /***********************************************/
        /* now test the accessors for the system field */
        /***********************************************/

        /* verify that system has its default value */

        if ( failures == 0 )
        {
            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(1): %b.\n",
                                       ve.getSystem());
                }
            }
        }


        /* now try to set system to true. This should fail as we don't have
         * any formal arguments defined.  Verify that system is still false.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }


            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                            "\"arg.setSystem()\" return when fArgList empty.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"arg.setSystem()\" failed to throw" +
                            " a SystemErrorException when fArgList empty.\n");
                    }
                }
            }


            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(2): %b.\n",
                                       ve.getSystem());
                }
            }
        }

        /* now add a formal argument and try to set system again.  Should
         * succeed.
         */
        if ( failures == 0 )
        {
            boolean appendFormalArgReturned = false;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB()));
                appendFormalArgReturned = true;
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }


            if ( ( ! appendFormalArgReturned ) ||
                 ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! appendFormalArgReturned )
                    {
                        outStream.print(
                            "\"arg.apendFormalArg()\" failed to return.\n");
                    }

                    if ( ! methodReturned )
                    {
                        outStream.print(
                            "\"arg.setSystem()\" failed to return.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                             "\"arg.apendFormalArg()\" or \"arg.setSystem()\"" +
                             " threw a SystemErrorException: \"%s\"\n",
                             systemErrorExceptionString);
                    }
                }
            }


            if ( ve.getSystem() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected value of system(3): %b.\n",
                                       ve.getSystem());
                }
            }
        }

        return failures;

    } /* FormalArgument::TestAccessors() */

    /**
     * TestfArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods
     * defined in this class using the instance of some subclass of
     * VocabElement supplied in the argument list.
     *
     * This method is intended to be called in the test code of subclasses
     * of VocabElement, and thus just returns the number of failures
     * unless the verbose parameter is true.
     *
     * While fArgLisToDBString(), fArgListToString(), and getNumFormalArgs()
     * are not tested systematically, they are used heavily it the tests for
     * the other formal argument list management routines.  Thus further
     * testing of these routines is probably not necessary.
     *
     * Note that the method doesn't leave the supplied instance of some
     * subclass of VocabElement in the same condition it found
     * it in, so it is probably best to discard the instance on return.
     *
     *                                          JRM -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int TestfArgListManagement(VocabElement ve,
                                            java.io.PrintStream outStream,
                                            boolean verbose)
        throws SystemErrorException
    {
        final String mName = "VocabElement::TestfArgListManagement(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        FormalArgument arg0 = null;
        FormalArgument arg1 = null;
        FormalArgument arg2 = null;
        FormalArgument arg3 = null;
        FormalArgument arg4 = null;
        FormalArgument arg5 = null;
        FormalArgument arg6 = null;
        FormalArgument testArg0 = null;
        FormalArgument testArg1 = null;
        FormalArgument testArg2 = null;
        FormalArgument testArg3 = null;
        FormalArgument testArg4 = null;
        FormalArgument testArg5 = null;
        FormalArgument testArg6 = null;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ! ( ve instanceof VocabElement ) )
        {
            outStream.print(mName +
                    "ve not instanceof AbstractFormalArgument.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( ve.fArgList.size() != 0 )
        {
            outStream.print(mName + "ve.fArgList.size() != 0 on entry.\n");

            throw new SystemErrorException(
                    mName + "ve.fArgList not empty on entry.");
        }

        failures = VerifyfNumFormalArgs(ve, 0, outStream, verbose, 0);


        /********************************/
        /*** testing AppendFormlArg() ***/
        /********************************/

        /* append several formal arguments and verify that they made it
         * into the formal argument list correctly.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB(), "<alpha>"));
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<bravo>"));
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<charlie>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to fArgListString " +
                                "failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(1): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    2);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 2);
        }

        /* Try to append a null formal argument.  Should fail with a system
         * error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                UnTypedFormalArg nullArg = null;
                ve.appendFormalArg(nullArg);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg(null) returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg(null) failed to "+
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    4);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 4);
         }


        /* Now set the system flag and verify that attempting to append a
         * valid formal argument will thow a system error.  Set the system
         * flag directly instead of using setSystem().
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(),
                                                            "<delta>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "appendFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg() with system set "+
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    6);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 6);
        }


        /* Now attempt to append a valid formal argument whose name is the
         * same as an existing formal argument.  This should throw a system
         * error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(),
                                                            "<alpha>"));
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                            "appendFormalArg() with dup fArgName returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("appendFormalArg() with dup fArgName" +
                                " failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <bravo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    8);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 8);
        }


        /********************************/
        /*** testing InsertFormlArg() ***/
        /********************************/

        /* Insert a bunch of new formal arguments, and verify that the
         * insertions actually took place.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new TimeStampFormalArg(ve.getDB(), "<delta>"),
                                   0);
                ve.insertFormalArg(new QuoteStringFormalArg(ve.getDB(), "<echo>"),
                                   2);
                ve.insertFormalArg(new NominalFormalArg(ve.getDB(), "<foxtrot>"),
                                   5);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "insertFormalArg() failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(9): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    10);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 10);
        }

        /* there are lots of ways of getting insertFormalArg() to throw
         * a system error.  Work through them one by one.
         *
         * Start with sending it a null formal argument.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                UnTypedFormalArg nullArg = null;
                ve.insertFormalArg(nullArg, 2);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg(null) returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg(null) failed to "+
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    12);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 12);
        }


        /* Now set the system flag and verify that attempting to insert a
         * valid formal argument will thow a system error.  Set the system
         * flag directly instead of using setSystem().
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   2);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "insertFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with system set "+
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    14);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 14);
        }


        /* Next, try to insert an valid formal argument with a negative target
         * index.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   -1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                           "insertFormalArg() with negative index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with negative " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    16);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 16);
        }

        /* Next, try to insert an valid formal argument with a target
         * index that doesn't exist.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<golf>"),
                                   7);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with " +
                                "non-existant index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with non-existant " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    18);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 18);
        }


        /* Next, try to insert an valid formal argument with a formal argument
         * name that already appears in the formal argument list.  Should fail.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.insertFormalArg(new UnTypedFormalArg(ve.getDB(), "<alpha>"),
                                   1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with " +
                                "duplicate fArgName returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("insertFormalArg() with duplicate " +
                            "fArgName failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<delta>, <alpha>, <echo>, <bravo>, <charlie>, <foxtrot>)",
                    null,
                    outStream,
                    verbose,
                    20);

            failures += VerifyfNumFormalArgs(ve, 6, outStream, verbose, 20);
        }



        /********************************/
        /*** testing DeleteFormlArg() ***/
        /********************************/

        /* We have inserted a bunch of entries in the formal argument list.
         * Now lets delete some and verify that we get the expected result.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(5);
                ve.deleteFormalArg(3);
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "deleteFormalArg() failed to complete.\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(21): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    22);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 22);
        }


        /* deleteFormalArg() should fail with a system error if n is negative,
         * if the target entry doesn't exist, or if the system flag is set.
         * Verify this.
         */

        /* try to delete a formal argument when system is set.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            ve.system = true;

            try
            {
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            ve.system = false;

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "deleteFormalArg() with system set returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with system set " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    24);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 24);
        }


        /* try to delete a formal argument with negative index.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(-1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                            "deleteFormalArg() with negative index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with negative index" +
                                " failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    26);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 26);
        }

        /* try to delete a formal argument that doesn't exist.  Should fail. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(3);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with non-existant " +
                                "index returned.\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("deleteFormalArg() with non-existant " +
                            "index failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the fArgList has not changed */

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<alpha>, <echo>, <charlie>)",
                    null,
                    outStream,
                    verbose,
                    28);

            failures += VerifyfNumFormalArgs(ve, 3, outStream, verbose, 28);
        }

        /* Delete all arguments in prep for next test. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.deleteFormalArg(0);
                ve.deleteFormalArg(0);
                ve.deleteFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to " +
                                "deleteFormalArg() failed to complete(2).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(29): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "()",
                    null,
                    outStream,
                    verbose,
                    30);

            failures += VerifyfNumFormalArgs(ve, 0, outStream, verbose, 30);
        }


        /******************************/
        /*** testing getFormalArg() ***/
        /******************************/

        /* Start by setting up a formal argument list for us to test on. */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg0 = new UnTypedFormalArg(ve.getDB(), "<hotel>");
                ve.insertFormalArg(arg0, 0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to setup " +
                                "getFormalArg() tests failed to complete(31).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(31): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    32);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 32);
        }

        /* get the first (and only) formal argument, and verify that it is
         * a copy of arg0.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg0 = ve.getFormalArg(0);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "getFormalArg(0) failed to return(33).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(33): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }

            if ( ( testArg0 != arg0 ) ||
                 ( ! ( testArg0 instanceof UnTypedFormalArg ) ) ||
                 ( arg0.getFargName().compareTo(testArg0.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(0) doesn't match arg0\n");
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    34);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 34);
        }


        /* Now attempt to get a formal argument with negative index.
         * Should fail with a systme error.
         */
        if ( failures == 0 )
        {
            FormalArgument testArg = null;

            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg = ve.getFormalArg(-1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException) )
            {
                if ( methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print(
                                "getFormalArg(-1) returned(35).\n");
                    }
                }

                if ( ! threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg(-1) failed to throw " +
                            "a SystemErrorException.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    36);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 36);
        }


        /* Now attempt to get a formal argument that doesn't exist.
         * Should return null.
         */
        if ( failures == 0 )
        {
            FormalArgument testArg = null;

            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                testArg = ve.getFormalArg(1);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( testArg!= null ) ||
                 ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg() for non-existant " +
                                "entry failed to return(37).\n");
                    }
                }

                if ( testArg != null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("getFormalArg() for non-existant " +
                                "entry didn't return null(37).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(37): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>)",
                    null,
                    outStream,
                    verbose,
                    38);

            failures += VerifyfNumFormalArgs(ve, 1, outStream, verbose, 38);
        }

        /* finally, add entries of all available types, get them, and then
         * verify that the opbjects returned are not identical to the objects
         * inserted, and contain the same data.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                // arg0 = new UnTypedFormalArg("<hotel>");
                arg1 = new IntFormalArg(ve.getDB(), "<india>");
                arg2 = new FloatFormalArg(ve.getDB(), "<juno>");
                arg3 = new NominalFormalArg(ve.getDB(), "<kilo>");
                arg4 = new TimeStampFormalArg(ve.getDB(), "<lima>");
                arg5 = new QuoteStringFormalArg(ve.getDB(), "<mike>");
                arg6 = new PredFormalArg(ve.getDB(), "<nero>");
                ve.insertFormalArg(arg1, 1);
                ve.insertFormalArg(arg2, 2);
                ve.insertFormalArg(arg3, 3);
                ve.insertFormalArg(arg4, 4);
                ve.insertFormalArg(arg5, 5);
                ve.insertFormalArg(arg6, 6);
                testArg0 = ve.getFormalArg(0);
                testArg1 = ve.getFormalArg(1);
                testArg2 = ve.getFormalArg(2);
                testArg3 = ve.getFormalArg(3);
                testArg4 = ve.getFormalArg(4);
                testArg5 = ve.getFormalArg(5);
                testArg6 = ve.getFormalArg(6);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to setup & run " +
                                "getFormalArg() tests failed to complete(39).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(39): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }

            if ( ( testArg0 != arg0 ) ||
                 ( ! ( testArg0 instanceof UnTypedFormalArg ) ) ||
                 ( arg0.getFargName().compareTo(testArg0.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(0) doesn't match arg0(2)\n");
                }
            }

            if ( ( testArg1 != arg1 ) ||
                 ( ! ( testArg1 instanceof IntFormalArg ) ) ||
                 ( arg1.getFargName().compareTo(testArg1.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(1) doesn't match arg1\n");
                }
            }

            if ( ( testArg2 != arg2 ) ||
                 ( ! ( testArg2 instanceof FloatFormalArg ) ) ||
                 ( arg2.getFargName().compareTo(testArg2.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(2) doesn't match arg2\n");
                }
            }

            if ( ( testArg3 != arg3 ) ||
                 ( ! ( testArg3 instanceof NominalFormalArg ) ) ||
                 ( arg3.getFargName().compareTo(testArg3.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(3) doesn't match arg3\n");
                }
            }

            if ( ( testArg4 != arg4 ) ||
                 ( ! ( testArg4 instanceof TimeStampFormalArg ) ) ||
                 ( arg4.getFargName().compareTo(testArg4.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(4) doesn't match arg4\n");
                }
            }

            if ( ( testArg5 != arg5 ) ||
                 ( ! ( testArg5 instanceof QuoteStringFormalArg ) ) ||
                 ( arg5.getFargName().compareTo(testArg5.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(5) doesn't match arg5\n");
                }
            }

            if ( ( testArg6 != arg6 ) ||
                 ( ! ( testArg6 instanceof PredFormalArg ) ) ||
                 ( arg6.getFargName().compareTo(testArg6.getFargName())
                   != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("getFormalArg(6) doesn't match arg6\n");
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<hotel>, <india>, <juno>, <kilo>, <lima>, <mike>, <nero>)",
                    null,
                    outStream,
                    verbose,
                    40);

            failures += VerifyfNumFormalArgs(ve, 7, outStream, verbose, 40);
        }


        /**********************************/
        /*** testing replaceFormalArg() ***/
        /**********************************/

        /* replaceFormalArg() is implemented very simply with one call each to
         * removeFormalArg() and insertFormalArg().  As we have already tested
         * those routines, our testing here can be cursory.
         *
         * If the implementation of replaceFormalArg() is ever reworked
         * significantly, this decision should be revisited.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve.replaceFormalArg((FormalArgument)
                            (new IntFormalArg(ve.getDB(), "<oscar>")), 0);
                ve.replaceFormalArg((FormalArgument)
                            (new UnTypedFormalArg(ve.getDB(), "<papa>")), 2);
                ve.replaceFormalArg((FormalArgument)
                             (new UnTypedFormalArg(ve.getDB(), "<quebec>")), 5);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException) )
            {
                if ( ! methodReturned )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("Sequence of calls to test " +
                                "replaceFormalArg() failed to complete(41).\n");
                    }
                }

                if ( threwSystemErrorException )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                            "Unexpected SystemErrorException(41): \"%s\"\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyfArgListContents(ve,
                    "(<oscar>, <india>, <papa>, <kilo>, <lima>, <quebec>, <nero>)",
                    null,
                    outStream,
                    verbose,
                    42);

            failures += VerifyfNumFormalArgs(ve, 7, outStream, verbose, 42);
        }

        return failures;

    } /* VocabElement::TestfArgListManagement() */


    /**
     * VerifyfArgListContents()
     *
     * Verify the contents of the formal argument list by running
     * fArgListToString() and fArgListToDBString() and comparing the output
     * with the supplied strings.  If discrepencies are found, increment
     * the failure count, and (if verbose is true) generate a diagnostic
     * message
     *
     *                                          JRM -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int VerifyfArgListContents(VocabElement ve,
                                             String expectedString,
                                             String expectedDBString,
                                             java.io.PrintStream outStream,
                                             boolean verbose,
                                             int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabElement::VerifyfArgListContents(): ";
        String fArgListString = null;
        String fArgListDBString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ! ( ve instanceof VocabElement ) )
        {
            outStream.print(mName +
                    "ve not instanceof AbstractFormalArgument.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( ( expectedString == null ) && ( expectedDBString == null ) )
        {
            outStream.print(mName + "both expected strings null on entry.\n");

            throw new SystemErrorException(
                    mName + "both expected strings null on entry.");
        }

        try
        {
            fArgListString = ve.fArgListToString();
            fArgListDBString = ve.fArgListToDBString();
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }
        if ( ( fArgListString == null ) ||
             ( fArgListDBString == null ) ||
             ( threwSystemErrorException) )
        {
            if ( fArgListString == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fArgListString is null(%d)\n", testNum);
                }
            }

            if ( fArgListDBString == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fArgListDBString is null(%d)\n", testNum);
                }
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected SystemErrorException(%d): \"%s\"\n",
                        testNum, systemErrorExceptionString);
                }
            }
        }

        if ( ( expectedString != null ) &&
             ( fArgListString.compareTo(expectedString) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "Unexpected fArgListString(%d): \"%s\"\n",
                        testNum, fArgListString);
            }
        }

        if ( ( expectedDBString != null ) &&
             ( fArgListDBString.compareTo(expectedDBString) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "Unexpected fArgListDBString(%d): \"%s\"\n",
                        testNum, fArgListDBString);
            }
        }

        return failures;

    } /* VocabElement::VerifyfArgListContents() */


    /**
     * VerifyfNumFormalArgs()
     *
     * Verify the number of entries in the formal argument list by
     * running getNumFormalArgs() and conparing the result with the expected
     * value.  If a discrepency is found, increment the failure count, and
     * (if verbose is true) generate a diagnostic message
     *
     *                                          JRM -- 3/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static int VerifyfNumFormalArgs(VocabElement ve,
                                           int expectedNumFormalArgs,
                                           java.io.PrintStream outStream,
                                           boolean verbose,
                                           int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabElement::VerifyfNumFormalArgs(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int numArgs = 0;

        if ( ve == null )
        {
            outStream.print(mName + "ve null on entry.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ! ( ve instanceof VocabElement ) )
        {
            outStream.print(mName +
                    "ve not instanceof AbstractFormalArgument.\n");

            throw new SystemErrorException(mName + "ve null on entry.");
        }
        else if ( ve.fArgList == null )
        {
            outStream.print(mName + "ve.fArgList null on entry.\n");

            throw new SystemErrorException(mName + "ve.fArgList null on entry.");
        }
        else if ( expectedNumFormalArgs < 0 )
        {
            outStream.print(mName + "expectedNumFormalArgs < 0 ?!?\n");

            throw new SystemErrorException(
                    mName + "negative expected number of formal arguments.");
        }

        try
        {
            numArgs = ve.getNumFormalArgs();
        }

       catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( numArgs != expectedNumFormalArgs ) ||
             ( threwSystemErrorException) )
        {
            if ( numArgs != expectedNumFormalArgs )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected numFormalArgs(%d): %d (%d)\n",
                            testNum, numArgs, expectedNumFormalArgs);
                }
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected SystemErrorException(%d): \"%s\"\n",
                        testNum, systemErrorExceptionString);
                }
            }
        }

        return failures;

    } /* VocabElement::VerifyNumFormalArgs() */

} /* class VocabElement */
