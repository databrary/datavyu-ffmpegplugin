/*
 * DataValue.java
 *
 * Created on December 6, 2006, 12:46 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 * Class DataValue
 *
 * Instances of subclasses of the abstract class DataValue are used to 
 * store individual pieces of data in the database, or as headers of 
 * predicates.
 *
 * Each DataValue is associated with with a formal argument, and is
 * constrained to the type (if any) of that formal argument.  The DataValue
 * must listen for changes in the formal argument, and accomodate itself
 * to them. 
 *
 * Each type of data value will have fields specific to its type, but 
 * their common fields and methods are defined here.
 *
 *                                              JRM -- 7/21/07  
 *
 * @author FGA
 */
public abstract class DataValue extends DBElement
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsFargID:   Long containingthe ID of the formal argument of which this
     *      DataValue is an assignment.  In the rare case in which this 
     *      instance is not associated with any formal argument, this field
     *      must be set to DBIndex.INVALID_ID.
     *
     * itsFargType: fArgType indicating the type of the formal argument 
     *      specified in itsFargID above.  Set this field to UNDEFINED if 
     *      itsFargID is DBIndex.INVALID_ID.
     *
     * subRange:  Boolean flag set to true iff the associated formal argument
     *      is sub-ranged. (i.e. an integer restricted to the range [1, 100].
     *
     * itsCell: Reference to the instance of Cell in which this DataValue
     *      appears, or null if it is not currently associated with a cell.
     *
     * itsCellID:  Long containing the ID of the cell in which this data value
     *      appears, or DBIndex.INVALID_ID if it is not currently associated
     *      with a cell.  Note that this field exists primarily for sanity
     *      checking.
     *
     * itsPredID:  Long containing the ID of the predicate or column predicate
     *      in whose argument list this data value appears (if any).  When the 
     *      DatavValue doesn't appear in any predicate, the field is set to 
     *      DBIndex.INVALID_ID.
     */

    /** ID of associated formal argument */
    long itsFargID = DBIndex.INVALID_ID;
    
    /** type of associated formal argument */
    FormalArgument.fArgType itsFargType = FormalArgument.fArgType.UNDEFINED;
    
    /** whether the associated formal argument is subtyped */
    boolean subRange = false;
    
    /** id of the cell in which the DataValue resides */
    long itsCellID = DBIndex.INVALID_ID;
    
    /** id of the Predicate in which which the DataValue resides -- if any. */
    long itsPredID = DBIndex.INVALID_ID;
    
//    /** Data Value Change Listeners */
//    java.util.Vector<DataValueChangeListener> changeListeners = 
//            new java.util.Vector<DataValueChangeListener>();
  
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * DataValue()
     *
     * Constructor for instances of DataValue.  
     * 
     * Two versions of this constructor.  One that takes a reference to
     * a database as its parameter and just calls the super() constructor, 
     * and one that is used to create a copies of subclasses of DataValue.
     *
     *                                              JRM -- 2/28/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public DataValue(Database db)
        throws SystemErrorException
    {
        super(db);
    }
    
    public DataValue(DataValue dv)
        throws SystemErrorException
    {
        super(dv);
        
        final String mName = "DataValue::DataValue(): ";
        
        if ( ! ( dv instanceof DataValue ) )
        {
            throw new SystemErrorException(mName + 
                    "dv not an instance of DataValue.");
        }
        
        this.itsFargID = dv.itsFargID;
        this.itsFargType = dv.itsFargType;
        this.subRange = dv.subRange;
        this.itsCellID = dv.itsCellID;
        this.itsPredID = dv.itsPredID;
        
//        /* Could do this with a call to clone(), but this way shuts up 
//         * the compiler.
//         */
//        for ( int i = 0; i < dv.changeListeners.size(); i++ )
//        {
//            this.addChangeListener(dv.changeListeners.get(i));
//        }
    } /* DataValue::DataValue() */
    
        
    /*************************************************************************/
    /******************* Abstract Method Declarations: ***********************/
    /*************************************************************************/
    
    /**
     * toString()
     *
     * Returns a String representation of the DBValue for display.
     * @return the string value.
     *
     * Changes:
     *
     *     - None.
     */
    
    public abstract String toString();


    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison 
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     * @return the string value.
     */
  
    public abstract String toDBString();
    
    
    /** 
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                          JRM -- 3/22/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public abstract void updateForFargChange(boolean fargNameChanged,
                                             boolean fargSubRangeChanged,
                                             boolean fargRangeChanged,
                                             FormalArgument oldFA,
                                             FormalArgument newFA)
        throws SystemErrorException;
    
    
    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is 
     * subranged, and if it is, updates the data values representation of 
     * the subrange (if ant) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected abstract void updateSubRange(FormalArgument fa)
        throws SystemErrorException;
    
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsFargID()
     *
     * Return the ID associated with the formal argument with which this 
     * DataValue is associated.
     *                                              JRM -- 7/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public long getItsFargID()
    {
        return this.itsFargID;
    }
    
    /**
     * getItsFargType()
     *
     * Return the type of the formal argument with which this DataValue is 
     * associated.
     *
     * Note that there is no setItsFargType() method, as the itsFargType
     * field is set in passing by setItsFargID().
     *
     *                                              JRM -- 7/23/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public FormalArgument.fArgType getItsFargType()
    {
        return this.itsFargType;
    }
    
    /**
     * getSubRange()
     *
     * Return the value of the subRange flag.  Observe that there is no 
     * setSubRange() method as this field is set in passing by setItsFargID(),
     * and can only be changed by a listener call.
     *
     *                                              JRM -- 7/23/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean getSubRange()
    {
        return this.subRange;
    }
    
    
    /**
     * setItsCellID()
     *
     * Set the ID associated with the cell with which this 
     * DataValue is associated.  In passing, verify that the target 
     * cell exists.
     * 
     * If the DataValue is not associated with a predicate (i.e. itsPredID 
     * == INVALID_ID), then the MatrixVocabElement of the Column in which
     * cell appears must contain the formal argument whose ID is stored in 
     * itsFargID.
     *
     * If, on the other hand, the data value is associated with a predicate,
     * then the supplied cell ID must match the itsCellID of that predicate.
     * 
     * In either case, verify these invarients.
     *
     * Note: if the DataValue does appear in a predicate, then the 
     * PredicateVocabElement associated with the predicate must contain the 
     * formal argument whose ID is stored in itsFargID.  However, we already
     * checked this when we set itsPredID, so no need to check it again here.
     * 
     *                                              JRM -- 11/14/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setItsCellID(long ID)
        throws SystemErrorException
    {
        final String mName = "DataValue::setItsCellID(): ";
        boolean matchFound = false;
        int i;
        long mveID;
        DBElement dbe = null;
        DataCell dc = null;
        MatrixVocabElement mve = null;
        FormalArgument fa = null;
        Predicate pred = null;
        
        if ( this.itsFargID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "itsFargID INVALID on entry.");
        }
        
        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID == INVALID_ID");    
        }
        
        if ( this.itsPredID == DBIndex.INVALID_ID )
        {
            /* The data value must be a top level argument of a data cell.
             * Verify this as follows:
             *
             * 1) looking up the cell indicated by the supplied ID
             *
             * 2) look up the MatrixVocabElement associated with the cell
             *
             * 3) Scan the argument list of the mve and verify that it
             *    contains a formal argument with ID matching this.itsFargID.
             */
            dbe = this.db.idx.getElement(ID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + "ID has no referent");
            }

            if ( ! ( dbe instanceof DataCell ) )
            {
                throw new SystemErrorException(mName + 
                        "ID doesn't refer to a DataCell");
            }

            /* If we get this far, we know that dbe is a DataCell */
            dc = (DataCell)dbe;

            mveID = dc.getItsMveID();

            if ( mveID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + "mveID == INVALID_ID");    
            }

            dbe = this.db.idx.getElement(mveID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + "mveID has no referent");
            }

            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName + 
                        "mveID doesn't refer to a MatrixVocabElement");
            }

            /* If we get this far, we know that dbe is a MatrixVocabElement */
            mve = (MatrixVocabElement)dbe;

            i = 0;
            matchFound = false;
            while ( ( i < mve.getNumFormalArgs() ) && ( ! matchFound ) )
            {
                if ( mve.getFormalArg(i).getID() == itsFargID )
                {
                    matchFound = true;
                }
                i++;
            }

            if ( ! matchFound )
            {
                // todo: delete the following line eventually
                int j = 1/0;
                throw new SystemErrorException(mName + 
                        "Target cell's mve does not contain itsFarg");
            }
        }
        else /* this.itsPredID != DBIndex.INVALID_ID */
        {
            /* The data value must be a top level argument of the predicate
             * indicated by this.itsPredID.  Verify that the supplied ID 
             * matches the cell ID of the containining predicate.
             */
            dbe = this.db.idx.getElement(this.itsPredID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + 
                        "this.itsPredID has no referent");
            }

            if ( ! ( dbe instanceof Predicate ) )
            {
                throw new SystemErrorException(mName + 
                        "this.itsPredID doesn't refer to a Predicate");
            }

            /* If we get this far, we know that dbe is a Predicate */
            pred = (Predicate)dbe;
            
            if ( pred.getCellID() != ID )
            {
                throw new SystemErrorException(mName + 
                                               "ID != pred.getCellID()");
            }
        }
        
        this.itsCellID = ID;
        
        return;
        
    } /* DataValue::SetItsCellID() */
    

    /**
     * setItsFargID()
     *
     * Set the ID associated with the formal argument with which this 
     * DataValue is associated.  In passing, verify that the target 
     * formal argument exists, and set itsFargType to match the type 
     * of the formal argument.
     *                                              JRM -- 7/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setItsFargID(long ID)
        throws SystemErrorException
    {
        final String mName = "DataValue::SetItsFargID(): ";
        DBElement dbe = null;
        FormalArgument fa = null;
        FormalArgument.fArgType fargType;
        
        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID == INVALID_ID");    
        }
        
        dbe = this.db.idx.getElement(ID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "ID has no referent");
        }
        
        if ( ! ( dbe instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName + 
                    "ID doesn't refer to a formal argument");
        }
        
        /* If we get this far, we know that dbe is a FormalArgument */
        fa = (FormalArgument)dbe;
        this.subRange = false; /* will change later if appropriate */
        fargType = fa.getFargType();
        
        switch ( fargType )
        {
            case COL_PREDICATE:
            case FLOAT:
            case INTEGER:
            case NOMINAL:
            case PREDICATE:
            case TIME_STAMP:
                this.itsFargID = ID;
                this.itsFargType = fargType;
                this.updateSubRange(fa);
                break;
            
            case QUOTE_STRING:
            case TEXT:
            case UNTYPED:
                this.itsFargID = ID;
                this.itsFargType = fargType;
                break;
                
            case UNDEFINED:
                throw new SystemErrorException(mName + 
                                               "formal arg type undefined???");
                /* break statement commented out to keep the compiler happy */
                // break;
                
            default:
                throw new SystemErrorException(mName + 
                                               "Unknown Formal Arg Type");
                /* break statement commented out to keep the compiler happy */
                // break;
        }
        
        return;
        
    } /* DataValue::SetItsFargID() */
    
    
    /**
     * setItsPredID()
     *
     * Set the ID associated with the Predicate with which this 
     * DataValue is associated.  In passing, verify that the target 
     * instance of Predicate exists, and that its associated 
     * PredicateVocabElement contains the formal argument whose ID is 
     * stored in itsFargID.
     * 
     *                                              JRM -- 11/14/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setItsPredID(long ID)
        throws SystemErrorException
    {
        final String mName = "DataValue::setItsCellID(): ";
        boolean matchFound = false;
        int i;
        long pveID;
        DBElement dbe = null;
        Predicate pred = null;
        PredicateVocabElement pve = null;
        FormalArgument fa = null;
        
        if ( this.itsFargID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "itsFargID INVALID on entry.");
        }
        
        if ( ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID == INVALID_ID");    
        }
        
        dbe = this.db.idx.getElement(ID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "ID has no referent");
        }
        
        if ( ! ( dbe instanceof Predicate ) )
        {
            throw new SystemErrorException(mName + 
                    "ID doesn't refer to a Predicate");
        }
        
        /* If we get this far, we know that dbe is a DataCell */
        pred = (Predicate)dbe;
        
        pveID = pred.getPveID();
        
        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID == INVALID_ID");    
        }
        
        dbe = this.db.idx.getElement(pveID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "pveID has no referent");
        }
        
        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName + 
                    "pveID doesn't refer to a PredicateVocabElement");
        }
        
        /* If we get this far, we know that dbe is a PredicateVocabElement */
        pve = (PredicateVocabElement)dbe;

        i = 0;
        matchFound = false;
        while ( ( i < pve.getNumFormalArgs() ) && ( ! matchFound ) )
        {
            if ( pve.getFormalArg(i).getID() == itsFargID )
            {
                matchFound = true;
            }
            i++;
        }
        
        if ( ! matchFound )
        {
            throw new SystemErrorException(mName + 
                    "Target pred's pve does not contain itsFarg");
        }
        
        this.itsPredID = ID;
        
        return;
        
    } /* DataValue::SetItsPredID() */
    
  
    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/
    
    /** 
     * insertInIndex()
     *
     * This method is called when the DataCell, part of whose value is stored in
     * this instance of DataValue, is first inserted in the database and becomes 
     * the first cannonical version of the DataCell.  It is also called if a 
     * new DataValue (i.e. that is a DataValue whose ID has not been assigned) 
     * appears in a new incarnation of the host DataCell.
     * 
     * The method makes note of the DataCell's ID, and inserts the DataValue
     * in the index.
     *
     * Note that PredDataValue must override this method so that its predicate
     * value can insert itself in the index.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "DataValue::insertInIndex(): ";
        
        // this call does all the sanity checking we need;
        this.setItsCellID(DCID);
        
        // for now at least, DataValue is a subclass of DBElement.  Thus we
        // must insert the DataValue in the index.
        
        this.db.idx.addElement(this);
        
        return;
        
    } /* DataValue::insertInIndex(DCID) */
    
    
    /** 
     * removeFromIndex()
     *
     * This method is called when the DataCell part of whose value is stored in
     * this instance of DataValue is deleted from the DataBase.  It is also
     * called if a DataValue is replaced with a new DataValue (i.e. that is 
     * a DataValue whose ID has not been assigned) in a new incarnation of the
     * host DataCell.
     * 
     * The method verifies that the supplied DataCell ID matches the one it 
     * has on file, and then removes itself from the index.
     *
     * Note that PredDataValue must override this method so that its predicate
     * value can remove itself in the index.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "DataValue::removeFromIndex(): ";
        
        if ( DCID != this.itsCellID )
        {
            throw new SystemErrorException(mName + "cell ID mismatch.");
        }
        
        // Remove the DataValue from the index.
        
        this.db.idx.removeElement(this.id);
        
        return;
        
    } /* DataValue::removeFromIndex(DCID) */
    
    
    /**
     * replaceInIndex()
     *
     * Update the index to point to this, the new incarnation of the DataValue.
     * The old incarnation is supplied as a parameter for sanity checking.
     *
     * Note that PredDataValue must override this method so as to pass an update
     * an update index for replacement message on to its Predicate.
     *
     *                                          JRM -- 2/20/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void replaceInIndex(DataValue old_dv,
                                  long DCID,
                                  boolean cascadeMveMod,
                                  boolean cascadeMveDel,
                                  long cascadeMveID,
                                  boolean cascadePveMod,
                                  boolean cascadePveDel,
                                  long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "DataValue::replaceInIndex(): ";
        
        if ( ( this.id != old_dv.id ) ||
             ( this.itsCellID != old_dv.itsCellID ) ||
             ( this.itsFargID != old_dv.itsFargID ) ||
             ( this.itsFargType != old_dv.itsFargType ) )
        {
            throw new SystemErrorException(mName + "mis-match with old_dv?!?");
        }
        
        if ( this.itsCellID != DCID )
        {
            throw new SystemErrorException(mName + "DCID mis-match!?!");
        }
        
        this.db.idx.replaceElement(this);
        
        return;
        
    } /* DataValue::replaceInIndex() */

    
