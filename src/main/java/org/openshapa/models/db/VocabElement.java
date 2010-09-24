package org.openshapa.models.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.Vector;

/**
 * This abstract class contains functionality that is common to matrix and
 * predicate vocabulary elements.
 */
public abstract class VocabElement extends DBElement {

    /**
     * String containing the name of the vocab element.
     *
     * For matricies, this will be the name of the column variable which defines
     * them. For predicates, this will be the predicate name.
     */
    protected String name = "";

    /**
     * Boolean field indicating whether this vocab element is directly visible
     * to and editable by the user.
     *
     * Examples of vocab elements that are visible to the user include user
     * defined matrix variables and user defined predicates.
     *
     * Examples of vocab elements that are not directly visible to the user
     * include system defined predicates, and single element matricies used to
     * implement integer, float, nominal, and text column variables.
     */
    protected boolean system = false;

    /**
     * Boolean field used to indicate whether the vocab element's vocab list is
     * variable length. System maticies will always be fixed length, but system
     * predicates may be variable length.
     */
    protected boolean varLen = false;

    /**
     * Vector containing the formal argument list of the vocabulary element.
     * Once a vocab element has been fully constructed, this list must contain
     * at least one argument.
     */
    protected Vector<FormalArgument> fArgList =
            new Vector<FormalArgument>();

    /**
     * Instance of VocabElementListeners containing references to internal and
     * external objects that must be notified when the vocabulary element is
     * modified.
     */
    protected VocabElementListeners listeners = null;

    /**
     * Constructor.
     *
     * @param db The database that this vocab element belongs too.
     *
     * @throws SystemErrorException If unable to create the Vocab Element.
     *
     * @date 2007/04/30
     */
    public VocabElement(Database db)
        throws SystemErrorException
    {
        super(db);
    }

