/*
 * FormalArgument.java
 *
 * Created on January 18, 2007, 4:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Abstract class for formal arguments.
 */

package au.com.nicta.openshapa.db;

/**
 *
 * @author mainzer
 */
public abstract class FormalArgument
        extends DBElement
{

    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/

    /**
     * fArgType:  Enumerated type used to specify the type of a formal
     *      argument. The set of possible types should match the set of
     *      formal argument types.
     *
     *      This type is not used in FormalArgument and its subclasses,
     *      but rather by DataValue and its subclasses so as to allow them
     *      to track the types of the formal arguments they instantiate.
     *
     *                                          JRM -- 7/21/07
     */

    public enum FArgType {UNDEFINED,
                          COL_PREDICATE,
                          INTEGER,
                          FLOAT,
                          NOMINAL,
                          PREDICATE,
                          QUOTE_STRING,
                          TIME_STAMP,
                          TEXT,
                          UNTYPED};

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     * fargName:    The name of the formal argument.  If the argument appears
     *      in a matrix or predicate argument formal argument list, this is
     *      the sting that will be displayed in the editor whenever the
     *      formal argument is undefined.
     *
     * fargType:    Type of the formal argument.  This field exists as a
     *      convenience to other classes, and is never used by FormalArgument
     *      or any of its subclasses.
     *
     * itsVocabElement: Reference to the vocab element with which this formal
     *      argument is associated.  If there is no such vocab element, the
     *      field is null;
     *
     * itsVocabElementID: ID assigned to the vocab element with which this
     *      formal argument is associated.  If there is no such vocab element,
     *      the field is set to DBIndex.INVALID_ID.
     *
     * hidden:  Boolean flag indicating whether this formal argument should
     *      be hidden on the spreadsheet.
     */

    /** The formal argument name */
    protected String fargName = "<val>";

    /** type code associated with the formal argument */
    protected FArgType fargType = FArgType.UNDEFINED;

    /** The associated vocab element, if any */
    protected VocabElement itsVocabElement = null;

    /** The id of the associated vocab element, if any */
    protected long itsVocabElementID = DBIndex.INVALID_ID;

    /** Whether or not the formal argument is hidden on the spreadsheet */
    protected boolean hidden = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     *
     * FormalArgument()
     *
     * Constructor for instances of FormalArgument.
     *
     * Three versions of this constructor -- one which sets the name of the
     * of the new formal argument, one which doesn't, and one which creates
     * a copy of the instance of FormalArgument provided as a
     * parameter.
     *
     * Note that in both cases must update the undo list to reflect the
     * fact that this formal argument has just been created.
     *
     *                                              JRM -- 1/24/07
     *
     * Changes:
     *
     *    - None.
     */

   public FormalArgument(Database db)
        throws SystemErrorException
   {
        super(db);

        /* TODO:  must add undo support */

    } /* FormalArgument() */

    public FormalArgument(Database db,
                          String name)
        throws SystemErrorException
    {

        super(db);

        final String mName = "AbstractFormalArgument::AbstractFormalArgument(): ";

        if ( Database.IsValidFargName(name) ) {

            this.fargName = (new String(name));

        } else {

            throw new SystemErrorException(mName + "Invalid farg name " + name);
        }

        /* TODO:  Must add undo support */

    } /* FormalArgument() */

    public FormalArgument(FormalArgument fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.setFargName(fArg.getFargName());
        this.setHidden(fArg.getHidden());
        this.itsVocabElement = fArg.getItsVocabElement();
        this.itsVocabElementID = fArg.getItsVocabElementID();

    } /* FormalArgument() */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getFargName() & setFargName()
     *
     * Accessor methods for the fargName field.
     *
     *                              JRM -- 1/24/06
     *
     * Changes:
     *
     *    - None.
     *
     */

    public String getFargName()
    {

        return (new String(this.fargName));

    } /* getFargName() */


    public void setFargName(String name)

        throws SystemErrorException
    {
        final String mName = "AbstractFormalArgument::setFargName(): ";

        if ( Database.IsValidFargName(name) ) {

            // TODO:
            // copy current value of this.fargName to undo queue


            // keep a copy of the new formal argument name
            this.fargName = (new String(name));

        } else {

            throw new SystemErrorException(mName + "Invalid farg name " + name);
        }

        return;

    } /* FormalArgument::setFargName() */


    /**
     * getFargType()
     *
     * Return the type of the formal agrument.  There is not setFargType, as
     * fargType is set by the constructor, and is not changed thereafter.
     *
     *                                              JRM -- 8/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public FArgType getFargType()
        throws SystemErrorException
    {
        final String mName = "AbstractFormalArgument::getFargType(): ";

        if ( this.fargType == FArgType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "fargName not initialized?");
        }

        return this.fargType;

    } /* FormalArgument::getFargType() */


    /**
     * getHidden() & setHidden()
     *
     * Accessor methods for the hidden field.
     *
     *                              JRM -- 2/14/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
        throws SystemErrorException
    {
        final String mName = "AbstractFormalArgument::setHidden(): ";

        if ( ( hidden != true ) && ( hidden != false ) )
        {
            /* This shouldn't be able to happen, but lets check regardless */
            throw new SystemErrorException(mName + "hidden out of range.");
        }

        this.hidden = hidden;

        return;

    } /* FormalArgument::setHidden() */


    /**
     * getItsVocabElement() & setItsVocabElement()
     *
     * Accessor methods for the itsVocabElement field.
     *
     *                              JRM -- 3/1/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public VocabElement getItsVocabElement()
    {
        return itsVocabElement;
    }

    public void setItsVocabElement(VocabElement vocabElement)
        throws SystemErrorException
    {
        this.itsVocabElement = vocabElement;

        return;

    } /* FormalArgument::setItsVocabElement() */


    /**
     * getItsVocabElementID() & setItsVocabElementID()
     *
     * Accessor methods for the itsVocabElement field.
     *
     *                              JRM -- 6/141/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public long getItsVocabElementID()
    {
        return this.itsVocabElementID;
    }

    public void setItsVocabElementID(long vocabElementID)
        throws SystemErrorException
    {
        final String mName = "FormalArgument::setItsVocabElementID(): ";

        if ( ( vocabElementID != DBIndex.INVALID_ID ) &&
             ( ( this.getDB() == null ) ||
               ( ! this.getDB().vl.inVocabList(vocabElementID) ) ) )
        {
            throw new SystemErrorException(mName + "bad vocabElementID.");
        }

        this.itsVocabElementID = vocabElementID;

        return;

    } /* FormalArgument::setItsVocabElementID() */



    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * toString() -- Override of abstract method in DataValue
     *
     * Returns a String representation of the DBValue for display.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toString() {

        return (getFargName());

    } /* toString() */

    /*************************************************************************/
    /******************* Abstract Method Declarations: ***********************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()
     *
     * Subclasses must define this method, which must return a DataValue of
     * the appropriate type to fill the position of the formal argument in
     * an associated argument list.
     *
     * If the supplied DataValue is of an appropriate type, use it to
     * initialize the newly created DataValue.  Otherwise, initialize the
     * new DataValue with an appropriate default value.
     *
     * Changes:
     *
     *    - None.
     */

    abstract DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException;


    /**
     * constructEmptyArg()
     *
     * Subclasses must define this method, which must return a DataValue
     * of the appropriate type to fill the position of the formal argument
     * in an associated argument list.  The data value must be initialize
     * with an appropriate default value.
     *
     * Changes:
     *
     *    - None.
     */

    abstract public DataValue constructEmptyArg()
        throws SystemErrorException;

    /**
     * isValidValue()
     *
     * Subclasses must define this method, which given a potential value for
     * the formal argument, will return true if the value can be used to
     * replace the formal argument, and false otherwise.
     *
     * Changes:
     *
     *    - None.
     *
     */

    abstract public boolean isValidValue(Object obj)
        throws SystemErrorException;


    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison against
     * the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     * @return the string value.
     */

    public abstract String toDBString();

    /**
     * CopyFormalArg()
     *
     * Construct a copy of the supplied formal argument, and return a reference
     * to the copy.  If the resetID parameter is set, set the ID of the copy
     * to the invalid ID.  If the resetItsVE parameter is set, set the
     * itsVE and itsVEID fields to null and the INVALID_ID respectively.
     *
     * Note that the supplied formal arg must be of defined type.
     *
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */
    protected FormalArgument CopyFormalArg(boolean resetID, boolean resetItsVE)
    throws SystemErrorException {

        try {
            FormalArgument fa_copy = (FormalArgument) this.clone();

            if (resetID) {
                fa_copy.clearID();
            }

            if (resetItsVE) {
                fa_copy.setItsVocabElement(null);
                fa_copy.setItsVocabElementID(DBIndex.INVALID_ID);
            }

            return fa_copy;
        } catch (CloneNotSupportedException e) {
            throw new SystemErrorException("Unable to clone formal arg");
        }
               
    } /* FormalArgument::CopyFormalArg() */

    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * FANameChanged()
     *
     * Test to see if two incarnations of the same formal argument (i.e.
     * two instances of formal argument with the same id), have different
     * names.  Return true if there is a difference, and false otherwise.
     *
     * Note that the method will throw a system error if the two instances
     * of FormalArgument are not incarnations of the same formal argument.
     *
     *                                          JRM -- 2/4/08
     *
     * Changes;
     *
     *    - none.
     */

    protected static boolean FANameChanged(FormalArgument fa0,
                                           FormalArgument fa1)
        throws SystemErrorException
    {
        final String mName = "FormalArgument::FANameChanged()";
        boolean nameChanged = false;

        if ( ( fa0 == null ) || ( fa1 == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": fa0 or fa1 null on entry.");
        }
        else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName +
                                           ": vocab element ID mismatch.");
        }
        else if ( fa0.getFargType() != fa1.getFargType() )
        {
            throw new SystemErrorException(mName +
                                           ": fa type mismatch.");
        }
        else if ( fa0.getFargName().compareTo(fa1.getFargName()) != 0 )
        {
            nameChanged = true;
        }

        return nameChanged;

    } // FormalArgument::FANameChanged()

    /**
     * FARangeChanged()
     *
     * Test to see if two incarnations of the same formal argument (i.e.
     * two instances of formal argument with the same id), have different
     * ranges.  Return true if there is a difference,  and false otherwise.
     *
     * Note that the method will throw a system error if the two instances
     * of FormalArgument are not incarnations of the same formal argument.
     *
     *                                          JRM -- 2/4/08
     *
     * Changes;
     *
     *    - none.
     */

    protected static boolean FARangeChanged(FormalArgument fa0,
                                            FormalArgument fa1)
        throws SystemErrorException
    {
        final String mName = "FormalArgument::FARangeChanged()";
        boolean rangeChanged = false;

        if ( ( fa0 == null ) || ( fa1 == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": fa0 or fa1 null on entry.");
        }
        else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName +
                                           ": vocab element ID mismatch.");
        }
        else
        {
             switch ( fa0.getFargType() )
             {
                 case FLOAT:
                     FloatFormalArg ffa0, ffa1;

                     if ( ( ! ( fa0 instanceof FloatFormalArg ) ) ||
                          ( ! ( fa1 instanceof FloatFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (1).");
                     }

                     ffa0 = (FloatFormalArg)fa0;
                     ffa1 = (FloatFormalArg)fa1;

                     if ( ( ffa0.getMinVal() != ffa1.getMinVal() ) ||
                          ( ffa0.getMaxVal() != ffa1.getMaxVal() ) )
                     {
                         rangeChanged = true;
                     }
                     break;

                 case INTEGER:
                     IntFormalArg ifa0, ifa1;

                     if ( ( ! ( fa0 instanceof IntFormalArg ) ) ||
                          ( ! ( fa1 instanceof IntFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (2).");
                     }

                     ifa0 = (IntFormalArg)fa0;
                     ifa1 = (IntFormalArg)fa1;

                     if ( ( ifa0.getMinVal() != ifa1.getMinVal() ) ||
                          ( ifa0.getMaxVal() != ifa1.getMaxVal() ) )
                     {
                         rangeChanged = true;
                     }
                     break;

                 case NOMINAL:
                     NominalFormalArg nfa0, nfa1;

                     if ( ( ! ( fa0 instanceof NominalFormalArg ) ) ||
                          ( ! ( fa1 instanceof NominalFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (3).");
                     }

                     nfa0 = (NominalFormalArg)fa0;
                     nfa1 = (NominalFormalArg)fa1;

                     if ( ( nfa0.getSubRange() != nfa1.getSubRange() ) ||
                          ( ( nfa0.getSubRange() ) &&
                            ( nfa1.getSubRange() ) &&
                            ( ( ! nfa0.approvedSet.
                                  containsAll(nfa1.approvedSet) ) ||
                              ( ! nfa1.approvedSet.
                                  containsAll(nfa0.approvedSet) ) ) ) )
                     {
                         rangeChanged = true;
                     }
                     break;

                 case PREDICATE:
                     PredFormalArg pfa0, pfa1;

                     if ( ( ! ( fa0 instanceof PredFormalArg ) ) ||
                          ( ! ( fa1 instanceof PredFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (4).");
                     }

                     pfa0 = (PredFormalArg)fa0;
                     pfa1 = (PredFormalArg)fa1;

                     if ( ( pfa0.getSubRange() != pfa1.getSubRange() ) ||
                          ( ( pfa0.getSubRange() ) &&
                            ( pfa1.getSubRange() ) &&
                            ( ( ! pfa0.approvedSet.
                                  containsAll(pfa1.approvedSet) ) ||
                              ( ! pfa1.approvedSet.
                                  containsAll(pfa0.approvedSet) ) ) ) )
                     {
                         rangeChanged = true;
                     }
                     break;

                 case QUOTE_STRING:
                     if ( ( ! ( fa0 instanceof QuoteStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof QuoteStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (5).");
                     }
                     /* quote string don't support subranges at present.
                      */
                     break;

                 case TEXT:
                     if ( ( ! ( fa0 instanceof TextStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof TextStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (6).");
                     }
                     /* text strings don't support subranges at present.
                      */
                     break;

                 case TIME_STAMP:
                     TimeStampFormalArg tsfa0, tsfa1;

                     if ( ( ! ( fa0 instanceof TimeStampFormalArg ) ) ||
                          ( ! ( fa1 instanceof TimeStampFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (7).");
                     }

                     tsfa0 = (TimeStampFormalArg)fa0;
                     tsfa1 = (TimeStampFormalArg)fa1;

                     if ( ( tsfa0.getSubRange() != tsfa1.getSubRange() ) ||
                          ( ( tsfa0.getSubRange() ) &&
                            ( tsfa1.getSubRange() ) &&
                            ( ( tsfa0.getMinVal().ne(tsfa1.getMinVal()) ) ||
                              ( tsfa0.getMaxVal().ne(tsfa1.getMaxVal()) ) ) ) )
                     {
                         rangeChanged = true;
                     }
                     break;

                 case UNTYPED:
                     if ( ( ! ( fa0 instanceof UnTypedFormalArg ) ) ||
                          ( ! ( fa1 instanceof UnTypedFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (8).");
                     }
                     /* untype formal argument don't support subranges.
                      */
                     break;

                 case UNDEFINED:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of undefined type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;

                 default:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of unknown type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;
             }
        }

        return rangeChanged;

    } // FormalArgument::FARangeChanged()


    /**
     * FASubRangeChanged()
     *
     * Test to see if two incarnations of the same formal argument (i.e.
     * two instances of formal argument with the same id), have different
     * values for their subRange field.  Return true if there is a difference,
     * and false otherwise.
     *
     * Note that the method will throw a system error if the two instances
     * of FormalArgument are not incarnations of the same formal argument.
     *
     *                                          JRM -- 2/4/08
     *
     * Changes;
     *
     *    - none.
     */

    protected static boolean FASubRangeChanged(FormalArgument fa0,
                                               FormalArgument fa1)
        throws SystemErrorException
    {
        final String mName = "FormalArgument::FASubRangeChanged()";
        boolean subRangeChanged = false;

        if ( ( fa0 == null ) || ( fa1 == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": fa0 or fa1 null on entry.");
        }
        else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName +
                                           ": vocab element ID mismatch.");
        }
        else
        {
             switch ( fa0.getFargType() )
             {
                 case FLOAT:
                     FloatFormalArg ffa0, ffa1;

                     if ( ( ! ( fa0 instanceof FloatFormalArg ) ) ||
                          ( ! ( fa1 instanceof FloatFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (1).");
                     }

                     ffa0 = (FloatFormalArg)fa0;
                     ffa1 = (FloatFormalArg)fa1;

                     if ( ffa0.getSubRange() != ffa1.getSubRange() )
                     {
                         subRangeChanged = true;
                     }
                     break;

                 case INTEGER:
                     IntFormalArg ifa0, ifa1;

                     if ( ( ! ( fa0 instanceof IntFormalArg ) ) ||
                          ( ! ( fa1 instanceof IntFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (2).");
                     }

                     ifa0 = (IntFormalArg)fa0;
                     ifa1 = (IntFormalArg)fa1;

                     if ( ifa0.getSubRange() != ifa1.getSubRange() )
                     {
                         subRangeChanged = true;
                     }
                     break;

                 case NOMINAL:
                     NominalFormalArg nfa0, nfa1;

                     if ( ( ! ( fa0 instanceof NominalFormalArg ) ) ||
                          ( ! ( fa1 instanceof NominalFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (3).");
                     }

                     nfa0 = (NominalFormalArg)fa0;
                     nfa1 = (NominalFormalArg)fa1;

                     if ( nfa0.getSubRange() != nfa1.getSubRange() )
                     {
                         subRangeChanged = true;
                     }
                     break;

                 case PREDICATE:
                     PredFormalArg pfa0, pfa1;

                     if ( ( ! ( fa0 instanceof PredFormalArg ) ) ||
                          ( ! ( fa1 instanceof PredFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (4).");
                     }

                     pfa0 = (PredFormalArg)fa0;
                     pfa1 = (PredFormalArg)fa1;

                     if ( pfa0.getSubRange() != pfa1.getSubRange() )
                     {
                         subRangeChanged = true;
                     }
                     break;

                 case QUOTE_STRING:
                     if ( ( ! ( fa0 instanceof QuoteStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof QuoteStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (5).");
                     }
                     /* quote strings don't support subranges at present.
                      */
                     break;

                 case TEXT:
                     if ( ( ! ( fa0 instanceof TextStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof TextStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (6).");
                     }
                     /* text strings don't support subranges at present.
                      */
                     break;

                 case TIME_STAMP:
                     TimeStampFormalArg tsfa0, tsfa1;

                     if ( ( ! ( fa0 instanceof TimeStampFormalArg ) ) ||
                          ( ! ( fa1 instanceof TimeStampFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (7).");
                     }

                     tsfa0 = (TimeStampFormalArg)fa0;
                     tsfa1 = (TimeStampFormalArg)fa1;

                     if ( tsfa0.getSubRange() != tsfa1.getSubRange() )
                     {
                         subRangeChanged = true;
                     }
                     break;

                 case UNTYPED:
                     if ( ( ! ( fa0 instanceof UnTypedFormalArg ) ) ||
                          ( ! ( fa1 instanceof UnTypedFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (8).");
                     }
                     /* untyped formal arguments don't support subranges.
                      */
                     break;

                 case UNDEFINED:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of undefined type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;

                 default:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of unknown type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;
             }
        }

        return subRangeChanged;

    } // FormalArgument::FASubRangeChanged()

    /**
     * FormalArgsAreEqual()
     *
     * Test to see if two formal arguments are logically equal.  Return true
     * if they are, and false otherwise.
     *
     *                                                  JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

     protected static boolean FormalArgsAreEqual(FormalArgument fa0,
                                                 FormalArgument fa1)
         throws SystemErrorException
     {
         final String mName = "FormalArgument::FormalArgsAreEqual()";
         boolean argsAreEqual = true;

         if ( ( fa0 == null ) || ( fa1 == null ) )
         {
             throw new SystemErrorException(mName +
                                            ": fa0 or fa1 null on entry.");
         }
         else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
         {
             throw new SystemErrorException(mName +
                                            ": vocab element ID mismatch.");
         }

         if ( ( fa0.getID() != fa1.getID() ) ||
              ( fa0.getFargType() != fa1.getFargType() ) ||
              ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() ) ||
              ( fa0.getHidden() != fa1.getHidden() ) ||
              ( fa0.getFargName().compareTo(fa1.getFargName()) != 0 ) )
         {
             argsAreEqual = false;
         }
         else
         {
             switch ( fa0.getFargType() )
             {
                 case FLOAT:
                     FloatFormalArg ffa0, ffa1;

                     if ( ( ! ( fa0 instanceof FloatFormalArg ) ) ||
                          ( ! ( fa1 instanceof FloatFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (1).");
                     }

                     ffa0 = (FloatFormalArg)fa0;
                     ffa1 = (FloatFormalArg)fa1;

                     if ( ( ffa0.getSubRange() != ffa1.getSubRange() ) ||
                          ( ( ffa0.getSubRange() ) &&
                            ( ( ffa0.getMinVal() != ffa1.getMinVal() ) ||
                              ( ffa0.getMaxVal() != ffa1.getMaxVal() ) ) ) )
                     {
                         argsAreEqual = false;
                     }
                     break;

                 case INTEGER:
                     IntFormalArg ifa0, ifa1;

                     if ( ( ! ( fa0 instanceof IntFormalArg ) ) ||
                          ( ! ( fa1 instanceof IntFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (2).");
                     }

                     ifa0 = (IntFormalArg)fa0;
                     ifa1 = (IntFormalArg)fa1;

                     if ( ( ifa0.getSubRange() != ifa1.getSubRange() ) ||
                          ( ( ifa0.getSubRange() ) &&
                            ( ( ifa0.getMinVal() != ifa1.getMinVal() ) ||
                              ( ifa0.getMaxVal() != ifa1.getMaxVal() ) ) ) )
                     {
                         argsAreEqual = false;
                     }
                     break;

                 case NOMINAL:
                     NominalFormalArg nfa0, nfa1;

                     if ( ( ! ( fa0 instanceof NominalFormalArg ) ) ||
                          ( ! ( fa1 instanceof NominalFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (3).");
                     }

                     nfa0 = (NominalFormalArg)fa0;
                     nfa1 = (NominalFormalArg)fa1;

                     if ( ( nfa0.getSubRange() != nfa1.getSubRange() ) ||
                          ( ( nfa0.getSubRange() ) &&
                            ( nfa1.getSubRange() ) &&
                            ( ( ! nfa0.approvedSet.
                                  containsAll(nfa1.approvedSet) ) ||
                              ( ! nfa1.approvedSet.
                                  containsAll(nfa0.approvedSet) ) ) ) )
                     {
                         argsAreEqual = false;
                     }
                     break;

                 case PREDICATE:
                     PredFormalArg pfa0, pfa1;

                     if ( ( ! ( fa0 instanceof PredFormalArg ) ) ||
                          ( ! ( fa1 instanceof PredFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (4).");
                     }

                     pfa0 = (PredFormalArg)fa0;
                     pfa1 = (PredFormalArg)fa1;

                     if ( ( pfa0.getSubRange() != pfa1.getSubRange() ) ||
                          ( ( pfa0.getSubRange() ) &&
                            ( pfa1.getSubRange() ) &&
                            ( ( ! pfa0.approvedSet.
                                  containsAll(pfa1.approvedSet) ) ||
                              ( ! pfa1.approvedSet.
                                  containsAll(pfa0.approvedSet) ) ) ) )
                     {
                         argsAreEqual = false;
                     }
                     break;

                 case QUOTE_STRING:
                     if ( ( ! ( fa0 instanceof QuoteStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof QuoteStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (5).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the quote string
                      * case.
                      */
                     break;

                 case TEXT:
                     if ( ( ! ( fa0 instanceof TextStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof TextStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (6).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the test string
                      * case.
                      */
                     break;

                 case TIME_STAMP:
                     TimeStampFormalArg tsfa0, tsfa1;

                     if ( ( ! ( fa0 instanceof TimeStampFormalArg ) ) ||
                          ( ! ( fa1 instanceof TimeStampFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (7).");
                     }

                     tsfa0 = (TimeStampFormalArg)fa0;
                     tsfa1 = (TimeStampFormalArg)fa1;

                     if ( ( tsfa0.getSubRange() != tsfa1.getSubRange() ) ||
                          ( ( tsfa0.getSubRange() ) &&
                            ( ( tsfa0.getMinVal().ne(tsfa1.getMinVal()) ) ||
                              ( tsfa0.getMaxVal().ne(tsfa1.getMaxVal()) ) ) ) )
                     {
                         argsAreEqual = false;
                     }
                     break;

                 case UNTYPED:
                     if ( ( ! ( fa0 instanceof UnTypedFormalArg ) ) ||
                          ( ! ( fa1 instanceof UnTypedFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (8).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the untyped case.
                      */
                     break;

                 case UNDEFINED:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of undefined type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;

                 default:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of unknown type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;
             }
         }

         return argsAreEqual;

     } /* FormalArgument::FormalArgsAreEqual() */


    /**
     * FormalArgsAreEquivalent()
     *
     * Test to see if two formal arguments are equivalent.
     *
     * We say that two formal argument are equivalent if they are of the
     * same type, have the same subtyping restrictions, and have the same name
     * and host VocabElement.  However, their IDs must either be invalid, or
     * not equal each other.
     *
     * This method exists to facilitate sanity checking on the column predicate
     * formal argument list maintained by MatrixVocabElements so as to
     * facilitate use of the column predicate implied by matrix vocab elements.
     *
     *                                                  JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */

     protected static boolean FormalArgsAreEquivalent(FormalArgument fa0,
                                                      FormalArgument fa1)
         throws SystemErrorException
     {
         final String mName = "FormalArgument::FormalArgsAreEquivalent()";
         boolean argsAreEquivalent = true;
         boolean verbose = true;

         if ( ( fa0 == null ) || ( fa1 == null ) )
         {
             throw new SystemErrorException(mName +
                                            ": fa0 or fa1 null on entry.");

         }
         else if ( fa0 == fa1 )
         {
             argsAreEquivalent = false;
         }
         else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0.getItsVocabElementID() = %d != " +
                         "fa1.getItsVocabElementID() = %d\n",
                         mName,
                         fa0.getItsVocabElementID(),
                         fa1.getItsVocabElementID());
             }
         }
         else if ( ( fa0.getID() == fa1.getID() ) &&
                   ( fa0.getID() != DBIndex.INVALID_ID ) )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0.getID() = %d == " +
                         "fa1.getID() = %d\n",
                         mName,
                         fa0.getID(),
                         fa1.getID());
             }
         }
         else
         {
             argsAreEquivalent =
                     FormalArgument.FormalArgsAreEquivalentModuloID(fa0, fa1);
         }

         return argsAreEquivalent;

     } /* FormalArgument::FormalArgsAreEquivalent() */


    /**
     * FormalArgsAreEquivalentModuloID()
     *
     * Test to see if two formal arguments are equivalent modulo ID.
     *
     * We say that two formal argument are equivalent modulo ID if they are of
     * the same type, have the same subtyping restrictions, and have the same
     * name and host VocabElement.  However, we say nothing about their IDs.
     *
     * This method exists to facilitate sanity checking on the column predicate
     * formal argument list maintained by MatrixVocabElements so as to
     * facilitate use of the column predicate implied by matrix vocab elements.
     *
     *                                                  JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */

     protected static boolean FormalArgsAreEquivalentModuloID(FormalArgument fa0,
                                                              FormalArgument fa1)
         throws SystemErrorException
     {
         final String mName = "FormalArgument::FormalArgsAreEquivalentModulo()";
         boolean argsAreEquivalent = true;
         boolean verbose = true;

         if ( ( fa0 == null ) || ( fa1 == null ) )
         {
             throw new SystemErrorException(mName +
                                            ": fa0 or fa1 null on entry.");
         }
         else if ( fa0 == fa1 )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0 == fa1\n", mName);
             }
         }
         else if ( fa0.getItsVocabElementID() != fa1.getItsVocabElementID() )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0.getItsVocabElementID() = %d != " +
                         "fa1.getItsVocabElementID() = %d\n",
                         mName,
                         fa0.getItsVocabElementID(),
                         fa1.getItsVocabElementID());
             }
         }
         else if ( fa0.getFargName().compareTo(fa1.getFargName()) != 0 )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0.getFargName() = \"%s\" != " +
                         "fa1.getFargName() = \"%s\"\n",
                         mName,
                         fa0.getFargName(),
                         fa1.getFargName());
             }
         }
         else if ( fa0.getFargType() != fa1.getFargType() )
         {
             argsAreEquivalent = false;

             if ( verbose )
             {
                 System.out.printf("%s: fa0.getFargType() = \"%s\" != " +
                         "fa1.getFargType() = \"%s\"\n",
                         mName,
                         fa0.getFargType().toString(),
                         fa1.getFargType().toString());
             }
         }
         else
         {
             switch ( fa0.getFargType() )
             {
                 case COL_PREDICATE:
                     if ( ( ! ( fa0 instanceof ColPredFormalArg ) ) ||
                          ( ! ( fa1 instanceof ColPredFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (0).");
                     }
                     break;

                 case FLOAT:
                     FloatFormalArg ffa0, ffa1;

                     if ( ( ! ( fa0 instanceof FloatFormalArg ) ) ||
                          ( ! ( fa1 instanceof FloatFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (1).");
                     }

                     ffa0 = (FloatFormalArg)fa0;
                     ffa1 = (FloatFormalArg)fa1;

                     if ( ( ffa0.getSubRange() != ffa1.getSubRange() ) ||
                          ( ( ffa0.getSubRange() ) &&
                            ( ( ffa0.getMinVal() != ffa1.getMinVal() ) ||
                              ( ffa0.getMaxVal() != ffa1.getMaxVal() ) ) ) )
                     {
                         argsAreEquivalent = false;

                         if ( verbose )
                         {
                             System.out.printf("%s: float range mismatch\n",
                                               mName);
                         }
                     }
                     break;

                 case INTEGER:
                     IntFormalArg ifa0, ifa1;

                     if ( ( ! ( fa0 instanceof IntFormalArg ) ) ||
                          ( ! ( fa1 instanceof IntFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (2).");
                     }

                     ifa0 = (IntFormalArg)fa0;
                     ifa1 = (IntFormalArg)fa1;

                     if ( ( ifa0.getSubRange() != ifa1.getSubRange() ) ||
                          ( ( ifa0.getSubRange() ) &&
                            ( ( ifa0.getMinVal() != ifa1.getMinVal() ) ||
                              ( ifa0.getMaxVal() != ifa1.getMaxVal() ) ) ) )
                     {
                         argsAreEquivalent = false;

                         if ( verbose )
                         {
                             System.out.printf("%s: int range mismatch\n",
                                               mName);
                         }
                     }
                     break;

                 case NOMINAL:
                     NominalFormalArg nfa0, nfa1;

                     if ( ( ! ( fa0 instanceof NominalFormalArg ) ) ||
                          ( ! ( fa1 instanceof NominalFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (3).");
                     }

                     nfa0 = (NominalFormalArg)fa0;
                     nfa1 = (NominalFormalArg)fa1;

                     if ( ( nfa0.getSubRange() != nfa1.getSubRange() ) ||
                          ( ( nfa0.getSubRange() ) &&
                            ( nfa1.getSubRange() ) &&
                            ( ( ! nfa0.approvedSet.
                                  containsAll(nfa1.approvedSet) ) ||
                              ( ! nfa1.approvedSet.
                                  containsAll(nfa0.approvedSet) ) ) ) )
                     {
                         argsAreEquivalent = false;

                         if ( verbose )
                         {
                             System.out.printf("%s: nominal range mismatch\n",
                                               mName);
                         }
                     }
                     break;

                 case PREDICATE:
                     PredFormalArg pfa0, pfa1;

                     if ( ( ! ( fa0 instanceof PredFormalArg ) ) ||
                          ( ! ( fa1 instanceof PredFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (4).");
                     }

                     pfa0 = (PredFormalArg)fa0;
                     pfa1 = (PredFormalArg)fa1;

                     if ( ( pfa0.getSubRange() != pfa1.getSubRange() ) ||
                          ( ( pfa0.getSubRange() ) &&
                            ( pfa1.getSubRange() ) &&
                            ( ( ! pfa0.approvedSet.
                                  containsAll(pfa1.approvedSet) ) ||
                              ( ! pfa1.approvedSet.
                                  containsAll(pfa0.approvedSet) ) ) ) )
                     {
                         argsAreEquivalent = false;

                         if ( verbose )
                         {
                             System.out.printf("%s: pred range mismatch\n",
                                               mName);
                         }
                     }
                     break;

                 case QUOTE_STRING:
                     if ( ( ! ( fa0 instanceof QuoteStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof QuoteStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (5).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the quote string
                      * case.
                      */
                     break;

                 case TEXT:
                     if ( ( ! ( fa0 instanceof TextStringFormalArg ) ) ||
                          ( ! ( fa1 instanceof TextStringFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (6).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the test string
                      * case.
                      */
                     break;

                 case TIME_STAMP:
                     TimeStampFormalArg tsfa0, tsfa1;

                     if ( ( ! ( fa0 instanceof TimeStampFormalArg ) ) ||
                          ( ! ( fa1 instanceof TimeStampFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (7).");
                     }

                     tsfa0 = (TimeStampFormalArg)fa0;
                     tsfa1 = (TimeStampFormalArg)fa1;

                     if ( ( tsfa0.getSubRange() != tsfa1.getSubRange() ) ||
                          ( ( tsfa0.getSubRange() ) &&
                            ( ( tsfa0.getMinVal().ne(tsfa1.getMinVal()) ) ||
                              ( tsfa0.getMaxVal().ne(tsfa1.getMaxVal()) ) ) ) )
                     {
                         argsAreEquivalent = false;

                         if ( verbose )
                         {
                             System.out.printf("%s: time stamp range mismatch\n",
                                               mName);
                         }
                     }
                     break;

                 case UNTYPED:
                     if ( ( ! ( fa0 instanceof UnTypedFormalArg ) ) ||
                          ( ! ( fa1 instanceof UnTypedFormalArg ) ) )
                     {
                         throw new SystemErrorException(mName +
                                 "farg type / class mismatch (8).");
                     }
                     /* for now at least, it we haven't found a difference
                      * by now, we will not find one in the untyped case.
                      */
                     break;

                 case UNDEFINED:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of undefined type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;

                 default:
                     throw new SystemErrorException(mName +
                             "fa0 & fa1 of unknown type??");
                     /* we comment out the following break statement to
                      * keep the compiler from complaining.
                      */
                     //break;
             }
         }

         return argsAreEquivalent;

     } /* FormalArgument::FormalArgsAreEquivalentModuloID() */

} /* Class FormalArgument */