//    /*************************************************************************/
//    /****************** Change Listener List Management: *********************/
//    /*************************************************************************/
//
//    /**
//     * addChangeListener()
//     *
//     * Add a change listener to this DataValue
//     *
//     * @param listener the change listener to add
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//  
//    public void addChangeListener(DataValueChangeListener listener)
//    {
//        this.changeListeners.add(listener);
//        
//        return;
//        
//    } /* DataValue::addChangeListener() */
//
//    /**
//     * removeChangeListener()
//     *
//     * Removes a change listener from this DataValue
//     *
//     * @param listener the change listener to remove
//     *
//     * Changes:
//     *
//     *    - None.
//     */
//  
//    public void removeChangeListener(DataValueChangeListener listener)
//    {
//        this.changeListeners.remove(listener);
//        
//        return;
//        
//    } /* DataValue::removeChangeListener() */
//
//    /**
//     * notifyListeners()
//     *
//     * Notifies the listeners that this DataValue has changed
//     *
//     * Changes:
//     *
//     *    - None
//     */
//  
//    protected void notifyListeners()
//    {
//        // Loop through vector calling listeners
//        for (int i=0; i<this.changeListeners.size(); i++) 
//        {
//            ((DataValueChangeListener)this.changeListeners.elementAt(i)).dataValueChanged(this);
//        }
//        
//        return;
//        
//    }  /* DataValue::notifyListeners() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Copy()
     *
     * Given a DataValue, return a copy.  Only minimal sanity checking.
     * Even less if blindCopy is true. 
     *
     *                                              JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static DataValue Copy(DataValue dv,
                                 boolean blindCopy)
        throws SystemErrorException
    {
        final String mName = "DataValue::CopyDataValue(): ";
        DataValue copy = null;
        
        if ( dv == null )
        {
            int i = 1/0;
            throw new SystemErrorException(mName + "dv null on entry.");
        }
        
        if ( dv instanceof ColPredDataValue )
        {
            copy = new ColPredDataValue((ColPredDataValue)dv);
        }
        else if ( dv instanceof FloatDataValue )
        {
            copy = new FloatDataValue((FloatDataValue)dv);
        }
        else if ( dv instanceof IntDataValue )
        {
            copy = new IntDataValue((IntDataValue)dv);
        }
        else if ( dv instanceof NominalDataValue )
        {
            copy = new NominalDataValue((NominalDataValue)dv);
        }
        else if ( dv instanceof PredDataValue )
        {
            copy = new PredDataValue((PredDataValue)dv, blindCopy);
        }
        else if ( dv instanceof QuoteStringDataValue )
        {
            copy = new QuoteStringDataValue((QuoteStringDataValue)dv);
        }
        else if ( dv instanceof TextStringDataValue )
        {
            copy = new TextStringDataValue((TextStringDataValue)dv);
        }
        else if ( dv instanceof TimeStampDataValue )
        {
            copy = new TimeStampDataValue((TimeStampDataValue)dv);
        }
        else if ( dv instanceof UndefinedDataValue )
        {
            copy = new UndefinedDataValue((UndefinedDataValue)dv);
        }
        else
        {
            throw new SystemErrorException(mName + 
                                           "Unknown data value sub-type");
        }
        
        return copy;
        
    } /* "DataValue::Copy() */
    
    
    /**
     * DataValuesAreLogicallyEqual()
     *
     * Given two instances of DataValue, return true if they contain identical
     * data, and false otherwise.
     *                                              JRM -- 2/7/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean DataValuesAreLogicallyEqual(DataValue dv0,
                                                      DataValue dv1)
        throws SystemErrorException
    {
        final String mName = "DataValue::DataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;
        
        if ( ( dv0 == null ) || ( dv1 == null ) )
        {
            throw new SystemErrorException(mName + ": dv0 or dv1 null on entry.");
        }
        
        if ( dv0 != dv1 )
        {
            if ( ( dv0.db != dv1.db ) ||
                 ( dv0.itsFargID != dv1.itsFargID ) ||
                 ( dv0.itsFargType != dv1.itsFargType ) ||
                 ( dv0.subRange != dv1.subRange ) )
            {
                dataValuesAreEqual = false;
            }
            else
            {
                if ( dv0 instanceof FloatDataValue )
                {
                    if ( ! ( dv1 instanceof FloatDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = FloatDataValue.
                           FloatDataValuesAreLogicallyEqual((FloatDataValue)dv0, 
                                                            (FloatDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof IntDataValue )
                {
                    if ( ! ( dv1 instanceof IntDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = IntDataValue.
                           IntDataValuesAreLogicallyEqual((IntDataValue)dv0, 
                                                          (IntDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof NominalDataValue )
                {
                    if ( ! ( dv1 instanceof NominalDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = NominalDataValue.
                                NominalDataValuesAreLogicallyEqual
                                        ((NominalDataValue)dv0, 
                                         (NominalDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof PredDataValue )
                {
                    if ( ! ( dv1 instanceof PredDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = PredDataValue.
                                PredDataValuesAreLogicallyEqual
                                        ((PredDataValue)dv0, 
                                         (PredDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof QuoteStringDataValue )
                {
                    if ( ! ( dv1 instanceof QuoteStringDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = QuoteStringDataValue.
                                QuoteStringDataValuesAreLogicallyEqual
                                        ((QuoteStringDataValue)dv0, 
                                         (QuoteStringDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof TextStringDataValue )
                {
                    if ( ! ( dv1 instanceof TextStringDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = TextStringDataValue.
                                TextStringDataValuesAreLogicallyEqual
                                        ((TextStringDataValue)dv0, 
                                         (TextStringDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof TimeStampDataValue )
                {
                    if ( ! ( dv1 instanceof TimeStampDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = TimeStampDataValue.
                                TimeStampDataValuesAreLogicallyEqual
                                        ((TimeStampDataValue)dv0, 
                                         (TimeStampDataValue)dv1);    
                    }
                }
                else if ( dv0 instanceof UndefinedDataValue )
                {
                    if ( ! ( dv1 instanceof UndefinedDataValue ) )
                    {
                        dataValuesAreEqual = false;
                    }
                    else 
                    {
                        dataValuesAreEqual = UndefinedDataValue.
                                UndefinedDataValuesAreLogicallyEqual
                                        ((UndefinedDataValue)dv0, 
                                         (UndefinedDataValue)dv1);    
                    }
                }
                else
                {
                    throw new SystemErrorException(mName + 
                            ": unknown DataValue subtype.");
                }
            }
            
        }
            
        return dataValuesAreEqual;
        
    } /* DataValue::DataValuesAreLogicallyEqual() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * TestAccessors()
     *
     * Verify that the accessors defined in this abstract class perform
     * as expected in the supplied instance of some subclass.
     *
     * The test assumes that:
     * 
     * 1) The itsFargID field has been set to match the id of fa0.
     *
     * 2) The itsCellID field has not been assigned.
     *
     * 3) fa0 and fa1 are of different types, and that dv is compatible
     *    with both. (In practice, this means that fa0 is typed to accept
     *    DataValues of type equal to that of dv, and that fa1 is an untyped
     *    formal argument, or vise versa.)
     *
     *    Note that there is one exception to this assumption -- if dv is a
     *    TextStringDataValue, then both fa0 and fa1 must be instances of
     *    TextStringFormalArg
     *
     * 4) mve1 is a single element matrix vocab element, and fa1 is its single 
     *    formal argument.  If dv is an instance of TextStringDataValue, 
     *    mve1 must be of type TEXT.  Otherwise, mve1 must be either of
     *    of type MATRIX or of type matching dv.
     *
     *                                          JRM -- 11/14/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestAccessors(Database db,
                                    FormalArgument fa0,
                                    MatrixVocabElement mve1,
                                    FormalArgument fa1,
                                    DataValue dv,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long cellID = DBIndex.INVALID_ID;
        DataColumn col = null;
        DataCell dc = null;
        
        if ( db == null )
        {
            failures++;
            outStream.printf("TestAccessors: db null on entry.\n");
        }
        
        if ( fa0 == null )
        {
            failures++;
            outStream.printf("TestAccessors: fa0 null on entry.\n");
        }
        
        if ( fa1 == null )
        {
            failures++;
            outStream.printf("TestAccessors: fa1 null on entry.\n");
        }
        
        if ( dv == null )
        {
            failures++;
            outStream.printf("TestAccessors: dv null on entry.\n");
        }
        
        if ( dv.getItsFargID() != fa0.getID() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.getItsFargID() != fa0.getID().\n");
            }
        }
        
        if ( dv.getItsFargType() != fa0.getFargType() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.getItsFargType() != fa0.getFargType().\n");
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            dv.setItsFargID(fa1.getID());
            
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( ! completed ) || 
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf(
                            "dv.setItsFargID() failed to completed.\n");
                }
            }
                
            if ( threwSystemErrorException )
            {
                outStream.printf("dv.setItsFargID() threw a system error " +
                                 "exception: \"%s\"", 
                                 systemErrorExceptionString);
            }
        }
        else
        {
            if ( dv.getItsFargID() != fa1.getID() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("dv.getItsFargID() != fa1.getID().\n");
                }
            }

            if ( dv.getItsFargType() != fa1.getFargType() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "dv.getItsFargType() != fa1.getFargType().\n");
                }
            }
        }
        
        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.itsCellID != DBIndex.INVALID_ID.\n");
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            // it is possible this method has been called before with the
            // supplied mve -- if so, we have already created a column for
            // it, and trying to create it again will throw a system error.
            if ( db.cl.inColumnList(mve1.getName()) )
            {
                col = db.getDataColumn(mve1.getName());
            }
            else
            {
                col = new DataColumn(db, mve1.getName(), false, false, mve1.getID());
            
                db.cl.addColumn(col);
            }

            dc = new DataCell(db, col.getID(), mve1.getID());
            
            cellID = db.appendCell(dc);
            
            dv.setItsCellID(cellID);
            
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( col == null ) ||
             ( dc == null ) ||
             ( cellID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( col == null )
                {
                    outStream.printf("DataColumn allocation failed.\n");
                }
                
                if ( dc == null )
                {
                    outStream.printf("DataCell allocation failed.\n");
                }
                
                if ( cellID == DBIndex.INVALID_ID )
                {
                    outStream.printf("*cellID is INVALID.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "setItsCellID() test failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("setItsCellID() test threw a system " +
                                     "error exception: \"%s\"", 
                                     systemErrorExceptionString);
                }
            }
        }
        else
        {
            if ( dv.itsCellID != cellID )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("dv.itsCellID != cellID.\n");
                }
            }
        }
        
        return failures;
    
    } /* DataValue::TestAccessors() */
    
    
    /**
     * Verify1ArgInitialization()
     *
     * Verify that the supplied instance of DataValue has been correctly 
     * initialized by a one argument constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int Verify1ArgInitialization(Database db,
                                               DataValue dv,
                                               java.io.PrintStream outStream,
                                               boolean verbose)
    {
        int failures = 0;
        
        if ( db == null )
        {
            failures++;
            outStream.printf("Verify1ArgInitialization: db null on entry.\n");
        }
        
        if ( dv == null )
        {
            failures++;
            outStream.printf("Verify1ArgInitialization: dv null on entry.\n");
        }

        if ( dv.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("dv.db not initialized correctly.\n");
            }
        }
        
        if ( dv.id != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.id not initialized corectly: %d.\n",
                                 dv.id);
            }
        }
        
        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsCellID not initialized corectly: %d.\n",
                        dv.itsCellID);
            }
        }
        
        if ( dv.itsFargID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsFargID not initialized correctly: %d.\n",
                        dv.itsFargID);
            }
        }
        
        if ( dv.itsFargType != FormalArgument.fArgType.UNDEFINED )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsFargType not initialized correctly.\n");
            }
        }
        
        if ( dv.lastModUID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.lastModUID not initialized correctly: %d.\n",
                        dv.lastModUID);
            }
        }
                
        if ( dv.subRange )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.subRange not set to false.\n");
            }
        }
                
        return failures;
        
    } /* DataValue::Verify1ArgInitialization() */

    
    /**
     * Verify2ArgInitialization()
     *
     * Verify that the supplied instance of DataValue has been correctly 
     * initialized by a two or more argument constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int Verify2PlusArgInitialization(Database db,
                                                   FormalArgument fa,
                                                   DataValue dv,
                                                   java.io.PrintStream outStream,
                                                   boolean verbose,
                                                   String dvDesc)
        throws SystemErrorException
    {
        int failures = 0;
        
        if ( dv == null )
        {
            failures++;
            outStream.printf(
                    "Verify2PlusArgInitialization: dv null on entry.\n");
        }
        
        if ( fa == null )
        {
            failures++;
            outStream.printf(
                    "Verify2PlusArgInitialization: fa null on entry.\n");
        }
        else if ( fa.getID() == DBIndex.INVALID_ID )
        {
            failures++;
            outStream.printf("Verify2PlusArgInitialization: fa.getID() " +
                             "returns invalid ID.\n");
        }

        if ( dv.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db not initialized correctly.\n", dvDesc);
            }
        }
        
        if ( dv.id != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.id not initialized corectly: %d.\n",
                                 dvDesc, dv.id);
            }
        }
        
        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsCellID not initialized corectly: %d.\n",
                        dvDesc, dv.itsCellID);
            }
        }
        
        if ( dv.itsFargID != fa.getID() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsFargID not initialized correctly: %d(%d).\n",
                        dvDesc, dv.itsFargID, fa.getID());
            }
        }
        
        if ( dv.itsFargType != fa.getFargType() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsFargType not initialized correctly.\n", 
                                 dvDesc);
            }
        }
        
        if ( dv.lastModUID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.lastModUID not initialized correctly: %d.\n",
                        dvDesc, dv.lastModUID);
            }
        }
                
        return failures;
        
    } /* DataValue::Verify2ArgInitialization() */

    
    /**
     * VerifyDVCopy()
     *
     * Verify that the supplied instances of DataValue are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     * Note that we do not compare the itsCell and itsCellID.
     *
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */
    
    protected static int VerifyDVCopy(DataValue base,
                                      DataValue copy,
                                      java.io.PrintStream outStream,
                                      boolean verbose,
                                      String baseDesc,
                                      String copyDesc)
    {
        int failures = 0;
        
        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDVCopy: %s null on entry.\n", baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDVCopy: %s null on entry.\n", copyDesc);
        }
        else if ( base == copy )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.db != copy.db )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.itsFargID != copy.itsFargID )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsFargID != %s.itsFargID.\n", 
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.itsFargType != copy.itsFargType )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsFargType != %s.itsFargType.\n", 
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.subRange != copy.subRange )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsFargType != %s.itsFargType.\n", 
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n", 
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n", 
                        baseDesc, copyDesc);
                outStream.printf(
                        "%s.toDBString() = \"%s\".\n%s.toDBString() = \"%s\".\n", 
                        baseDesc, base.toDBString(), 
                        copyDesc, copy.toDBString());
            }
        }
        else
        {
            if ( base instanceof ColPredDataValue )
            {
                if ( ! ( copy instanceof ColPredDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a ColPredDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        ColPredDataValue.
                            VerifyColPredDVCopy((ColPredDataValue)base,
                                                (ColPredDataValue)copy,
                                                outStream,
                                                verbose,
                                                baseDesc,
                                                copyDesc);    
                }
            }
            else if ( base instanceof FloatDataValue )
            {
                if ( ! ( copy instanceof FloatDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a FloatDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        FloatDataValue.VerifyFloatDVCopy((FloatDataValue)base,
                                                         (FloatDataValue)copy,
                                                         outStream,
                                                         verbose,
                                                         baseDesc,
                                                         copyDesc);    
                }
            }
            else if ( base instanceof IntDataValue )
            {
                if ( ! ( copy instanceof IntDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a IntDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        IntDataValue.VerifyIntDVCopy((IntDataValue)base,
                                                     (IntDataValue)copy,
                                                     outStream,
                                                     verbose,
                                                     baseDesc,
                                                     copyDesc);    
                }
            }
            else if ( base instanceof NominalDataValue )
            {
                if ( ! ( copy instanceof NominalDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a NominalDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        NominalDataValue.
                            VerifyNominalDVCopy((NominalDataValue)base,
                                                (NominalDataValue)copy,
                                                outStream,
                                                verbose,
                                                baseDesc,
                                                copyDesc);    
                }
            }
            else if ( base instanceof PredDataValue )
            {
                if ( ! ( copy instanceof PredDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a PredDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        PredDataValue.
                            VerifyPredDVCopy((PredDataValue)base,
                                             (PredDataValue)copy,
                                             outStream,
                                             verbose,
                                             baseDesc,
                                             copyDesc);    
                }
            }
            else if ( base instanceof QuoteStringDataValue )
            {
                if ( ! ( copy instanceof QuoteStringDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a QuoteStringDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        QuoteStringDataValue.
                            VerifyQuoteStringDVCopy((QuoteStringDataValue)base,
                                                    (QuoteStringDataValue)copy,
                                                    outStream,
                                                    verbose,
                                                    baseDesc,
                                                    copyDesc);    
                }
            }
            else if ( base instanceof TextStringDataValue )
            {
                if ( ! ( copy instanceof TextStringDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a TextStringDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        TextStringDataValue.
                            VerifyTextStringDVCopy((TextStringDataValue)base,
                                                   (TextStringDataValue)copy,
                                                   outStream,
                                                   verbose,
                                                   baseDesc,
                                                   copyDesc);    
                }
            }
            else if ( base instanceof TimeStampDataValue )
            {
                if ( ! ( copy instanceof TimeStampDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a TimeStampDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        TimeStampDataValue.
                            VerifyTimeStampDVCopy((TimeStampDataValue)base,
                                                  (TimeStampDataValue)copy,
                                                  outStream,
                                                  verbose,
                                                  baseDesc,
                                                  copyDesc);    
                }
            }
            else if ( base instanceof UndefinedDataValue )
            {
                if ( ! ( copy instanceof UndefinedDataValue ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a UndefinedDataValue but %s is not.\n", 
                                baseDesc, copyDesc);
                    }
                }
                else 
                {
                    failures += 
                        UndefinedDataValue.
                            VerifyUndefinedDVCopy((UndefinedDataValue)base,
                                                  (UndefinedDataValue)copy,
                                                  outStream,
                                                  verbose,
                                                  baseDesc,
                                                  copyDesc);    
                }
            }
            else
            {
                failures++;
                outStream.printf("%s is a DataValue of unknown type.\n",
                                 baseDesc);
            }
        }
        
        return failures;
        
    } /* DataValue::VerifyDVCopy() */

} //End of DataValue class definition