    /**
     * Constructor.
     *
     * @param db The database that this vocab element belongs too.
     * @param name The name of the vocab element.
     *
     * @throws SystemErrorException If unable to create the Vocab Element.
     *
     * @date 2007/04/30
     */
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

    }

    /**
     * Copy Constructor.
     *
     * @param ve The vocab element to create a copy of.
     *
     * @throws SystemErrorException If unable to create the Vocab Element.
     *
     * @date 2007/04/30
     */
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

    }

    /**
     * Subclasses must define this method, which must return true if the
     * given VocabElement is in form suitable for insertion in the vocab list,
     * and false if it isn't.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param newVE True if the vocab element is new (about to be inserted into
     * the vocab list), false otherwise.
     *
     * @return True if the vocab element is well formed, false otherwise.
     *
     * @throws SystemErrorException If unable to determine if the vocab element
     * is well formed.
     *
     * @date 2007/06/19
     */
    abstract public boolean isWellFormed(boolean newVE)
        throws SystemErrorException;


    /**
     * This prepares the vocab element for removal from the database, when
     * deleting vocab elements, some types require the removal of additional
     * data (columns, cells, etc) to ensure that the database does not become
     * corrupted.
     *
     * @throws SystemErrorException If unable to prepare for removal.
     */
    abstract public void prepareForRemoval() throws SystemErrorException;


    /**
     * Gets the name of the vocab element.
     * getName() & setName()
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return A copy of the name of the vocab element.
     *
     * @date 2007/02/14
     */
    public String getName()
    {

        return (new String(this.name));

    }

    /**
     * Sets the name of the vocab element.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param name The new name to use for the vocab element.
     *
     * @throws SystemErrorException If unable to set the name of the vocab
     * element.
     *
     * @date 2007/02/14
     */
    public void setName(String name)
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

    }

    /**
     * Gets the system vocab element state.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return If the vocab element is a system element or not.
     *
     * @date 2007/02/14
     */
    public boolean getSystem()
    {

        return system;

    } /* VocabElement::getSystem() */

    /**
     * Sets this vocab element to be a System vocab element.
     *
     * @throws SystemErrorException If unable to set the vocab element to be a
     * system vocab element.
     *
     * @date 2007/02/14
     */
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

    } /* VocabElement::setSystem() */

    /**
     * @return True if the Vocab Element has a varying number of formal
     * arguments.
     *
     * @date 2007/02/14
     */
    public boolean getVarLen()
    {
        return varLen;
    }

    /**
     * Sets if this vocab element has variable arguments or not.
     *
     * @param varLen True if you want this vocab element to varying arguments,
     * false otherwise.
     *
     * @throws SystemErrorException If unable to set the vocab element to have
     * variable arguments or not.
     *
     * @date 2007/02/14
     */
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

    }

    /**
     * appendFormalArg()
     *
     * Append the supplied formal argument to the end of the formal argument
     * list.
     *
     * Two versions of this method -- a public one that will always fail
     * on an attempt to modify a system predicate, and a protected one that
     * will go ahead and do the modification if the force parameter is true.
     *
     * We need the protected version, as there are some variable length
     * system predicates in MacSHAPA database, and we must be able to add
     * parameters to them.
     *
     * @param newArg The formal argument to append to the list of formal
     * arguments used for this VocabElement.
     *
     * @throws SystemErrorException When unable to add the argument to the list
     * of formal arguments.
     *
     * @date 2007/02/27
     */
    public void appendFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
        
        this.appendFormalArg(newArg, false);
        
        return;
        
    } /* VocabElement::appendFormalArg(newArg) */
    
    protected void appendFormalArg(FormalArgument newArg,
                                   boolean force)
        throws SystemErrorException
    {
        final String mName = "VocabElement::appendFormalArg(): ";

        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }

        if ( ( this.system ) && ( ! force ) )
        {
            /* this is a system vocab element, and thus is read only.
             *
             * as originally envisioned, system vocab elements were completely
             * imutable -- however support for MacSHAPA databases requires
             * that we relax this on occasion.
             */
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

    }

    /**
     * Returns a copy of the n-th formal argument, or null if there is no such
     * argument.  Note that the itsVocabElementID field of the copy is reset.
     *
     * Changes:
     * <ul>
     *   <li>
     *     Added code setting the itsVocabElement field of the copy to null,
     *     and simillarly setting the itsVocabElementID field to the
     *     INVALID_ID. --2007/06/15
     *   </li>
     * </ul>
     *
     * @param n The index of the formal argument to return a copy of.
     *
     * @return A copy of the n-th formal argument, or null if no such argument
     * exists.
     *
     * @throws SystemErrorException The
     *
     * @date 2007/02/27
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

    }

    /**
     * Construct and return a vector containing a copy of the formal argument
     * list.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return A copy of the formal argument list used for this vocab element.
     *
     * @throws SystemErrorException When unable to copy the list of formal
     * arguments.
     *
     * @date 2008/02/02
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

    }

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
     * Delete the n-th formal argument from the formal argument list.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param n The nth formal argument to delete from the vocab element.
     *
     * @throws SystemErrorException If no n-th formal argument exists.
     *
     * @date 2007/02/27
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

    }

    /**
     * Construct a string containing the names of the formal arguments in a
     * format that displays the full status of the formal arguments and
     * facilitates debugging.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return A database string representation of the formal arguments of this
     * vocab element.
     *
     * @date 2007/02/27
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

    }

    /**
     * Construct a string containing the names of the formal arguments in the
     * format: (<arg0>, <arg1>, ... <argn>).
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return A string representation of the formal arguments in this vocab
     * element.
     *
     * @date 2007/02/27
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

    }

    /**
     * Scan the formal argument list, and test to see if the supplied formal
     * argument list is unique.  Return true if it is, and false otherwise.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param fArgName The name of the formal argument to see if it is unique.
     *
     * @return True if the formal argument is unique, false otherwise.
     *
     * @date 2007/03/18
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

    }

    /**
     * Returns whether or not the vocab element has any duplicate names
     */
    public boolean hasDuplicateArgNames(){
        boolean duplicates = false;

        int numArgs = fArgList.size();
        for(int i = 0; i< numArgs; i++){
            for(int j = 0; j< numArgs; j++){
                if((fArgList.get(i).getFargName().equals(fArgList.get(j).getFargName()))
                        && (i!=j)){
                    duplicates = true;
                }
            }
        }

        return duplicates;
    }

    /**
     * Returns a copy of the n-th formal argument, or null if there
     * is no such argument.
     *
     * Changes:
     * <ul>
     *   <li>
     *     Modified the method to simply return the formal argument.  This
     *     change is due to a decision to handle vocab  changes at the
     *     level of vocab elements -- 2007/04/30
     *   </li>
     * </ul>
     *
     * @param n The n-th formal argument to get a copy of.
     *
     * @return A copy of the n-th formal argument.
     *
     * @throws SystemErrorException If unable to create a copy of the n-th
     * formal argument.
     *
     * @date 2007/02/27
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

    } // VocabElement::getFormalArg()

    // getFormalArgCopy()
    /**
     * Returns a copy of the n-th formal argument, or null if there is no such
     * argument.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param n The index of the formal argument to return a copy of.
     *
     * @return A copy of the n-th formal argument, or null if no such argument
     * exists.
     *
     * @throws SystemErrorException The
     *
     * @date 2007/02/27
     */
    public FormalArgument getFormalArgCopy(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::getFormalArgCopy(): ";
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

        fArgCopy = fArg.CopyFormalArg(false, false);

        if ( fArgCopy == null )
        {
            throw new SystemErrorException(mName + "fArgcopy is null");
        }

        return fArgCopy;

    } // VocabElement::getFormalArgCopy()


    /**
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @return the number of formal arguments.
     *
     * @throws SystemErrorException If unable to get the number of formal
     * arguments use in this vocab element.
     *
     * @date 2007/03/03
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

    }

    /**
     * Insert the supplied formal argument in the n-th position in the formal
     * argument list.  If n is not zero, there must be at least n-1 formal
     * arguments in the list to begin with.  Any existing arguments with
     * index greater than or equal to n have their indicies increased by 1.     
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param newArg The new formal argument to push into the n-th position.
     * @param n The position of the formal argument to insert.
     *
     * @throws SystemErrorException If unable to insert the formal argument into
     * the Vocab Element.
     *
     * @date 2007/02/27
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

    }

     /**
      * Propagate the id assigned to the VocabElement to all current formal
      * arguments, if any.  This method should be called after the VocabElement
      * is assigned an ID and inserted into the vocab list.
      *
      * Changes:
      * <ul>
      *   <li>None.</li>
      * </ul>
      *
      * @throws SystemErrorException If unable to propgate the ID to the current
      * formal arguments.
      *
      * @date 2007/06/17
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

    }

    /**
     * If the n-th formal argument exists, replace it with the supplied
     * formal argument.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param newArg The new formal argument to use when replacing an existing
     * formal argument.
     * @param n The index of the formal argument to be replaced.
     *
     * @throws SystemErrorException If there is no n-th formal argument to begin
     * with.
     *
     * @date 2007/02/27
     */
    public void replaceFormalArg(FormalArgument newArg, int n)
    throws SystemErrorException {
        deleteFormalArg(n);
        insertFormalArg(newArg, n);

        return;
    }

    /**
     * Deregisters the supplied external vocab element listener.
     *
     * Otherwise, pass the deregister external change listeners message on to
     * the instance of VocabElementListeners pointed to by this.listeners.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param el The ExternalVocabElementListener to deregister with this vocab
     * element.
     *
     * @throws SystemErrorException When unable to deregister the supplied vocab
     * element listener.
     *
     * @date 2007/02/05
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

    }

    /**
     * Deregisters an internal vocab element listener.
     *
     * Otherwise, pass the deregister internal change listeners message on to
     * the instance of VocabElementListeners pointed to by this.listeners.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param id The id of the internal vocab element listener to deregister.
     *
     * @throws SystemErrorException  When unable to degregister the supplied
     * internal vocab element listener.
     *
     * @date 2008/02/05
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

    }

    /**
     * @return the corrent value of this.listeners.
     *
     * @date 2008/02/05
     */
    protected VocabElementListeners getListeners()
    {

        return this.listeners;

    }

    /**
     * Otherwise, pass a note changes message on to the instance of
     * VocabElementListeners pointed to by this.listeners.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param oldVE The old vocab element that we are comparing for changes.
     * @param newVE The new vocab element that we are comparing for changes.
     *
     * @throws SystemErrorException If unable to note the changes between the
     * two supplied vocab elements.
     *
     * @date 2008/02/05
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

    }

    /**
     * This method notifies all listeners to this VocabElement of all changes
     * made to this vocabElement.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @throws SystemErrorException if unable to notify listeners of changes to
     * the vocab element.
     *
     * @date 2008/02/05
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

    } // VocabElement::notifyListenersOfChange()

    /**
     * Notify all listeners of deletion message on to the instance of
     * VocabElementListeners pointed to by this.listeners.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @throws SystemErrorException If unable to notify listeners of deletion.
     *
     * @date 2008/02/05
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
    }

    /**
     * Registers a new external listener with this VocabElement.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param el The next external listener to register with this Vocab Element.
     *
     * @throws SystemErrorException If unable to register an external listener
     * with this VocabElement.
     *
     * @date 2008/02/05
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
    }

    /**
     * Registers a new internal listener with this VocabElement.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param id The Id of the internal listener to register with this
     * VocabElement.
     *
     * @throws SystemErrorException If unable to register an internal listener
     * with this VocabElement.
     *
     * @date 2008/02/05
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

    }

    /**
     * Set the listeners field.  Setting this.listeners to a non-null value
     * signifies that this instance of VocabElement is the cannonical current
     * incarnation of the vocab element.  Setting it back to null indicates
     * that the incarnation has been superceeded.
     *
     * If this.listeners is null, it may be set to reference an instance
     * of VocabElementListeners that is associated with this vocab element.  If
     * this.listeners is not null, the only permissiable new value is null.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param listeners The new list of listeners to use with this VocabElement.
     *
     * @throws SystemErrorException If unable to set the listeners to use for
     * this VocabElement.
     *
     * @date 2008/02/05
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

    }

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
